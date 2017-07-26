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
}
