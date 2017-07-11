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
        Log.d(LOG_TAG, "WiFi IP=" + mWiFiIP + " MobileData IP=" + mMobileIP);
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
}
