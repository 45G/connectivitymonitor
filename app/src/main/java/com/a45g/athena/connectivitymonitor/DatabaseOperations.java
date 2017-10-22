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
    private static final String LOG_TAG = "DatabaseOperations";
    private SQLiteDatabase mDatabaseUpdate = null;
    private SQLiteUpdateHelper mDatabaseUpdateHelper = null;
    private int mNumUpdateOpened;

    private String[] allConnectivityColumns = {
            SQLiteUpdateHelper.COLUMN_ID, SQLiteUpdateHelper.COLUMN_TIMESTAMP,
            SQLiteUpdateHelper.COLUMN_INTERFACE, SQLiteUpdateHelper.COLUMN_EVENT,
            SQLiteUpdateHelper.COLUMN_DETAILS
    };

    private String[] allTestsColumns = {
            SQLiteUpdateHelper.COLUMN_ID, SQLiteUpdateHelper.COLUMN_TIMESTAMP,
            SQLiteUpdateHelper.COLUMN_TYPE, SQLiteUpdateHelper.COLUMN_VALUE
    };

    private String[] allCollectedDataColumns = {
            SQLiteUpdateHelper.COLUMN_ID, SQLiteUpdateHelper.COLUMN_TIMESTAMP,
            SQLiteUpdateHelper.COLUMN_RX_WLAN, SQLiteUpdateHelper.COLUMN_RX_LTE,
            SQLiteUpdateHelper.COLUMN_TX_WLAN, SQLiteUpdateHelper.COLUMN_TX_LTE,
            SQLiteUpdateHelper.COLUMN_RSSI_WLAN, SQLiteUpdateHelper.COLUMN_RSSI_LTE,
            SQLiteUpdateHelper.COLUMN_RTT_WLAN, SQLiteUpdateHelper.COLUMN_RTT_LTE
    };

    private String[] idColumn = {
            SQLiteUpdateHelper.COLUMN_ID
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
                    Log.e(LOG_TAG, "SQL exception thrown while trying to get writable database",
                            sqlException);
                    // TODO: Do something in this case.
                }
            }
            mNumUpdateOpened++;
        }
    }

    public synchronized void openRead() {
        if (mDatabaseUpdateHelper != null) {
            if (mDatabaseUpdate == null) {
                try {
                    mDatabaseUpdate = mDatabaseUpdateHelper.getReadableDatabase();
                } catch (SQLException sqlException) {
                    Log.e(LOG_TAG, "SQL exception thrown while trying to get readable database",
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
            Log.e(LOG_TAG, "Insert with database closed.");
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(SQLiteUpdateHelper.COLUMN_TIMESTAMP, timestamp);
        values.put(SQLiteUpdateHelper.COLUMN_INTERFACE, iface);
        values.put(SQLiteUpdateHelper.COLUMN_EVENT, event);
        values.put(SQLiteUpdateHelper.COLUMN_DETAILS, details);

        return mDatabaseUpdate.insert(SQLiteUpdateHelper.TABLE_CONNECTIVITY, null, values);
    }

    public long insertTestResult(String timestamp, String type, String value) {
        if (mDatabaseUpdate == null) {
            Log.e(LOG_TAG, "Insert with database closed.");
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(SQLiteUpdateHelper.COLUMN_TIMESTAMP, timestamp);
        values.put(SQLiteUpdateHelper.COLUMN_TYPE, type);
        values.put(SQLiteUpdateHelper.COLUMN_VALUE, value);

        return mDatabaseUpdate.insert(SQLiteUpdateHelper.TABLE_TESTS, null, values);
    }

    public long insertCollectedData(String timestamp, String rx_wlan, String rx_lte,
                                    String tx_wlan, String tx_lte, String rssi_wlan,
                                    String rssi_lte, String rtt_wlan, String rtt_lte) {
        if (mDatabaseUpdate == null) {
            Log.e(LOG_TAG, "Insert with database closed.");
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(SQLiteUpdateHelper.COLUMN_TIMESTAMP, timestamp);
        values.put(SQLiteUpdateHelper.COLUMN_RX_WLAN, rx_wlan);
        values.put(SQLiteUpdateHelper.COLUMN_RX_LTE, rx_lte);
        values.put(SQLiteUpdateHelper.COLUMN_TX_WLAN, tx_wlan);
        values.put(SQLiteUpdateHelper.COLUMN_TX_LTE, tx_lte);
        values.put(SQLiteUpdateHelper.COLUMN_RSSI_WLAN, rssi_wlan);
        values.put(SQLiteUpdateHelper.COLUMN_RSSI_LTE, rssi_lte);
        values.put(SQLiteUpdateHelper.COLUMN_RTT_WLAN, rtt_wlan);
        values.put(SQLiteUpdateHelper.COLUMN_RTT_LTE, rtt_lte);

        return mDatabaseUpdate.insert(SQLiteUpdateHelper.TABLE_COLLECTED_DATA, null, values);
    }

    public List<ConnectivityOutput> getAllConnectivityOutputs() {
        if (mDatabaseUpdate == null) {
            Log.e(LOG_TAG, "getAllResults: Query with database closed.");
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

    public List<ConnectivityOutput> getRecentConnectivityOutputs(long id) {
        if (mDatabaseUpdate == null) {
            Log.e(LOG_TAG, "getAllResults: Query with database closed.");
            return null;
        }

        List<ConnectivityOutput> outputs = new ArrayList<ConnectivityOutput>();
        String whereClause = SQLiteUpdateHelper.COLUMN_ID + " > '" + id + "'";
        Cursor cursor = mDatabaseUpdate.query(SQLiteUpdateHelper.TABLE_CONNECTIVITY, allConnectivityColumns,
                whereClause, null, null, null, null);

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

    public ConnectivityOutput getConnectivityOutputById(long id) {
        if (mDatabaseUpdate == null) {
            Log.e(LOG_TAG, "getConnectivityOutputById: Query with database closed.");
            return null;
        }


        String whereClause = SQLiteUpdateHelper.COLUMN_ID + " = '" + id + "'";
        Cursor cursor = mDatabaseUpdate.query(SQLiteUpdateHelper.TABLE_CONNECTIVITY, allConnectivityColumns,
                whereClause, null, null, null, null);


        ConnectivityOutput output = null;
        try {
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToLast();
                    output = cursorToConnectivityOutput(cursor);
                }
            }
        } finally {
            if (cursor != null){
                cursor.close();
            }
        }

        return output;
    }

    public List<ConnectivityOutput> getConnectivityOutputByTime(String timestamp) {
        if (mDatabaseUpdate == null) {
            Log.e(LOG_TAG, "getConnectivityOutputByTime: Query with database closed.");
            return null;
        }

        String whereClause = SQLiteUpdateHelper.COLUMN_TIMESTAMP + " >= '" + timestamp + "'";
        Cursor cursor = mDatabaseUpdate.query(SQLiteUpdateHelper.TABLE_CONNECTIVITY, allConnectivityColumns,
                whereClause, null, null, null, null);


        List<ConnectivityOutput> outputs = new ArrayList<ConnectivityOutput>();

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

    public List<CollectedDataOutput> getCollectedDataOutputByTime(String timestamp) {
        if (mDatabaseUpdate == null) {
            Log.e(LOG_TAG, "getCollectedDataOutputByTime: Query with database closed.");
            return null;
        }

        String whereClause = SQLiteUpdateHelper.COLUMN_TIMESTAMP + "='" + timestamp + "'";
        Cursor cursor = mDatabaseUpdate.query(SQLiteUpdateHelper.TABLE_COLLECTED_DATA, allCollectedDataColumns,
                whereClause, null, null, null, null);


        List<CollectedDataOutput> outputs = new ArrayList<CollectedDataOutput>();

        try {
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    CollectedDataOutput output = cursorToCollectedDataOutput(cursor);
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


    public List<ConnectivityOutput> getSomeConnectivityOutputs(int no) {
        if (mDatabaseUpdate == null) {
            Log.e(LOG_TAG, "getAllResults: Query with database closed.");
            return null;
        }

        List<ConnectivityOutput> outputs = new ArrayList<ConnectivityOutput>();


        Cursor cursor = mDatabaseUpdate.query(SQLiteUpdateHelper.TABLE_CONNECTIVITY, allConnectivityColumns,
                null, null, null, null, "_id DESC", "10");

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

    public long getLastId() {
        if (mDatabaseUpdate == null) {
            Log.e(LOG_TAG, "getAllResults: Query with database closed.");
            return -1;
        }

        long id = -1;

        Cursor cursor = mDatabaseUpdate.query(SQLiteUpdateHelper.TABLE_CONNECTIVITY, idColumn,
                null, null, null, null, "_id DESC", "1");

        try {
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToLast();
                    id = cursor.getLong(0);
                }
            }
        } finally {
            if (cursor != null){
                cursor.close();
            }
        }

        return id;
    }

    private ConnectivityOutput cursorToConnectivityOutput(Cursor cursor) {
        return new ConnectivityOutput(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4));
    }


    public List<TestOutput> getAllTestOutputs() {
        if (mDatabaseUpdate == null) {
            Log.e(LOG_TAG, "getAllResults: Query with database closed.");
            return null;
        }

        List<TestOutput> outputs = new ArrayList<TestOutput>();
        Cursor cursor = mDatabaseUpdate.query(SQLiteUpdateHelper.TABLE_TESTS, allTestsColumns,
                null, null, null, null, null);

        try {
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    TestOutput output = cursorToTestOutput(cursor);
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

    private TestOutput cursorToTestOutput(Cursor cursor) {
        return new TestOutput(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3));
    }

    private CollectedDataOutput cursorToCollectedDataOutput(Cursor cursor) {
        return new CollectedDataOutput(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6),
                cursor.getString(7), cursor.getString(8), cursor.getString(9));
    }
}
