package com.a45g.athena.connectivitymonitor;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.a45g.athena.connectivitymonitor.HelperFunctions.sudoForResult;

public class ConfigService extends Service {
    private static final String LOG_TAG = ConfigService.class.getName();
    private static final Integer ID_FOREGROUND = 1890;

    private static final String ACTION_WIFI_ENABLE = "com.a45g.athena.connectivitymonitor.action.WIFIENABLE";
    private static final String ACTION_WIFI_DISABLE = "com.a45g.athena.connectivitymonitor.action.WIFIDISABLE";
    private static final String ACTION_LTE_ENABLE = "com.a45g.athena.connectivitymonitor.action.LTEENABLE";
    private static final String ACTION_LTE_DISABLE = "com.a45g.athena.connectivitymonitor.action.LTEDISABLE";
    private static final String ACTION_MPTCP_ENABLE = "com.a45g.athena.connectivitymonitor.action.MPTCPENABLE";
    private static final String ACTION_START_SERVICE = "com.a45g.athena.connectivitymonitor.action.STARTSERVICE";
    private static final String LTE_SCRIPT = "/data/data/com.a45g.athena.connectivitymonitor/set_mptcp_lte.sh";
    private static final String WIFI_SCRIPT = "/data/data/com.a45g.athena.connectivitymonitor/set_mptcp_wifi.sh";

    private ConnectivityReceiver connReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "Starting ConfigService.");

        setForeground();

        saveScripts();

        handleActionMPTCPEnable();
        handleActionLTEEnable();

        registerReceivers();
    }


    private void setForeground() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("ConfigService running")
                .setContentText("ConfigService running")
                .setContentIntent(pi)
                .build();

        notification.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(ID_FOREGROUND, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            handleIntent(intent);
        }

        return (START_STICKY);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Destroying Foreground Service.");

        stopForeground(true);
        unregisterReceiver(connReceiver);
    }

    public static void startService(Context context){
        Intent intent = new Intent(context, ConfigService.class);
        intent.setAction(ACTION_START_SERVICE);
        context.startService(intent);
    }


    public static void startActionWifiEnable(Context context){
        Intent intent = new Intent(context, ConfigService.class);
        intent.setAction(ACTION_WIFI_ENABLE);
        context.startService(intent);
    }

    public static void startActionWiFiDisable(Context context) {
        Intent intent = new Intent(context, ConfigService.class);
        intent.setAction(ACTION_WIFI_DISABLE);
        context.startService(intent);
    }

    public static void startActionLTEEnable(Context context) {
        Intent intent = new Intent(context, ConfigService.class);
        intent.setAction(ACTION_LTE_ENABLE);
        context.startService(intent);
    }

    public static void startActionLTEDisable(Context context) {
        Intent intent = new Intent(context, ConfigService.class);
        intent.setAction(ACTION_LTE_DISABLE);
        context.startService(intent);
    }

    public static void startActionMPTCPEnable(Context context) {

        Intent intent = new Intent(context, ConfigService.class);
        intent.setAction(ACTION_MPTCP_ENABLE);
        context.startService(intent);
    }


    private void handleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_SERVICE.equals(action)){
                //testPWD();
            }
            if (ACTION_WIFI_ENABLE.equals(action)) {
                handleActionWifiEnable();
            }
            else if (ACTION_WIFI_DISABLE.equals(action)){
                handleActionWifiDisable();
            }
            else if (ACTION_LTE_ENABLE.equals(action)){
                handleActionLTEEnable();
            }
            else if (ACTION_LTE_DISABLE.equals(action)){
                handleActionLTEDisable();
            }
            else if (ACTION_MPTCP_ENABLE.equals(action)){
                handleActionMPTCPEnable();
            }
        }
    }

    private void handleActionWifiEnable(){
        String output = sudoForResult("sh "+WIFI_SCRIPT);
        Log.d(LOG_TAG, output);
    }

    private void handleActionWifiDisable(){
        String output = sudoForResult("ip rule delete table 2");
        Log.d(LOG_TAG, output);
    }

    private void handleActionLTEEnable(){
        String output = sudoForResult("sh "+LTE_SCRIPT);
        Log.d(LOG_TAG, output);
    }

    private void handleActionLTEDisable(){
        String output = sudoForResult("ip rule delete table 1");
        Log.d(LOG_TAG, output);
    }

    private void handleActionMPTCPEnable(){
        String output = sudoForResult("sysctl net.mptcp.mptcp_enabled");

        if (output.equals("net.mptcp.mptcp_enabled = 0\n")){
            Log.d(LOG_TAG, sudoForResult("sysctl -w net.mptcp.mptcp_enabled=1"));
        }
    }

    private void saveScripts(){
        HelperFunctions.saveScript(getApplicationContext(), R.raw.set_mptcp_lte, LTE_SCRIPT);
        HelperFunctions.saveScript(getApplicationContext(), R.raw.set_mptcp_wifi, WIFI_SCRIPT);
    }

    private void testPWD(){
        String output = sudoForResult("pwd");
        Log.d(LOG_TAG, output);
    }

    private void registerReceivers(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.intent.action.ANY_DATA_STATE");
        connReceiver = new ConnectivityReceiver();
        registerReceiver(connReceiver, filter);
    }
}
