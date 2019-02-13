package org.yop.android.sql.handler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.yop.android.sql.adapter.android.SQLiteConnection;
import org.yop.orm.gen.Table;
import org.yop.orm.sql.Config;
import org.yop.orm.sql.Executor;
import org.yop.orm.sql.Query;
import org.yop.orm.sql.SimpleQuery;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * SQLite database handler.
 * Set the Yopables and {@link #onCreate(SQLiteDatabase)}
 */
public class SQLHandler extends SQLiteOpenHelper {

    private static final String TAG = "YOP_Android_POC#SQLHandler";
    private static final String DB_NAME = "tracker";
    private final Set<Class> yopables = new HashSet<>();

    public SQLHandler(Context context, Collection<Class> yopables) {
        super(context, DB_NAME, null, 1);

        this.yopables.clear();
        this.yopables.addAll(yopables);

        context.deleteDatabase(DB_NAME);
        Log.i(TAG, "Created SQL handler for database [" + this.getDatabaseName() + "]");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(SQLiteDatabase db) {
        SQLiteConnection connection = new SQLiteConnection(db);
        Config config = connection.config();
        Log.i(TAG, "Database [" + db.getPath() + "] created !");

        Set<Table> tables = new HashSet<>();
        this.yopables.forEach(yopable -> tables.addAll(Table.findTablesFor(yopable, config)));

        for (Table table : tables) {
            try {
                SimpleQuery query = new SimpleQuery(table.toSQL(), Query.Type.INSERT, config);
                Log.i(TAG,"Executing SQL script for table [" + table.qualifiedName() + "]");
                Executor.executeQuery(connection, query);
            } catch (RuntimeException e) {
                Log.w(TAG,"Error executing script line [" + table.toSQL() + "]", e);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public SQLiteConnection getWriteConnection() {
        return new SQLiteConnection(this.getWritableDatabase());
    }

    public SQLiteConnection getReadConnection() {
        return new SQLiteConnection(this.getWritableDatabase());
    }
}