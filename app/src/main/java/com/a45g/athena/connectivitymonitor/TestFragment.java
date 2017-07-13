package com.a45g.athena.connectivitymonitor;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import static com.a45g.athena.connectivitymonitor.HelperFunctions.getTime;
import static com.a45g.athena.connectivitymonitor.HelperFunctions.sudoForResult;
import static com.a45g.athena.connectivitymonitor.HelperFunctions.sudoForResultErr;

public class TestFragment extends Fragment {

    private static final String LOG_TAG = "TestFragment";
    private static final String curl_cmd = "LD_LIBRARY_PATH=/data/data/com.termux/files/usr/lib /data/data/com.termux/files/usr/bin/curl";

    private Button mChooseType = null;
    private TextView mScriptText = null;
    private EditText mScriptName = null;
    private TextView mOption1 = null;
    private EditText mOption1Value = null;
    private TextView mOption2 = null;
    private EditText mOption2Value = null;
    private TextView mTimesText = null;
    private EditText mTimesValue = null;

    private TextView mResultText = null;


    private Button mStartTest = null;
    private Button mStopTest = null;
    private Button mClearOutput = null;
    private Button mChooseScript = null;

    private TextView mResult = null;
    private LinearLayout mLayout = null;
    private View mScrollView = null;

    private int mScrollPos;
    private int mMaxScrollPosition;
    private Runnable checkScrollRunnable = null;

    private String[] methods;
    private int methodIndex = 1;
    private String[] methodName;

    private String[] scriptName;
    private int scriptIndex = 0;

    private ExecuteTask task = null;
    private boolean cancel = false;
    private int times = 0;
    private String mCmd = null;

