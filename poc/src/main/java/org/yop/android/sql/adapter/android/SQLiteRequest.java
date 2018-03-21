package org.yop.android.sql.adapter.android;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.yop.orm.sql.Parameters;
import org.yop.orm.sql.Query;
import org.yop.orm.sql.adapter.IRequest;
import org.yop.orm.sql.adapter.IResultCursor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Android SQL statement adapter (kinda).
 */
public class SQLiteRequest implements IRequest {

    private static final String TAG = "YOP_Android_POC#SQLiteRequest";

    private Query query;
    private SQLiteDatabase db;

    SQLiteRequest(Query query, SQLiteDatabase db) {
        this.query = query;
        this.db = db;
    }

    @Override
    public Query getQuery() {
        return this.query;
    }

    @Override
    public IResultCursor execute() {
        List<String> parameters = new ArrayList<>();
        for (Parameters.Parameter parameter : this.query.getParameters()) {
            parameters.add(
                parameter.getValue() == null ? null : String.valueOf(parameter.getValue())
            );
        }

        Log.i(TAG, "Query : [" + this.query.getSafeSql() + "]");
        Log.i(TAG, "Query parameters : " + parameters);
        Cursor cursor = this.db.rawQuery(this.query.getSafeSql(), parameters.toArray(new String[0]));
        return new SQLiteCursor(cursor, this.query);
    }

    @Override
    public void executeUpdate() {
        SQLiteStatement sqLiteStatement = this.db.compileStatement(this.query.getSafeSql());

        while (this.query.nextBatch()) {
            for (int i = 0; i < this.getQuery().getParameters().size(); i++) {
                bind(sqLiteStatement, i + 1, this.query.getParameters().get(i));
            }

            Log.i(TAG, "Query : [" + this.query.getSafeSql() + "]");
            Log.i(TAG, "Query parameters : " + this.query.getParameters());

            Long inserted = sqLiteStatement.executeInsert();
            if(inserted != -1) {
                this.query.getGeneratedIds().add(inserted);
            }
        }
    }

    @Override
    public void close() throws SQLException {}

    /**
     * There is apparently no SQLiteStatement#bindObject method.
     * So this a DIY parameter binding method.
     * @param statement the statement on which the parameter is to bind.
     * @param at        the parameter position (1-based)
     * @param parameter the parameter tot bind
     */
    private static void bind(SQLiteStatement statement, int at, Parameters.Parameter parameter) {
        Object value = parameter.getValue();
        String logMessage = "Binding [" + value + "] at position [" + at + "]";
        Log.v(TAG, logMessage);
        if(value == null) {
            statement.bindNull(at);
            return;
        }

        Class<?> clazz = value.getClass();
        Package pack = clazz.getPackage();

        try {
            Double casted = Double.class.cast(value);
            statement.bindDouble(at, casted);
            return;
        } catch (ClassCastException e) {
            Log.v(TAG,logMessage);
        }

        try {
            Float casted = Float.class.cast(value);
            statement.bindDouble(at, casted);
            return;
        } catch (ClassCastException e) {
            Log.v(TAG,logMessage);
        }

        try {
            Long casted = Long.class.cast(value);
            statement.bindLong(at, casted);
            return;
        } catch (ClassCastException e) {
            Log.v(TAG,logMessage);
        }

        try {
            Integer casted = Integer.class.cast(value);
            statement.bindLong(at, casted);
            return;
        } catch (ClassCastException e) {
            Log.v(TAG,logMessage);
        }

        try {
            Short casted = Short.class.cast(value);
            statement.bindLong(at, casted);
            return;
        } catch (ClassCastException e) {
            Log.v(TAG,logMessage);
        }

        try {
            Byte casted = Byte.class.cast(value);
            statement.bindLong(at, casted);
            return;
        } catch (ClassCastException e) {
            Log.v(TAG,logMessage);
        }

        try {
            String casted = String.class.cast(value);
            statement.bindString(at, casted);
            return;
        } catch (ClassCastException e) {
            Log.v(TAG,logMessage);
        }

        if(value instanceof byte[]) {
            statement.bindBlob(at, (byte[]) value);
            return;
        }

        if(pack.getName().startsWith("java.time")) {
            statement.bindString(at, value.toString());
        }
    }
}
