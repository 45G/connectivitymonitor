package com.a45g.athena.connectivitymonitor;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private OutputFragment mOutputFragment = null;
    private TestFragment mTestFragment = null;
    private SettingsFragment mSettingsFragment = null;

    final static int OUTPUT_FRAGMENT_INDEX = 0;
    final static int TEST_FRAGMENT_INDEX = 1;
    final static int SETTINGS_FRAGMENT_INDEX = 2;

    final static int FRAGMENTS_NUMBER = 3;

    final private static int MY_PERMISSIONS_REQUEST_WRITE_SDCARD = 123;


    private IntentFilter mIntentFilter = null;

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.a45g.athena.connectivitymonitor.ACTION_DISPLAY")){
                long id = intent.getLongExtra("id", -1);
                if (id != -1) {
                    mOutputFragment.addOutput(id);
                    Log.d(LOG_TAG, "Recv msg, id=" + id);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOutputFragment = new OutputFragment();
        mTestFragment = new TestFragment();
        mSettingsFragment = new SettingsFragment();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        SQLiteUpdateHelper createDBHelper = new SQLiteUpdateHelper(getApplicationContext());
        SQLiteDatabase database = createDBHelper.getWritableDatabase();
        createDBHelper.onCreate(database);
        //createDBHelper.onUpgrade(database, 1, 2);

        requestSdcardPermissions();

        //ConfigService.startService(getApplicationContext());

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

    public SettingsFragment getSettingsFragment() {
        return mSettingsFragment;
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
                case SETTINGS_FRAGMENT_INDEX:
                    fragment = mSettingsFragment;
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
                case SETTINGS_FRAGMENT_INDEX:
                    return getString(R.string.settings_fragment_title).toUpperCase(l);
            }

            return null;
        }
    }

    private void requestSdcardPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_SDCARD);
            }
        }
        else{
            Log.d(LOG_TAG, "Sdcard permission already granted");
            ConfigService.startService(getApplicationContext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_SDCARD: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "Sdcard permission granted");
                    ConfigService.startService(getApplicationContext());
                } else {
                    Log.d(LOG_TAG, "Sdcard permission not granted");
                    //Do smth
                }
                return;
            }
        }
    }


}
