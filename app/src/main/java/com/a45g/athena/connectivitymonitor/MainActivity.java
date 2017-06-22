package com.a45g.athena.connectivitymonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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

    private IntentFilter mIntentFilter = null;

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.a45g.athena.connectivitymonitor.ACTION_DISPLAY")){
                Bundle extras = intent.getExtras();
                String time = extras.get("timestamp").toString();
                String value = extras.get("value").toString();
                mOutputFragment.addOutput(value, time);
            }
        }
    };

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

        SQLiteUpdateHelper createDBHelper = new SQLiteUpdateHelper(getApplicationContext());
        SQLiteDatabase database = createDBHelper.getWritableDatabase();
        createDBHelper.onCreate(database);
        //createDBHelper.onUpgrade(database, 1, 2);

        ConfigService.startService(getApplicationContext());

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.a45g.athena.connectivitymonitor.ACTION_DISPLAY");

    }

    @Override
    protected void onResume() {
        registerReceiver(mIntentReceiver, mIntentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mIntentReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            Fragment fragment = null;

            switch (position) {
                case OUTPUT_FRAGMENT_INDEX:
                    fragment = mOutputFragment;
                    break;
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


}
