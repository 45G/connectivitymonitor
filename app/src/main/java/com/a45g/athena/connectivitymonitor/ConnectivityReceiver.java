package com.a45g.athena.connectivitymonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static com.a45g.athena.connectivitymonitor.HelperFunctions.getTime;

public class ConnectivityReceiver
        extends BroadcastReceiver {
    
    private static final String LOG_TAG = "ConnectivityReceiver";

    private StringBuilder sb = null;
    private String timestamp = null;
    private Context mContext = null;

    public ConnectivityReceiver() {;}

    private String newLine = null;

    private String delims = " ";

    @Override
    public void onReceive(Context context, Intent intent) {

        //displayBroadcast(context, intent);
        processBroadcast(context, intent);

    }

    private void displayBroadcast(Context context, Intent intent){
        timestamp = getTime();
        mContext = context;
        String action = intent.getAction();
        newLine = System.getProperty("line.separator");

        sb = new StringBuilder();
        sb.append(newLine);

        displayAction(intent);
        displayAllKeys(intent);

        if (sb.toString().equals(newLine)) return;

        Intent i=new Intent("com.a45g.athena.connectivitymonitor.ACTION_DISPLAY");
        i.putExtra("timestamp", timestamp);
        i.putExtra("value", sb.toString());
        context.sendBroadcast(i);
    }

    private void processBroadcast(Context context, Intent intent){
        timestamp = getTime();
        mContext = context;
        String action = intent.getAction();
        newLine = System.getProperty("line.separator");

        long id = -1;

        String apnToWatch = Singleton.getApn();
        //Log.v(LOG_TAG, "APN to watch: " + apnToWatch);

        sb = new StringBuilder();
        sb.append(newLine);

        /*if (action.equals("android.net.wifi.STATE_CHANGE")) {
            NetworkInfo ni = getNetworkInfo(intent);
            if (ni.getState().toString().equals("CONNECTED")) {
                displayAction(intent);
                displayKey(intent, "wifiInfo");
            }
            else{
                return;
            }
        }
        else*/
        if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")){
            NetworkInfo ni = getNetworkInfo(intent);
            if (ni.getState().toString().equals("CONNECTED")) {
                if (ni.getTypeName().equals("WIFI")){
                    displayAllInfo(intent);
                    getAllNetworks(context);

                    String newIP = getWiFiIP(context);

                    if (Singleton.isWifiEnabled() && newIP.equals(Singleton.getWiFiIP())){
                        Log.d(LOG_TAG, "WiFi already enabled, same IP address");
                    }
                    else if (Singleton.isWifiEnabled() && !newIP.equals(Singleton.getWiFiIP())){
                        Log.d(LOG_TAG, "WiFi IP has changed");
                        if (Singleton.isMPTCPSupported() && Singleton.isMPTCPEnabled() && Singleton.savedScripts()) {
                            ConfigService.startActionWiFiDisable(context);
                            ConfigService.startActionWifiEnable(context);
                        }
                        else{
                            Singleton.setWifi(true);
                            Singleton.setWiFiIP(newIP);
                            Singleton.displayConnectivityStatus();
                        }
                        id = insertConnEventInDB("WIFI", "CONNECTED",sb.toString());
                        sendIntentToDisplayInfo(id);
                        //Log.d(LOG_TAG, "Sent msg, id="+id);
                    }
                    else
                    if (!Singleton.isWifiEnabled()) {
                        if (Singleton.isMPTCPNeeded() && Singleton.areScriptsNeeded()
                                && Singleton.isMPTCPSupported() && Singleton.isMPTCPEnabled()
                                && Singleton.savedScripts()) {
                            ConfigService.startActionWifiEnable(context);
                        }
                        else{
                            Singleton.setWifi(true);
                            Singleton.displayConnectivityStatus();
                        }
                        id = insertConnEventInDB("WIFI", "CONNECTED",sb.toString());
                        sendIntentToDisplayInfo(id);
                        //Log.d(LOG_TAG, "Sent msg, id="+id);
                    }

                    if (Singleton.getLastUploadTime() != null) {
                        String time = HelperFunctions.getTime();
                        long difference = Long.parseLong(time) - Long.parseLong(Singleton.getLastUploadTime());
                        Log.d(LOG_TAG, "Difference=" + difference);

                        if (difference > Singleton.day) {
                            UploadDB.upload();
                        }
                    }
                    else{
                        UploadDB.upload();
                    }

                }
                else{
                    return;
                }
            }
            else if (ni.getState().toString().equals("DISCONNECTED")) {
                sb.append("Disconnected "+ni.getTypeName()).append(System.getProperty("line.separator"));

                if (ni.getTypeName().equals("WIFI")){
                    displayAllInfo(intent);

                    if (Singleton.isWifiEnabled()) {
                        if (Singleton.isMPTCPNeeded() && Singleton.areScriptsNeeded()
                                && Singleton.isMPTCPSupported() && Singleton.isMPTCPEnabled()
                                && Singleton.savedScripts()) {
                            ConfigService.startActionWiFiDisable(context);
                        }
                        else{
                            Singleton.setWifi(false);
                            Singleton.displayConnectivityStatus();
                        }
                        Singleton.setWiFiIP(null);
                        id = insertConnEventInDB("WIFI", "DISCONNECTED", sb.toString());
                        sendIntentToDisplayInfo(id);
                        Log.d(LOG_TAG, "Sent msg, id="+id);
                    }
                    else{
                        Log.d(LOG_TAG, "WiFi already disabled");
                    }
                }
                else{
                    return;
                }
            }
            else{
                return;
            }
        }
        else if (action.equals("android.intent.action.ANY_DATA_STATE")){
            Bundle extras = intent.getExtras();
            if (//(extras.get("reason") != null && extras.get("reason").equals("connected")) &&
                    (extras.get("state") != null && extras.get("state").equals("CONNECTED")) &&
                            (extras.get("apn") != null && extras.get("apn").equals(apnToWatch)) &&
                            (extras.get("apnType") != null && extras.get("apnType").equals("default"))
                    ){
                displayAllInfo(intent);

                String newIP = getMobileIP();
                //Log.d(LOG_TAG, "new mobile IP="+newIP);

                if (Singleton.isMobileDataEnabled() && newIP.equals(Singleton.getMobileIP())){
                    Log.d(LOG_TAG, "Mobile Data already enabled, same IP address");
                }
                else if (Singleton.isMobileDataEnabled() && !newIP.equals(Singleton.getMobileIP())){
                    Log.d(LOG_TAG, "Mobile IP has changed");
                    if (Singleton.isMPTCPNeeded() && Singleton.areScriptsNeeded()
                            && Singleton.isMPTCPSupported() && Singleton.isMPTCPEnabled()
                            && Singleton.savedScripts()) {
                        ConfigService.startActionMobileDataDisable(context);
                        ConfigService.startActionMobileDataEnable(context);
                    }
                    else{
                        Singleton.setMobileData(true);
                        Singleton.setMobileIP(newIP);
                        Singleton.displayConnectivityStatus();
                    }
                    id = insertConnEventInDB("MobileData", "CONNECTED",sb.toString());
                    sendIntentToDisplayInfo(id);
                    Log.d(LOG_TAG, "Sent msg, id="+id);
                }
                else
                if (!Singleton.isMobileDataEnabled()) {
                    if (Singleton.isMPTCPSupported() && Singleton.isMPTCPEnabled() && Singleton.savedScripts()) {
                        ConfigService.startActionMobileDataEnable(context);
                    }
                    else{
                        Singleton.setMobileData(true);
                        Singleton.displayConnectivityStatus();
                    }
                    id = insertConnEventInDB("MobileData", "CONNECTED",sb.toString());
                    sendIntentToDisplayInfo(id);
                    Log.d(LOG_TAG, "Sent msg, id="+id);
                }
            }
            else if ((extras.get("state") != null && extras.get("state").equals("DISCONNECTED")) &&
                    (extras.get("apn") != null && extras.get("apn").equals(apnToWatch)) &&
                    // (extras.get("reason") != null && extras.get("reason").equals("specificDisabled")
                    //         || (extras.get("reason") == null)) &&
                    (extras.get("apnType") != null && extras.get("apnType").equals("default"))
                    ){
                displayAllInfo(intent);

                if (Singleton.isMobileDataEnabled()) {
                    if (Singleton.isMPTCPNeeded() && Singleton.areScriptsNeeded()
                            && Singleton.isMPTCPSupported() && Singleton.isMPTCPEnabled()
                            && Singleton.savedScripts()) {
                        ConfigService.startActionMobileDataDisable(context);
                    }
                    else{
                        Singleton.setMobileData(false);
                        Singleton.displayConnectivityStatus();
                    }
                    Singleton.setMobileIP(null);
                    id = insertConnEventInDB("MobileData", "DISCONNECTED",sb.toString());
                    sendIntentToDisplayInfo(id);
                    Log.d(LOG_TAG, "Sent msg, id="+id);
                }
                else{
                    Log.d(LOG_TAG, "Mobile Data already disabled");
                }
            }
            else {
                //displayAllInfo(intent);
                return;
            }
        }
        else{
            return;
        }


        if (sb.toString().equals(newLine) || id == -1) return;

        //sendIntentToDisplayInfo(id);
    }

    private long insertConnEventInDB(String iface, String event, String details){
        long id = -1;

        DatabaseOperations databaseOperations = new DatabaseOperations(mContext);
        databaseOperations.openWrite();
        id = databaseOperations.insertConnectivityEvent(timestamp, iface, event,details);
        databaseOperations.close();

        return id;
    }

    private void sendIntentToDisplayInfo(long id){
        Intent i=new Intent("com.a45g.athena.connectivitymonitor.ACTION_DISPLAY");
        i.putExtra("id", id);
        mContext.sendBroadcast(i);
    }

    private void displayAllInfo(Intent intent){
        displayAction(intent);
        displayAllKeys(intent);
    }

    private void getAllNetworks(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] nets = connectivity.getAllNetworkInfo();

            for(int i=0; i<nets.length; i++){
                if (nets[i].getState() == NetworkInfo.State.CONNECTED
                    && (nets[i].getTypeName().equals("MOBILE") || nets[i].getTypeName().equals("WIFI")))
                {
                    //Log.v(LOG_TAG, "Connected "+nets[i].getTypeName());
                    //sb.append("Connected "+nets[i].getTypeName()).append(System.getProperty("line.separator"));


                    if (nets[i].getType() == ConnectivityManager.TYPE_WIFI) {
                        //displayWifiInfo(context);

                        LinkProperties prop = connectivity.getLinkProperties(connectivity.getActiveNetwork());
                        sb.append("IP info: "+prop.toString()).append(System.getProperty("line.separator"));

                    } else if (nets[i].getType() == ConnectivityManager.TYPE_MOBILE) {
                        displayLTEInfo(context);

                        LinkProperties prop = connectivity.getLinkProperties(connectivity.getActiveNetwork());
                        sb.append("IP info: "+prop.toString()).append(System.getProperty("line.separator"));

                    }
                }
            }
        }
    }

    private void displayAction(Intent intent){
        Log.v(LOG_TAG, "Action: " + intent.getAction());
        sb.append("Action: " + intent.getAction()).append(System.getProperty("line.separator"));
    }

    private void displayAllKeys(Intent intent){
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key: extras.keySet()) {
                Log.v(LOG_TAG, key + ": " +
                        extras.get(key));
                sb.append(key + ": " +
                        extras.get(key)).append(System.getProperty("line.separator"));
            }
        }
        else {
            Log.v(LOG_TAG, "no extras");
        }
    }

    private void displayKey(Intent intent, String key){
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.v(LOG_TAG, key + ": " +
                    extras.get(key));
            sb.append(key + ": " +
                    extras.get(key)).append(System.getProperty("line.separator"));

        }
    }

    private NetworkInfo getNetworkInfo(Intent intent) {
        Bundle extras = intent.getExtras();
        return (NetworkInfo)(extras.get("networkInfo"));
    }

    public void connectivityInfo(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null)
                    if (info.getState() == NetworkInfo.State.CONNECTED)
                    {
                        Log.v(LOG_TAG, "Connected "+info.getTypeName());


                        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                            displayWifiInfo(context);
                        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                            displayLTEInfo(context);
                        }
                    }


        }
    }

    private void displayWifiInfo(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String wifiName = wifiManager.getConnectionInfo().getSSID();
        if (wifiName != null) {
            Log.v(LOG_TAG, "WiFi SSID: " + wifiName);
            sb.append("WiFi SSID: " + wifiName).append(System.getProperty("line.separator"));

        }
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        Singleton.setWiFiIPGateway(intToIp(dhcpInfo.gateway));
        Log.v(LOG_TAG, "DHCP Info: "+dhcpInfo.toString());
        sb.append("DHCP Info: "+dhcpInfo.toString()).append(System.getProperty("line.separator"));
    }

    private void displayLTEInfo(Context context){
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkName = tm.getNetworkOperatorName();
        if (networkName != null){
            Log.v(LOG_TAG, "Mobile network name: "+networkName+"\n IP: "+getMobileIP());
            sb.append("Mobile network name: "+networkName).append(System.getProperty("line.separator"));
            //sb.append("IP: "+getMobileIP()).append(System.getProperty("line.separator"));
        }
       // Log.v(LOG_TAG, tm.getCarrierConfig());

    }

    public String intToIp(int i) {
        return (( i & 0xFF) + "." +
                ((i >> 8 ) & 0xFF)+ "." +
                ((i >> 16 ) & 0xFF) + "." +
                ((i >> 24 ) & 0xFF) );
    }

    private String getWiFiIP(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        String[] tokens = dhcpInfo.toString().split(delims);
        //Log.v(LOG_TAG, "Get WiFiIP="+tokens[1]);

        return tokens[1];
    }

    public String getMobileIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ipaddress = inetAddress .getHostAddress().toString();
                        return ipaddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(LOG_TAG, "Exception in Get IP Address: " + ex.toString());
        }
        return null;
    }
}
