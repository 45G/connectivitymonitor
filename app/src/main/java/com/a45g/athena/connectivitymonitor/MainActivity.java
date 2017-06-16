package com.a45g.athena.connectivitymonitor;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private String tag = "MainActivity:";

    private ConnectivityReceiver connReceiver;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private OutputFragment mOutputFragment = null;
    private TestFragment mTestFragment = null;

    final static int OUTPUT_FRAGMENT_INDEX = 0;
    final static int TEST_FRAGMENT_INDEX = 1;

    final static int FRAGMENTS_NUMBER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initializing fragments.
        // Depending on user / developer mode initialize what it is needed.
        mOutputFragment = new OutputFragment();
        mTestFragment = new TestFragment();

        // Create the adapter that will return a fragment for each of the one / three
        // primary sections of the application.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        ConfigService.startActionMPTCPEnable(getApplicationContext());

        //SQLiteUpdateHelper createDBHelper = new SQLiteUpdateHelper(getApplicationContext());
        //createDBHelper.getWritableDatabase();

        registerReceivers();
    }

    public OutputFragment getOutputFragment() {
        return mOutputFragment;
    }

    public TestFragment getTestFragment() {
        return mTestFragment;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = mOutputFragment;

            switch (position) {
                case TEST_FRAGMENT_INDEX:
                    fragment = mTestFragment;
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return FRAGMENTS_NUMBER;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();

            switch (position) {
                case TEST_FRAGMENT_INDEX:
                    return getString(R.string.test_fragment_title).toUpperCase(l);
                case OUTPUT_FRAGMENT_INDEX:
                    return getString(R.string.output_fragment_title).toUpperCase(l);
            }

            return null;
        }
    }




    public void registerReceivers(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.intent.action.ANY_DATA_STATE");
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
