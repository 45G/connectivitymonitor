package com.a45g.athena.connectivitymonitor;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HelperFunctions {
    private static final String LOG_TAG = "HelperFunctions";

    public static String sudoForResult(String...strings) {
        String res = "This device is not rooted";
        DataOutputStream outputStream = null;
        InputStream response = null;
        try{
            Process su = Runtime.getRuntime().exec("su");
            outputStream = new DataOutputStream(su.getOutputStream());
            response = su.getInputStream();

            for (String s : strings) {
                outputStream.writeBytes(s+"\n");
                outputStream.flush();
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            res = readFully(response);
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            closeSilently(outputStream, response);
        }
        return res;
    }

    public static String sudoForResultErr(String...strings) {
        String res = "This device is not rooted";
        DataOutputStream outputStream = null;
        InputStream response = null;
        try{
            Process su = Runtime.getRuntime().exec("su");
            outputStream = new DataOutputStream(su.getOutputStream());
            response = su.getErrorStream();

            for (String s : strings) {
                outputStream.writeBytes(s+"\n");
                outputStream.flush();
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            res = readFully(response);
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            closeSilently(outputStream, response);
        }
        return res;
    }

    private static String readFully(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString("UTF-8");
    }

    public static void saveScript(Context context, int rawResourceID, String scriptName){
        InputStream ins = context.getResources().openRawResource(rawResourceID);
        byte[] buffer;
        try {
            buffer = new byte[ins.available()];
            ins.read(buffer);
            ins.close();
            FileOutputStream fos = new FileOutputStream(new File(scriptName));
            fos.write(buffer);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //public static String getTime(){ return new SimpleDateFormat("HH:mm:ss").format(new Date()); }
    public static String getTime(){return "" + System.currentTimeMillis() / 1000l;   }


    private static void closeSilently(Object... xs) {
        for (Object x : xs) {
            if (x != null) {
                try {
                    //Log.d(LOG_TAG, "closing: " + x);
                    if (x instanceof Closeable) {
                        ((Closeable) x).close();
                    } else if (x instanceof Socket) {
                        ((Socket) x).close();
                    } else if (x instanceof DatagramSocket) {
                        ((DatagramSocket) x).close();
                    } else {
                        Log.d(LOG_TAG, "cannot close: " + x);
                        throw new RuntimeException("cannot close " + x);
                    }
                } catch (Throwable e) {
                    Log.d(LOG_TAG, e.toString());
                }
            }
        }
    }

    /*public static void putValue(Context context, String pref, String value) {
        SharedPreferences settings = context.getSharedPreferences(
                context.getString(R.string.settings), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(pref, value);
        editor.commit();
    }

    public static String getValue(Context context, String value, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(
                context.getString(R.string.settings), Activity.MODE_PRIVATE);
        return settings.getString(value, defaultValue);
    }*/

    public static boolean checkPermission(Context context, String permission){
        int permissionCheck = ContextCompat.checkSelfPermission(context, permission);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else{
            return false;
        }
    }
}
