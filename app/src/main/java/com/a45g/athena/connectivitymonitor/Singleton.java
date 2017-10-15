package com.a45g.athena.connectivitymonitor;

import android.util.Log;

public class Singleton {
    private static final String LOG_TAG = "ConnectivityStatus";

    private static boolean mWifiEnabled = false;
    private static boolean mMobileDataEnabled = false;
    private static boolean mMPTCPSupported = false;
    private static boolean mMPTCPEnabled = false;
    private static String mWiFiIP = null;
    private static String mMobileIP = null;
    private static boolean mSavedScripts = false;
    private static boolean mScriptsNeeded = true;
    private static String mApn = "land";
    private static boolean mMPTCPNeeded = true;
    private static int rx_wlan = 0;
    private static int rx_lte = 0;
    private static int tx_wlan = 0;
    private static int tx_lte = 0;
    public static boolean empty_bytes = true;

    static final String LTE_SCRIPT = "/sdcard/45g/set_mptcp_lte.sh";
    static final String WIFI_SCRIPT = "/sdcard/45g/set_mptcp_wifi.sh";
    static final String LTE_IP_SCRIPT = "/sdcard/45g/get_lte_ip.sh";
    static final String WIFI_IP_SCRIPT = "/sdcard/45g/get_wifi_ip.sh";
    static final String GET_BYTES_SCRIPT = "/sdcard/45g/get_bytes.py";
    static final String URL_SCRIPT = "/sdcard/45g/url.py";
    static final String TCP_PING_SCRIPT = "/sdcard/45g/tcp_ping.py";
    static final String TFO_CLIENT_SCRIPT = "/sdcard/45g/tfo_client.py";
    static final String folder45g = "45g";
    static final String curl_cmd = "LD_LIBRARY_PATH=/data/data/com.termux/files/usr/lib /data/data/com.termux/files/usr/bin/curl";
    static final String PYTHON ="/data/user/0/org.qpython.qpy/files/bin/qpython-android5.sh";


    /**
     * A private Constructor prevents any other class from instantiating.
     */
    private Singleton() {}

    public static boolean isWifiEnabled() {
        return mWifiEnabled;
    }

    public static void setWifi(boolean Wifi) {
        mWifiEnabled = Wifi;
    }

    public static boolean isMobileDataEnabled() {
        return mMobileDataEnabled;
    }

    public static void setMobileData(boolean MobileData) {
        mMobileDataEnabled = MobileData;
    }

    public static boolean isMPTCPSupported() {
        return mMPTCPSupported;
    }

    public static void setMPTCPSupported(boolean MPTCPSupported) {
        mMPTCPSupported = MPTCPSupported;
    }

    public static boolean isMPTCPEnabled() {
        return mMPTCPEnabled;
    }

    public static void setMPTCPEnabled(boolean MPTCPEnabled) {
        mMPTCPEnabled = MPTCPEnabled;
    }

    public static void displayConnectivityStatus(){
        Log.d(LOG_TAG, "MPTCP=" + mMPTCPEnabled + " WiFi=" + mWifiEnabled + " MobileData=" + mMobileDataEnabled);
        Log.d(LOG_TAG, "WiFiIP=" + mWiFiIP + " MobileDataIP=" + mMobileIP);
    }

    public static String getWiFiIP() {
        return mWiFiIP;
    }

    public static void setWiFiIP(String WiFiIP) {
        mWiFiIP = WiFiIP;
    }


    public static String getMobileIP() {
        return mMobileIP;
    }

    public static void setMobileIP(String MobileIP) {
        mMobileIP = MobileIP;
    }

    public static boolean savedScripts() {
        return mSavedScripts;
    }

    public static void setSavedScripts(boolean savedScripts) {
        mSavedScripts = savedScripts;
    }

    public static boolean areScriptsNeeded() {
        return mScriptsNeeded;
    }

    public static void setScriptsNeeded(boolean scriptsNeeded) {
        mScriptsNeeded = scriptsNeeded;
    }

    public static String getApn() {
        return mApn;
    }

    public static void setApn(String apn) {
        mApn = apn;
    }

    public static boolean isMPTCPNeeded() {
        return mMPTCPNeeded;
    }

    public static void setMPTCPNeeded(boolean MPTCPNeeded) {
        mMPTCPNeeded = MPTCPNeeded;
    }

    public static int getRxWlan() {
        return rx_wlan;
    }

    public static void setRxWlan(int rx_wlan) {
        Singleton.rx_wlan = rx_wlan;
    }

    public static int getRxLte() {
        return rx_lte;
    }

    public static void setRxLte(int rx_lte) {
        Singleton.rx_lte = rx_lte;
    }

    public static int getTxWlan() {
        return tx_wlan;
    }

    public static void setTxWlan(int tx_wlan) {
        Singleton.tx_wlan = tx_wlan;
    }

    public static int getTxLte() {
        return tx_lte;
    }

    public static void setTxLte(int tx_lte) {
        Singleton.tx_lte = tx_lte;
    }
}
