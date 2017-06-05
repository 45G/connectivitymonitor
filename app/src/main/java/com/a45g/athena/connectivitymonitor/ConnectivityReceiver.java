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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

public class ConnectivityReceiver
        extends BroadcastReceiver {

    private String tag = "Connectivity Info:";

    private MainActivity mainActivity = null;

    private StringBuilder sb = null;

    public ConnectivityReceiver(MainActivity ma){
        mainActivity = ma;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        sb = new StringBuilder();
        sb.append(System.getProperty("line.separator"));

        if (!intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
            Log.v(tag, "Action: " + intent.getAction());
            sb.append("Action: " + intent.getAction()).append(System.getProperty("line.separator"));
            Log.v(tag, "component: " + intent.getComponent());
            Bundle extras = intent.getExtras();
            if (extras != null) {
                for (String key: extras.keySet()) {
                    Log.v(tag, key + ": " +
                            extras.get(key));
                    sb.append(key + ": " +
                            extras.get(key)).append(System.getProperty("line.separator"));
                }
            }
            mainActivity.addOutput(sb.toString(), getTime());
            return;
        }



        NetworkInfo ni = debugIntent(intent);

        if (ni.getState().toString().equals("CONNECTED")) {
            getAllNetworks(context);
        }
        else{
            sb.append("Disconnected "+ni.getTypeName()).append(System.getProperty("line.separator"));
        }

        mainActivity.addOutput(sb.toString(), getTime());
    }

    private void getAllNetworks(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] nets = connectivity.getAllNetworkInfo();

            for(int i=0; i<nets.length; i++){
                if (nets[i].getState() == NetworkInfo.State.CONNECTED
                    && (nets[i].getTypeName().equals("MOBILE") || nets[i].getTypeName().equals("WIFI")))
                {
                    Log.v(tag, "Connected "+nets[i].getTypeName());
                    sb.append("Connected "+nets[i].getTypeName()).append(System.getProperty("line.separator"));


                    if (nets[i].getType() == ConnectivityManager.TYPE_WIFI) {
                        displayWifiInfo(context);

                        LinkProperties prop = connectivity.getLinkProperties(connectivity.getActiveNetwork());
                        sb.append("IP info: "+prop.toString()).append(System.getProperty("line.separator"));

                    } else if (nets[i].getType() == ConnectivityManager.TYPE_MOBILE) {
                        displayLTEInfo(context);

                        LinkProperties prop = connectivity.getLinkProperties(connectivity.getActiveNetwork());
                        sb.append("IP info: "+prop.toString()).append(System.getProperty("line.separator"));

                    }
                }
                else
                if (nets[i].getState() == NetworkInfo.State.DISCONNECTED
                        && (nets[i].getTypeName().equals("MOBILE") || nets[i].getTypeName().equals("WIFI"))){
                    Log.v(tag, "Disconnected "+nets[i].getTypeName());
                    sb.append("Disconnected "+nets[i].getTypeName()).append(System.getProperty("line.separator"));
                }
            }
        }
    }

    private NetworkInfo debugIntent(Intent intent) {
        Log.v(tag, "Action: " + intent.getAction());
        sb.append("Action: " + intent.getAction()).append(System.getProperty("line.separator"));
        Log.v(tag, "component: " + intent.getComponent());
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key: extras.keySet()) {
                Log.v(tag, key + ": " +
                        extras.get(key));
                sb.append(key + ": " +
                        extras.get(key)).append(System.getProperty("line.separator"));
            }
        }
        else {
            Log.v(tag, "no extras");
        }

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
                        Log.v(tag, "Connected "+info.getTypeName());


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
            Log.v(tag, "WiFi SSID: " + wifiName);
            sb.append("WiFi SSID: " + wifiName).append(System.getProperty("line.separator"));

        }
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        Log.v(tag, "DHCP Info: "+dhcpInfo.toString());
        sb.append("DHCP Info: "+dhcpInfo.toString()).append(System.getProperty("line.separator"));
    }

    private void displayLTEInfo(Context context){
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkName = tm.getNetworkOperatorName();
        if (networkName != null){
            Log.v(tag, "Mobile network name: "+networkName+"\n IP: "+getMobileIP());
            sb.append("Mobile network name: "+networkName).append(System.getProperty("line.separator"));
            //sb.append("IP: "+getMobileIP()).append(System.getProperty("line.separator"));
        }
       // Log.v(tag, tm.getCarrierConfig());

    }

    public String intToIp(int i) {
        return ((i >> 24 ) & 0xFF ) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ( i & 0xFF) ;
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
            Log.e(tag, "Exception in Get IP Address: " + ex.toString());
        }
        return null;
    }

    private String getTime(){
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }



}