    private boolean runCurl = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.test_fragment, container, false);

        methods = new String[]{"", "/data/user/0/org.qpython.qpy/files/bin/qpython-android5.sh ", "sh "};
        methodName = getResources().getStringArray(R.array.execute_array);
        mChooseType = (Button) rootView.findViewById(R.id.chooseType);

        scriptName = getResources().getStringArray(R.array.scripts_array);
        mChooseScript = (Button) rootView.findViewById(R.id.chooseScript);

        mScriptText = (TextView) rootView.findViewById(R.id.scriptText);
        mScriptName = (EditText) rootView.findViewById(R.id.scriptName);

        mOption1 = (TextView) rootView.findViewById(R.id.option1);
        mOption1Value = (EditText) rootView.findViewById(R.id.option1value);

        mOption2 = (TextView) rootView.findViewById(R.id.option2);
        mOption2Value = (EditText) rootView.findViewById(R.id.option2value);

        mTimesText = (TextView) rootView.findViewById(R.id.timesText);
        mTimesValue = (EditText) rootView.findViewById(R.id.timesValue);

        mResultText = (TextView) rootView.findViewById(R.id.resultText);

        mStartTest = (Button) rootView.findViewById(R.id.start);
        mStopTest = (Button) rootView.findViewById(R.id.stop);
        mClearOutput = (Button) rootView.findViewById(R.id.clear);

        mResult = (TextView) rootView.findViewById(R.id.testResult);
        mLayout = (LinearLayout) rootView.findViewById(R.id.testLayout);
        mScrollView = (ScrollView) rootView.findViewById(R.id.testScrollView);

        mChooseType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListViewScriptTypes();
            }
        });

        mChooseScript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListViewScripts();
            }
        });

        mStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Starting text execution");

                mResult.setText("");

                if (methodIndex == 1){
                    mCmd = methods[methodIndex] +
                            mScriptName.getText() + " " + mOption1Value.getText() + " " +
                            mOption2Value.getText() + " && exit";
                    Log.d(LOG_TAG, "CMD: "+mCmd);
                }
                else{
                    mCmd = methods[methodIndex] +
                            mScriptName.getText() + " " + mOption1Value.getText() + " " +
                            mOption2Value.getText();
                    Log.d(LOG_TAG, "CMD: "+mCmd);
                }

                if (!mTimesValue.getText().toString().equals("")) {
                    times = Integer.valueOf(mTimesValue.getText().toString());
                }
                else{
                    times = 1;
                }

                if (times > 0) {
                    task = new ExecuteTask();
                    task.execute(mCmd);
                }

            }
        });

        mStopTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Canceling test execution manually");

                task.cancel(false);
            }
        });

        mClearOutput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Deleting output");

                mResult.setText("");
            }
        });


        mResult.addTextChangedListener(new TextWatcher() {

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

        checkScrollRunnable = new Runnable() {
            @Override
            public void run() {
                ((ScrollView) mScrollView).fullScroll(View.FOCUS_DOWN);
            }
        };

        //this.setHasOptionsMenu(true);
        return rootView;
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
    }

    private void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
    }

    private void checkScroll(int added) {
        if (mScrollPos == 0) {
            mScrollView.post(checkScrollRunnable);
        } else {
            Log.d(LOG_TAG, "Scrolling with addition: " + (-added));
            mScrollView.scrollBy(0, -added);
        }
    }

    private class ExecuteTask extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... params) {

            String output = null;

            if (runCurl){
                output = sudoForResultErr(params[0]);
            }
            else{
                output = sudoForResult(params[0]);
            }

            Log.d(LOG_TAG, output);

            DatabaseOperations databaseOperations = new DatabaseOperations(getContext());
            databaseOperations.openWrite();
            databaseOperations.insertTestResult(getTime(), methodName[methodIndex], output);
            databaseOperations.close();

            times--;

            return output;
        }

        protected void onPostExecute(String result) {

            if (result == null) return;

            CharSequence previousResults = mResult.getText();
            mResult.setText(previousResults + result + "\n");

            if (times > 0) {
                task = new ExecuteTask();
                task.execute(mCmd);
            }
        }

        @Override
        protected void onCancelled() {
            Log.d(LOG_TAG,"Test execution has been canceled manually");
        }
    }

    private void showListViewScriptTypes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.execute)
                .setItems(R.array.execute_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Log.d(LOG_TAG, "Selected option "+which);

                        methodIndex = which;
                        mScriptText.setText(methodName[methodIndex]+":");

                        switch (methodIndex){
                            case 0:
                                mScriptName.setText("ping -c 3 jepi.cs.pub.ro");
                                mTimesValue.setText("1");
                                break;
                            case 1:
                                mScriptName.setText("/sdcard/url.py");
                                mTimesValue.setText("1");
                                break;
                            case 2:
                                mScriptName.setText("");
                                mTimesValue.setText("1");
                                break;
                            default:
                                disableEditText(mOption1Value);
                                disableEditText(mOption2Value);
                                mScriptName.setText("");
                                mTimesValue.setText("1");
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.getListView().setSelection(1);
        alertDialog.show();
    }

    private void showListViewScripts() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.scripts)
                .setItems(R.array.scripts_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Log.d(LOG_TAG, "Selected option "+which);

                        scriptIndex = which;

                        switch (scriptIndex){
                            case 0:
                                mScriptName.setText("/sdcard/url.py");
                                mOption1Value.setText("-l 2000000");
                                mOption2Value.setText("-c 10");
                                mTimesValue.setText("1");
                                methodIndex = 1;
                                mScriptText.setText(methodName[methodIndex]+":");
                                runCurl = false;
                                break;
                            case 1:
                                mScriptName.setText("/sdcard/tcp_ping.py");
                                mOption1Value.setText("jepi.cs.pub.ro 1234");
                                mOption2Value.setText("-c 10");
                                mTimesValue.setText("1");
                                methodIndex = 1;
                                mScriptText.setText(methodName[methodIndex]+":");
                                runCurl = false;
                                break;
                            case 2:
                                mScriptName.setText("/sdcard/tfo_client.py");
                                mOption1Value.setText("");
                                mOption2Value.setText("");
                                mTimesValue.setText("1");
                                methodIndex = 1;
                                mScriptText.setText(methodName[methodIndex]+":");
                                runCurl = false;
                                break;
                            default:
                                mScriptName.setText(curl_cmd);
                                mOption1Value.setText("jepi.cs.pub.ro/test49.cap");
                                mOption2Value.setText("> /sdcard/test49.cap");
                                mTimesValue.setText("1");
                                methodIndex = 0;
                                mScriptText.setText(methodName[methodIndex]+":");
                                runCurl = true;
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.getListView().setSelection(0);
        alertDialog.show();
    }
}
