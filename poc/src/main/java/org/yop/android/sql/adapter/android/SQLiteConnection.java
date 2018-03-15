package org.yop.android.sql.adapter.android;

import android.database.sqlite.SQLiteDatabase;
import org.yop.orm.sql.Query;
import org.yop.orm.sql.adapter.IConnection;
import org.yop.orm.sql.adapter.IRequest;

/**
 * Android connection to database adapter.
 */
public class SQLiteConnection implements IConnection {

    private SQLiteDatabase db;

    public SQLiteConnection(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public IRequest prepare(Query query) {
        return new SQLiteRequest(query, this.db);
    }

    @Override
    public void close() {
        this.db.close();
    }

    @Override
    public void setAutoCommit(boolean b) {
        if(!b) {
            this.db.beginTransaction();
        }
    }

    @Override
    public void commit() {
        this.db.setTransactionSuccessful();
        this.db.endTransaction();
    }
}
