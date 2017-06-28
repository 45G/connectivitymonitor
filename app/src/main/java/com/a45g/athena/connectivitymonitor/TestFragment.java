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

public class TestFragment extends Fragment {

    private static final String LOG_TAG = TestFragment.class.getName();

    private Button mChooseType = null;
    private TextView mScriptText = null;
    private EditText mScriptName = null;
    private TextView mDomainText = null;
    private EditText mDomainName = null;
    private TextView mPortText = null;
    private EditText mPortValue = null;
    private TextView mTimesText = null;
    private EditText mTimesValue = null;

    private TextView mResultText = null;


    private Button mStartTest = null;
    private Button mStopTest = null;
    private Button mClearOutput = null;

    private TextView mResult = null;
    private LinearLayout mLayout = null;
    private View mScrollView = null;

    private int mScrollPos;
    private int mMaxScrollPosition;
    private Runnable checkScrollRunnable = null;

    private String[] methods;
    private int methodIndex = 1;
    private String[] methodName;

    private ExecuteTask task = null;
    private boolean cancel = false;
    private int times = 0;
    private String mCmd = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.test_fragment, container, false);

        methods = new String[]{"", "/data/user/0/org.qpython.qpy/files/bin/qpython-android5.sh ", "sh "};
        methodName = getResources().getStringArray(R.array.execute_array);

        mChooseType = (Button) rootView.findViewById(R.id.chooseType);

        mScriptText = (TextView) rootView.findViewById(R.id.scriptText);
        mScriptName = (EditText) rootView.findViewById(R.id.scriptName);

        mDomainText = (TextView) rootView.findViewById(R.id.domainText);
        mDomainName = (EditText) rootView.findViewById(R.id.domainName);

        mPortText = (TextView) rootView.findViewById(R.id.portText);
        mPortValue = (EditText) rootView.findViewById(R.id.portValue);

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
                showListView();
            }
        });

        mStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Starting text execution");

                mResult.setText("");

                if (methodIndex == 1){
                    mCmd = methods[methodIndex] +
                            mScriptName.getText() + " " + mDomainName.getText() + " " +
                            mPortValue.getText() + " && exit";
                    Log.d(LOG_TAG, "CMD: "+mCmd);
                }
                else{
                    mCmd = methods[methodIndex] +
                            mScriptName.getText();
                    Log.d(LOG_TAG, "CMD: "+mCmd);
                }

                times = Integer.valueOf(mTimesValue.getText().toString());

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

            String output = "";

            String result = sudoForResult(params[0]);
            String[] lineTokens = result.split("\n");
            //String[] tokens = lineTokens[0].split(" ");
            //if (tokens.length >= 5)
            //    Log.d(LOG_TAG, tokens[4]+" ms");

            if (methodIndex == 1){
                output = lineTokens[0];
            }
            else{
                output = result;
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

    private void showListView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.execute)
                .setItems(R.array.execute_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        Log.d(LOG_TAG, "Selected option "+which);
                        methodIndex = which;
                        mScriptText.setText(methodName[methodIndex]+":");
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.getListView().setSelection(1);
        alertDialog.show();
    }
}
