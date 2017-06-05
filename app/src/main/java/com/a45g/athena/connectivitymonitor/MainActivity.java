package com.a45g.athena.connectivitymonitor;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String tag = "Connectivity Info:";

    private ConnectivityReceiver connReceiver;

    private TextView mOutputText = null;
    private View mScrollView = null;
    private RelativeLayout mLayout = null;

    private int mScrollPos;
    private int mMaxScrollPosition;
    private List<OutputData> mOutputCache = null;
    private Runnable mOutputRunnable = null;
    private Runnable checkScrollRunnable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOutputText = (TextView) findViewById(R.id.outputText);
        mScrollView = (ScrollView) findViewById(R.id.outputScrollView);
        mLayout = (RelativeLayout) findViewById(R.id.mainRelative);

        mOutputText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // This should not happen.
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                mScrollPos = mScrollView.getScrollY();
                mMaxScrollPosition = mLayout.getHeight() - mScrollView.getHeight();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                checkScroll(mLayout.getHeight() - mScrollView.getHeight() - mMaxScrollPosition);
            }

        });

        mOutputCache = new ArrayList<OutputData>();
        mOutputRunnable = new Runnable() {
            @Override
            public void run() {
                appendOutput();
            }
        };
        checkScrollRunnable = new Runnable() {
            @Override
            public void run() {
                ((ScrollView) mScrollView).fullScroll(View.FOCUS_UP);
            }
        };

        //mOutputText.setText("Test main");

        registerReceivers();
    }

    private synchronized void appendOutput() {
        if (mOutputCache.size() == 0) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for(OutputData output : mOutputCache) {
            sb.append(output.getTime()).append(" ")
                    .append(output.getValue())
                    .append(System.getProperty("line.separator"))
                    .append(System.getProperty("line.separator"));
        }

        mOutputText.setText(sb.toString() + mOutputText.getText());
        mOutputCache.clear();
    }

    private void checkScroll(int added) {
        if (mScrollPos == 0) {
            mScrollView.post(checkScrollRunnable);
        } else {
            Log.d(tag, "Scrolling with addition: " + (-added));
            mScrollView.scrollBy(0, -added);
        }
    }

    public void addOutput(String value, String time){

        //Log.d(tag, "Test fragment");
        //mOutputText.setText("Test fragment");

        OutputData output = new OutputData(value, time);
        if (mOutputCache != null) {
            mOutputCache.add(output);
            // if (mOutputCache.size() > MIN_DISPLAY_SIZE) {
            appendOutput();
            //} else {
            //this.postDelayed(mOutputRunnable, 500);
            //}

        }
    }

    public void registerReceivers(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        //filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        connReceiver = new ConnectivityReceiver(this);
        registerReceiver(connReceiver, filter);

        /*TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        PhoneStateListener myCustomPhoneStateListener = new PhoneStateListener(){
            public void onDataConnectionStateChanged(int state){
                switch(state){
                    case TelephonyManager.DATA_DISCONNECTED:
                        Log.v(tag, "Mobile data disconnected");
                        break;
                    case TelephonyManager.DATA_CONNECTED:
                        Log.v(tag, "Mobile data connected");
                        break;
                }
            }
        };

        tm.listen(myCustomPhoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(connReceiver);
    }
}
