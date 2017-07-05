package com.a45g.athena.connectivitymonitor;

import android.util.Log;

public class Singleton {
    private static final String LOG_TAG = "ConnectivityStatus";

    private static boolean wifiEnabled = false;
    private static boolean mobileDataEnabled = false;
    private static boolean MPTCPSupported = false;
    private static boolean MPTCPEnabled = false;

    /**
     * A private Constructor prevents any other class from instantiating.
     */
    private Singleton() {}

    public static boolean isWifi() {
        return wifiEnabled;
    }

    public static void setWifi(boolean mWifi) {
        wifiEnabled = mWifi;
    }

    public static boolean isMobileData() {
        return mobileDataEnabled;
    }

    public static void setMobileData(boolean mMobileData) {
        mobileDataEnabled = mMobileData;
    }

    public static boolean isMPTCPSupported() {
        return MPTCPSupported;
    }

    public static void setMPTCPSupported(boolean mMPTCPSupported) {
        MPTCPSupported = mMPTCPSupported;
    }

    public static boolean isMPTCPEnabled() {
        return MPTCPEnabled;
    }

    public static void setMPTCPEnabled(boolean mMPTCPEnabled) {
        MPTCPEnabled = mMPTCPEnabled;
    }

    public static void displayConnectivityStatus(){
        Log.d(LOG_TAG, "MPTCP=" + MPTCPEnabled + " WiFi="+wifiEnabled + " LTE="+mobileDataEnabled);
    }
}
