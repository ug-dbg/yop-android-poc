package org.yop.android;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.yop.android.model.RelatedToSample;
import org.yop.android.model.Sample;
import org.yop.android.sql.adapter.android.SQLiteConnection;
import org.yop.android.sql.handler.SQLHandler;
import org.yop.orm.evaluation.Operator;
import org.yop.orm.exception.YopRuntimeException;
import org.yop.orm.query.JoinSet;
import org.yop.orm.query.Select;
import org.yop.orm.query.Upsert;
import org.yop.orm.query.Where;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A simple activity that performs some very basic CRUD
 * on {@link org.yop.android.model}
 * using {@link SQLHandler}.
 * <br>
 * <br>
 * I'm sorry, this is some very poor Android application design.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "YOP_Android_POC#Activity";
    private SQLHandler sqlHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Giving the application context and the Yopable classes to the handler :
        // Any existing database is deleted and the Yopable tables are created.
        // YOLO !
        this.sqlHandler = new SQLHandler(
            this.getApplicationContext(),
            Arrays.asList(Sample.class, RelatedToSample.class)
        );

        // Yeah, I use the textview to display some tests result. This is my POC. Sue me.
        TextView infoText = findViewById(R.id.info);
        infoText.append("Doing some CRUD tests..." + "\n\n");
        this.doSomeBasicCRUD(infoText);
        infoText.append("\n" + "CRUD tests went well !");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    /**
     * Do some CRUD operations on the {@link org.yop.android.model} objects.
     * <br>
     * Come on, it's not that ugly !
     * @param infoText the textview to hem... print some stuffs.
     */
    private void doSomeBasicCRUD(TextView infoText) {
        try (
            SQLiteConnection readonlyConnection  = this.sqlHandler.getReadConnection();
            SQLiteConnection writeableConnection = this.sqlHandler.getWriteConnection()) {

            Sample sample = new Sample("My sample from Android !", LocalDateTime.now());
            RelatedToSample related = new RelatedToSample(
                1.1,
                "related to sample#1",
                "do I really need a comment ?"
            );
            sample.getRelated().add(related);

            Log.i(TAG, "Inserting [" + sample + "] into database !");
            infoText.append("Inserting [" + sample + "] into database !" + "\n");
            Upsert.from(Sample.class).onto(sample).joinAll().execute(writeableConnection);
            doAssert( sample.getId() != null);

            Set<Sample> samples = Select.from(Sample.class).joinAll().execute(readonlyConnection);
            logSample(samples, infoText);
            doAssert( samples.size() == 1);
            doAssert( samples.iterator().next().getRelated().size() == 1);

            samples = Select
                .from(Sample.class)
                .join(JoinSet
                    .to(Sample::getRelated)
                    // It seems like using 'float' fields instead of 'double' does not work for this
                    .where(Where.compare(RelatedToSample::getRate, Operator.EQ, 1.1))
                )
                .execute(readonlyConnection);
            logSample(samples, infoText);
            doAssert(samples.size() == 1);
            doAssert(samples.iterator().next().getRelated().size() == 1);

            sample.getRelated().clear();
            Log.i(TAG, "Updating [" + sample + "] into database (cutting relation) !");
            infoText.append("Updating [" + sample + "] into database (cutting relation) !" + "\n");
            Upsert.from(Sample.class).onto(sample).joinAll().execute(writeableConnection);

            samples = Select.from(Sample.class).joinAll().execute(readonlyConnection);
            logSample(samples, infoText);
            doAssert( samples.size() == 1);
            doAssert( samples.iterator().next().getRelated().size() == 0);

            try (Cursor cursor = this.sqlHandler
                .getReadableDatabase()
                .rawQuery("SELECT count(*) FROM rel_sample_related", new String[0])) {

                if (cursor.moveToFirst()) {
                   doAssert(cursor.getLong(0) == 0);
                   Log.i(TAG, "Relation was deleted on cascade :-)");
                   infoText.append("Relation was deleted on cascade :-)" + "\n");
                } else {
                    throw new YopRuntimeException("Assertion failed !");
                }
            }
        }
    }

    private static void logSample(Collection<Sample> samples, TextView infoText) {
        Log.i(TAG, "Found [" + samples.size() + "] samples !");
        infoText.append("Found [" + samples.size() + "] samples !" + "\n");
        if (samples.size() == 1) {
            Sample fromDB = samples.iterator().next();
            Log.i(TAG, "Found sample from DB [" + fromDB + "]");
            infoText.append("Found sample from DB [" + fromDB + "]" + "\n");

            List<RelatedToSample> relatedsFromDB = fromDB.getRelated();
            Log.i(
                TAG,
                "Sample from DB #[" + fromDB.getId()
                + "] has [" + relatedsFromDB.size()
                + "] related element(s) !"
            );

            infoText.append(
                "Sample from DB #[" + fromDB.getId()
                + "] has [" + relatedsFromDB.size()
                + "] related elements !"
                + "\n"
            );

            if (relatedsFromDB.size() == 1) {
                RelatedToSample relatedFromDB = relatedsFromDB.iterator().next();
                Log.i(TAG, "Related to sample from DB [" + relatedFromDB + "]");
                infoText.append("Related to sample from DB [" + relatedFromDB + "]" + "\n");
            }
        }
    }

    private static void doAssert(boolean condition) {
        if (!condition) {
            throw new YopRuntimeException("Assertion failed !");
        }
    }
}
