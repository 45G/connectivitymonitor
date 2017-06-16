package com.a45g.athena.connectivitymonitor;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseOperations {
    private SQLiteDatabase mDatabaseUpdate = null;
    private SQLiteUpdateHelper mDatabaseUpdateHelper = null;
    private int mNumUpdateOpened;

    private String tag = "DatabaseOperations";

    private String[] allConnectivityColumns = {
            SQLiteUpdateHelper.COLUMN_ID, SQLiteUpdateHelper.COLUMN_TIMESTAMP,
            SQLiteUpdateHelper.COLUMN_INTERFACE, SQLiteUpdateHelper.COLUMN_EVENT,
            SQLiteUpdateHelper.COLUMN_DETAILS
    };

    public DatabaseOperations(Context context) {
        mDatabaseUpdateHelper = new SQLiteUpdateHelper(context);
        mNumUpdateOpened = 0;
    }

    public synchronized void openWrite() {
        if (mDatabaseUpdateHelper != null) {
            if (mDatabaseUpdate == null) {
                try {
                    mDatabaseUpdate = mDatabaseUpdateHelper.getWritableDatabase();
                } catch (SQLException sqlException) {
                    Log.e(tag, "SQL exception thrown while trying to get writable database",
                            sqlException);
                    // TODO: Do something in this case.
                }
            }
            mNumUpdateOpened++;
        }
    }

    public synchronized void close() {
        if (mDatabaseUpdateHelper != null && mDatabaseUpdate != null && mNumUpdateOpened > 0) {
            mNumUpdateOpened--;
            if (mNumUpdateOpened == 0) {
                mDatabaseUpdateHelper.close();
                mDatabaseUpdate = null;
            }
        } else {
            mNumUpdateOpened = 0;
            mDatabaseUpdate = null;
        }
    }

    public long insertConnectivityEvent(String timestamp, String iface, String event, String details) {
        if (mDatabaseUpdate == null) {
            Log.e(tag, "Insert with database closed.");
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(SQLiteUpdateHelper.COLUMN_TIMESTAMP, timestamp);
        values.put(SQLiteUpdateHelper.COLUMN_INTERFACE, iface);
        values.put(SQLiteUpdateHelper.COLUMN_EVENT, event);
        values.put(SQLiteUpdateHelper.COLUMN_DETAILS, details);

        return mDatabaseUpdate.insert(SQLiteUpdateHelper.TABLE_CONNECTIVITY, null, values);
    }

    public List<ConnectivityOutput> getAllResults() {
        if (mDatabaseUpdate == null) {
            Log.e(tag, "getAllResults: Query with database closed.");
            return null;
        }

        List<ConnectivityOutput> outputs = new ArrayList<ConnectivityOutput>();
        Cursor cursor = mDatabaseUpdate.query(SQLiteUpdateHelper.TABLE_CONNECTIVITY, allConnectivityColumns,
                null, null, null, null, null);

        try {
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    ConnectivityOutput output = cursorToConnectivityOutput(cursor);
                    outputs.add(output);
                    cursor.moveToNext();
                }
            }
        } finally {
            if (cursor != null){
                cursor.close();
            }
        }

        return outputs;
    }

    private ConnectivityOutput cursorToConnectivityOutput(Cursor cursor) {
        return new ConnectivityOutput(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4));
    }




}
