package com.a45g.athena.connectivitymonitor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteUpdateHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "SQLiteUpdateHelper";

    private static final String DATABASE_NAME = "connectivitymonitor.db";
    private static final int DATABASE_VERSION = 1;


    public static final String TABLE_CONNECTIVITY = "connectivity_events";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_INTERFACE = "interface";
    public static final String COLUMN_EVENT = "event";
    public static final String COLUMN_DETAILS = "details";

    public static final String TABLE_TESTS = "test_results";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_VALUE = "value";

    public static final String TABLE_COLLECTED_DATA = "collected_data";
    public static final String COLUMN_RX_WLAN = "rx_wlan";
    public static final String COLUMN_RX_LTE = "rx_lte";
    public static final String COLUMN_TX_WLAN = "tx_wlan";
    public static final String COLUMN_TX_LTE = "tx_lte";

    public static final String COLUMN_RSSI_WLAN = "rssi_wlan";
    public static final String COLUMN_RSSI_LTE = "rssi_lte";

    public static final String COLUMN_MCS_WLAN = "mcs_wlan";
    public static final String COLUMN_FREQ_WLAN = "freq_wlan";

    public static final String COLUMN_RTT_WLAN = "rtt_wlan";
    public static final String COLUMN_RTT_LTE = "rtt_lte";

    public static final String COLUMN_CI_LTE = "ci_lte";
    public static final String COLUMN_TAC_LTE = "tac_lte";

    public static final String COLUMN_BATTERY = "battery";


    private static final String CONNECTIVITY_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CONNECTIVITY + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TIMESTAMP + " string not null, "
            + COLUMN_INTERFACE + " string not null, "
            + COLUMN_EVENT + " string not null, "
            + COLUMN_DETAILS + " string not null);";

    private static final String TESTS_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_TESTS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TIMESTAMP + " string not null, "
            + COLUMN_TYPE + " string not null, "
            + COLUMN_VALUE + " string not null);";

    private static final String COLLECTED_DATA_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_COLLECTED_DATA + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TIMESTAMP + " string not null, "
            + COLUMN_RX_WLAN + " string not null, "
            + COLUMN_RX_LTE + " string not null, "
            + COLUMN_TX_WLAN + " string not null, "
            + COLUMN_TX_LTE + " string not null, "
            + COLUMN_RSSI_WLAN + " string not null, "
            + COLUMN_RSSI_LTE + " string not null, "
            + COLUMN_MCS_WLAN + " string not null, "
            + COLUMN_FREQ_WLAN + " string not null, "
            + COLUMN_RTT_WLAN + " string not null, "
            + COLUMN_RTT_LTE + " string not null, "
            + COLUMN_CI_LTE + " string not null, "
            + COLUMN_TAC_LTE + " string not null, "
            + COLUMN_BATTERY + " string not null);";


    public SQLiteUpdateHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void createConnectivity(SQLiteDatabase database) {
        database.execSQL(CONNECTIVITY_CREATE);
    }

    public void deleteConnectivity(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CONNECTIVITY);
    }

    public void createTests(SQLiteDatabase database) {
        database.execSQL(TESTS_CREATE);
    }

    public void deleteTests(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TESTS);
    }

    public void createBytes(SQLiteDatabase database) {
        database.execSQL(COLLECTED_DATA_CREATE);
    }

    public void deleteBytes(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_COLLECTED_DATA);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d(LOG_TAG, "Creating tables in " + DATABASE_NAME);

        createConnectivity(database);
        createTests(database);
        createBytes(database);
    }

    public void onDelete(SQLiteDatabase database) {
        Log.d(LOG_TAG, "Deleting tables from " + DATABASE_NAME);

        deleteConnectivity(database);
        deleteTests(database);
        deleteBytes(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion +
                " to " + newVersion + ", which will destroy all old data");

        onDelete(database);
        onCreate(database);
    }

}

