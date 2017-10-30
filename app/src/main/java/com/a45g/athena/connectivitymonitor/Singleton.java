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
    private static int rx_wlan_dif = 0;
    private static int rx_lte_dif = 0;
    private static int tx_wlan_dif = 0;
    private static int tx_lte_dif = 0;
    public static boolean empty_bytes = true;
    private static String mWiFiIPGateway = null;
    private static String mLTEIPGateway = null;
    private static float rtt_wlan = 0;
    private static float rtt_lte = 0;
    private static int rssi_wlan = 0;
    private static int rssi_lte = 0;
    private static int mcs_wlan = 0;
    private static int freq_wlan = 0;
    private static int ci_lte = 0;
    private static int tac_lte = 0;
    private static float battery = 0;



    static final String LTE_SCRIPT = "/sdcard/45g/set_mptcp_lte.sh";
    static final String WIFI_SCRIPT = "/sdcard/45g/set_mptcp_wifi.sh";
    static final String LTE_IP_SCRIPT = "/sdcard/45g/get_lte_ip.sh";
    static final String LTE_GATE_SCRIPT = "/sdcard/45g/get_lte_gateway.sh";
    static final String WIFI_IP_SCRIPT = "/sdcard/45g/get_wifi_ip.sh";
    static final String WIFI_GATE_SCRIPT = "/sdcard/45g/get_wifi_gateway.sh";
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

    public static String getWiFiIPGateway() {
        return mWiFiIPGateway;
    }

    public static void setWiFiIPGateway(String WiFiIPGateway) {
        Singleton.mWiFiIPGateway = WiFiIPGateway;
    }

    public static String getLTEIPGateway() {
        return mLTEIPGateway;
    }

    public static void setLTEIPGateway(String LTEIPGateway) {
        Singleton.mLTEIPGateway = LTEIPGateway;
    }

    public static float getRtt_wlan() {
        return rtt_wlan;
    }

    public static void setRtt_wlan(float rtt_wlan) {
        Singleton.rtt_wlan = rtt_wlan;
    }

    public static float getRtt_lte() {
        return rtt_lte;
    }

    public static void setRtt_lte(float rtt_lte) {
        Singleton.rtt_lte = rtt_lte;
    }

    public static int getRx_wlan_dif() {
        return rx_wlan_dif;
    }

    public static void setRx_wlan_dif(int rx_wlan_dif) {
        Singleton.rx_wlan_dif = rx_wlan_dif;
    }

    public static int getRx_lte_dif() {
        return rx_lte_dif;
    }

    public static void setRx_lte_dif(int rx_lte_dif) {
        Singleton.rx_lte_dif = rx_lte_dif;
    }

    public static int getTx_wlan_dif() {
        return tx_wlan_dif;
    }

    public static void setTx_wlan_dif(int tx_wlan_dif) {
        Singleton.tx_wlan_dif = tx_wlan_dif;
    }

    public static int getTx_lte_dif() {
        return tx_lte_dif;
    }

    public static void setTx_lte_dif(int tx_lte_dif) {
        Singleton.tx_lte_dif = tx_lte_dif;
    }

    public static int getRssi_wlan() {
        return rssi_wlan;
    }

    public static void setRssi_wlan(int rssi_wlan) {
        Singleton.rssi_wlan = rssi_wlan;
    }

    public static int getRssi_lte() {
        return rssi_lte;
    }

    public static void setRssi_lte(int rssi_lte) {
        Singleton.rssi_lte = rssi_lte;
    }

    public static int getMcs_wlan() {
        return mcs_wlan;
    }

    public static void setMcs_wlan(int mcs_wlan) {
        Singleton.mcs_wlan = mcs_wlan;
    }

    public static int getFreq_wlan() {
        return freq_wlan;
    }

    public static void setFreq_wlan(int freq_wlan) {
        Singleton.freq_wlan = freq_wlan;
    }

    public static int getCi_lte() {
        return ci_lte;
    }

    public static void setCi_lte(int ci_lte) {
        Singleton.ci_lte = ci_lte;
    }

    public static int getTac_lte() {
        return tac_lte;
    }

    public static void setTac_lte(int tac_lte) {
        Singleton.tac_lte = tac_lte;
    }

    public static float getBattery() {
        return battery;
    }

    public static void setBattery(float battery) {
        Singleton.battery = battery;
    }
}
