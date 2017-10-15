package com.a45g.athena.connectivitymonitor;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.File;
import android.os.Environment;
import android.widget.Toast;

import static com.a45g.athena.connectivitymonitor.HelperFunctions.sudoForResult;

public class ConfigService extends Service {
    private static final String LOG_TAG = "ConfigService";
    private static final Integer ID_FOREGROUND = 1890;

    private static final String ACTION_WIFI_ENABLE = "com.a45g.athena.connectivitymonitor.action.WIFIENABLE";
    private static final String ACTION_WIFI_DISABLE = "com.a45g.athena.connectivitymonitor.action.WIFIDISABLE";
    private static final String ACTION_MOBILE_DATA_ENABLE = "com.a45g.athena.connectivitymonitor.action.LTEENABLE";
    private static final String ACTION_MOBILE_DATA_DISABLE = "com.a45g.athena.connectivitymonitor.action.LTEDISABLE";
    private static final String ACTION_MPTCP_ENABLE = "com.a45g.athena.connectivitymonitor.action.MPTCPENABLE";
    private static final String ACTION_MPTCP_DISABLE = "com.a45g.athena.connectivitymonitor.action.MPTCPDISABLE";
    private static final String ACTION_START_SERVICE = "com.a45g.athena.connectivitymonitor.action.STARTSERVICE";


    private String delims = "\n";

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

        if (Singleton.isMPTCPNeeded()) {
            handleActionMPTCPTestAndEnable();

            if (Singleton.isMPTCPSupported() && Singleton.isMPTCPEnabled()
                    && Singleton.savedScripts() && Singleton.areScriptsNeeded()) {
                handleActionMobileDataEnable();
                handleActionWifiEnable();
            }
        }

        registerReceivers();

        periodicTasks();
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

    public static void startActionMobileDataEnable(Context context) {
        Intent intent = new Intent(context, ConfigService.class);
        intent.setAction(ACTION_MOBILE_DATA_ENABLE);
        context.startService(intent);
    }

    public static void startActionMobileDataDisable(Context context) {
        Intent intent = new Intent(context, ConfigService.class);
        intent.setAction(ACTION_MOBILE_DATA_DISABLE);
        context.startService(intent);
    }

    public static void startActionMPTCPEnable(Context context) {
        Intent intent = new Intent(context, ConfigService.class);
        intent.setAction(ACTION_MPTCP_ENABLE);
        context.startService(intent);
    }

