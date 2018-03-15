package org.yop.android.sql.adapter.android;

import android.database.Cursor;
import org.apache.commons.lang.StringUtils;
import org.yop.orm.sql.Query;
import org.yop.orm.sql.adapter.IResultCursor;
import org.yop.orm.transform.ITransformer;

import java.sql.SQLException;

/**
 * Android resultset adapter (~ {@link Cursor}.
 */
public class SQLiteCursor implements IResultCursor {

    private Cursor cursor;
    private Query query;

    SQLiteCursor(Cursor cursor, Query query) {
        this.cursor = cursor;
        this.query = query;
    }

    @Override
    public boolean hasColumn(String columnName) {
        for (String column : this.cursor.getColumnNames()) {
            if (StringUtils.equals(columnName, column)) return true;
        }
        return false;
    }

    @Override
    public String getColumnName(int i) {
        return this.cursor.getColumnName(i);
    }

    @Override
    public int getColumnCount() {
        return this.cursor.getColumnCount();
    }

    @Override
    public Long getLong(String columnName) {
        return this.cursor.getLong(this.cursor.getColumnIndex(columnName));
    }

    @Override
    public Long getLong(int columnIndex) {
        return this.cursor.getLong(columnIndex);
    }

    @Override
    public Object getObject(String columnName) {
        int columnIndex = this.cursor.getColumnIndex(columnName);
        switch (this.cursor.getType(columnIndex)) {
            case Cursor.FIELD_TYPE_BLOB :   return this.cursor.getBlob(columnIndex);
            case Cursor.FIELD_TYPE_FLOAT:   return this.cursor.getFloat(columnIndex);
            case Cursor.FIELD_TYPE_INTEGER: return this.cursor.getInt(columnIndex);
            case Cursor.FIELD_TYPE_STRING:  return this.cursor.getString(columnIndex);
            case Cursor.FIELD_TYPE_NULL:
            default: return null;
        }
    }

    @Override
    public Object getObject(String columnName, Class<?> aClass) {
        return ITransformer.fallbackTransformer().fromSQL(this.getObject(columnName), aClass);
    }

    @Override
    public boolean next() {
        return this.cursor.moveToNext();
    }

    @Override
    public void close() throws SQLException {
        this.cursor.close();
    }
}