    public static void startActionMPTCPDisable(Context context) {
        Intent intent = new Intent(context, ConfigService.class);
        intent.setAction(ACTION_MPTCP_DISABLE);
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
            else if (ACTION_MOBILE_DATA_ENABLE.equals(action)){
                handleActionMobileDataEnable();
            }
            else if (ACTION_MOBILE_DATA_DISABLE.equals(action)){
                handleActionMobileDataDisable();
            }
            else if (ACTION_MPTCP_ENABLE.equals(action)){
                handleActionMPTCPTestAndEnable();
            }
            else if (ACTION_MPTCP_DISABLE.equals(action)){
                handleActionMPTCPDisable();
            }
        }
    }

    private void handleActionWifiEnable(){
        String output = sudoForResult("sh "+Singleton.WIFI_SCRIPT);
        Log.d(LOG_TAG, output);

        if (output.equals("Please start WiFi\n")){
            Singleton.setWifi(false);
            Log.d(LOG_TAG, "WiFi disabled");
        }
        else{
            String[] tokens = output.split(delims);
            Singleton.setWiFiIP(tokens[0]);
            Singleton.setWifi(true);
            Log.d(LOG_TAG, "WiFi enabled");
        }

        Singleton.displayConnectivityStatus();
    }

    private void handleActionWifiDisable(){
        String output = sudoForResult("ip rule delete table 2");
        Log.d(LOG_TAG, output);
        Singleton.setWifi(false);

        Singleton.displayConnectivityStatus();
    }

    private void handleActionMobileDataEnable(){
        String output = sudoForResult("sh "+Singleton.LTE_SCRIPT);
        Log.d(LOG_TAG, output);

        if (output.equals("Please start Mobile Data\n")){
            Singleton.setMobileData(false);
            Log.d(LOG_TAG, "Mobile Data disabled");
        }
        else{
            String[] tokens = output.split(delims);
            Singleton.setMobileIP(tokens[0]);
            Singleton.setMobileData(true);
            Log.d(LOG_TAG, "Mobile Data enabled");
        }

        Singleton.displayConnectivityStatus();
    }

    private void handleActionMobileDataDisable(){
        String output = sudoForResult("ip rule delete table 1");
        Log.d(LOG_TAG, output);
        Singleton.setMobileData(false);

        Singleton.displayConnectivityStatus();
    }

    public String getWiFiIP(){
        String output = sudoForResult("sh "+Singleton.WIFI_IP_SCRIPT);
        Log.d(LOG_TAG, output);

        if (!output.equals("Please start WiFi\n")){
            String[] tokens = output.split(delims);
            return tokens[0];
        }
        return null;
    }

    public String getMobileDataIP(){
        String output = sudoForResult("sh "+Singleton.LTE_IP_SCRIPT);
        Log.d(LOG_TAG, output);

        if (!output.equals("Please start Mobile Data\n")){
            String[] tokens = output.split(delims);
            return tokens[0];
        }
        return null;
    }

    private void handleActionMPTCPTestAndEnable(){
        String output = sudoForResult("sysctl net.mptcp.mptcp_enabled");

        if (output.equals("net.mptcp.mptcp_enabled = 0\n")){
            String output2 = sudoForResult("sysctl -w net.mptcp.mptcp_enabled=1");
            if (output2.equals("net.mptcp.mptcp_enabled = 1\n")){
                Singleton.setMPTCPSupported(true);
                Singleton.setMPTCPEnabled(true);
                Log.d(LOG_TAG, "Enabled MPTCP");
            }
            else if(output2.equals("net.mptcp.mptcp_enabled = 0\n")){
                Singleton.setMPTCPSupported(true);
                Singleton.setMPTCPEnabled(false);
                Log.d(LOG_TAG, "MPTCP supported, but cannot be enabled");
            }
        }
        else if (output.equals("net.mptcp.mptcp_enabled = 1\n")){
            Singleton.setMPTCPSupported(true);
            Singleton.setMPTCPEnabled(true);
            Log.d(LOG_TAG, "MPTCP is already enabled");
        }
        else if (output.equals("sysctl: unknown key \'net.mptcp.mptcp_enabled\'\n")){
            Singleton.setMPTCPSupported(false);
            Singleton.setMPTCPEnabled(false);
            Log.d(LOG_TAG, output);
            Log.d(LOG_TAG, "MPTCP is not supported");
        }
        else{
            Singleton.setMPTCPSupported(false);
            Singleton.setMPTCPEnabled(false);
            Log.d(LOG_TAG, output);
            Log.d(LOG_TAG, "MPTCP is not supported or some error has occured");
        }
    }

    private void handleActionMPTCPDisable(){
        String output = sudoForResult("sysctl net.mptcp.mptcp_enabled");

        if (output.equals("net.mptcp.mptcp_enabled = 1\n")){
            String output2 = sudoForResult("sysctl -w net.mptcp.mptcp_enabled=0");
            if (output2.equals("net.mptcp.mptcp_enabled = 0\n")){
                Singleton.setMPTCPEnabled(false);
                Log.d(LOG_TAG, "Disabled MPTCP");
            }
        }
        else if (output.equals("net.mptcp.mptcp_enabled = 0\n")){
            Singleton.setMPTCPEnabled(false);
            Log.d(LOG_TAG, "MPTCP is already disabled");
        }
        else if (output.equals("sysctl: unknown key \'net.mptcp.mptcp_enabled\'\n")){
            Singleton.setMPTCPSupported(false);
            Singleton.setMPTCPEnabled(false);
            Log.d(LOG_TAG, output);
            Log.d(LOG_TAG, "MPTCP is not supported");
        }
        else{
            Singleton.setMPTCPSupported(false);
            Singleton.setMPTCPEnabled(false);
            Log.d(LOG_TAG, output);
            Log.d(LOG_TAG, "MPTCP is not supported or some error has occured");
        }
    }

    private void saveScripts(){


        if (HelperFunctions.checkPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            File f = new File(Environment.getExternalStorageDirectory(), Singleton.folder45g);
            if (!f.exists()) {
                f.mkdirs();
            }

            HelperFunctions.saveScript(getApplicationContext(), R.raw.set_mptcp_lte, Singleton.LTE_SCRIPT);
            HelperFunctions.saveScript(getApplicationContext(), R.raw.set_mptcp_wifi, Singleton.WIFI_SCRIPT);
            HelperFunctions.saveScript(getApplicationContext(), R.raw.get_lte_ip, Singleton.LTE_IP_SCRIPT);
            HelperFunctions.saveScript(getApplicationContext(), R.raw.get_wifi_ip, Singleton.WIFI_IP_SCRIPT);
            HelperFunctions.saveScript(getApplicationContext(), R.raw.get_bytes, Singleton.GET_BYTES_SCRIPT);
            HelperFunctions.saveScript(getApplicationContext(), R.raw.url, Singleton.URL_SCRIPT);
            HelperFunctions.saveScript(getApplicationContext(), R.raw.tcp_ping, Singleton.TCP_PING_SCRIPT);
            HelperFunctions.saveScript(getApplicationContext(), R.raw.tfo_client, Singleton.TFO_CLIENT_SCRIPT);
            Singleton.setSavedScripts(true);
        }
        else{
            Log.d(LOG_TAG, "Unable to save scripts, please grant external storage permissions");
            Singleton.setSavedScripts(false);
        }
    }

    private void testPWD(){
        String output = sudoForResult("pwd");
        Log.d(LOG_TAG, output);
    }

    private void registerReceivers(){
        IntentFilter filter = new IntentFilter();
        //filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.intent.action.ANY_DATA_STATE");
        filter.setPriority(-100);
        connReceiver = new ConnectivityReceiver();
        registerReceiver(connReceiver, filter);
    }

    private void MPTCPCleanup(){
        handleActionWifiDisable();
        handleActionMobileDataDisable();
        handleActionMPTCPDisable();
        Log.d(LOG_TAG, "Disabled MPTCP and cleaned up routing tables");
    }

    private void periodicTasks(){
        final Handler handler=new Handler();
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                storeBytes(getBytes());
                handler.postDelayed(this, 30000);
            }
        };
        handler.post(runnableCode);
    }

    private String getBytes(){
        String output = sudoForResult(Singleton.PYTHON+" "+Singleton.GET_BYTES_SCRIPT+" && exit");
        String[] tokens = output.split(delims);
        return tokens[0];
    }

    private void storeBytes(String result){

        String[] tokens = result.split(" ");

        String timestamp = tokens[0];
        int rx_wlan = Integer.parseInt(tokens[1]);
        int rx_lte = Integer.parseInt(tokens[2]);
        int tx_wlan = Integer.parseInt(tokens[3]);
        int tx_lte = Integer.parseInt(tokens[4]);

        int rx_wlan_dif = 0;
        int rx_lte_dif = 0;
        int tx_wlan_dif = 0;
        int tx_lte_dif = 0;

        if (!Singleton.empty_bytes) {
            rx_wlan_dif = rx_wlan - Singleton.getRxWlan();
            rx_lte_dif = rx_lte - Singleton.getRxLte();
            tx_wlan_dif = tx_wlan - Singleton.getTxWlan();
            tx_lte_dif = tx_lte - Singleton.getTxLte();

            if ((rx_wlan_dif > 10000) || (tx_wlan_dif > 10000)){
                Log.d(LOG_TAG, "Network traffic on WiFi");
            }
            if ((rx_lte_dif > 10000) || (tx_lte_dif > 10000)){
                Log.d(LOG_TAG, "Network traffic on LTE");
            }
        }

        Singleton.setRxWlan(rx_wlan);
        Singleton.setRxLte(rx_lte);
        Singleton.setTxWlan(tx_wlan);
        Singleton.setTxLte(tx_lte);

        Log.d(LOG_TAG, timestamp + " Total: " + rx_wlan + " "
                + rx_lte + " " + tx_wlan + " " + tx_lte);

        if (!Singleton.empty_bytes) {
            Log.d(LOG_TAG, timestamp + " Differences: " + rx_wlan_dif + " "
                    + rx_lte_dif + " " + tx_wlan_dif + " " + tx_lte_dif);

            DatabaseOperations databaseOperations = new DatabaseOperations(getApplicationContext());
            databaseOperations.openWrite();
            databaseOperations.insertBytesResult(timestamp,
                    Integer.toString(rx_wlan_dif), Integer.toString(rx_lte_dif),
                    Integer.toString(tx_wlan_dif), Integer.toString(tx_lte_dif));
            databaseOperations.close();
        }
        else{
            Singleton.empty_bytes = false;
        }
    }
}
