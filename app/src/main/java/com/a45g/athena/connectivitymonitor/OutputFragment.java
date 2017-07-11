package com.a45g.athena.connectivitymonitor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.R.attr.id;

public class OutputFragment extends Fragment {
    private static final String LOG_TAG = "OutputFragment";

    private TextView mOutputText = null;
    private View mScrollView = null;
    private RelativeLayout mLayout = null;

    private int mScrollPos;
    private int mMaxScrollPosition;
    private List<OutputData> mOutputCache = null;
    private Runnable mOutputRunnable = null;
    private Runnable checkScrollRunnable = null;

    private boolean ready = true;

    private long lastId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.output_fragment, container, false);

        mOutputText = (TextView) rootView.findViewById(R.id.outputText);
        mScrollView = (ScrollView) rootView.findViewById(R.id.outputScrollView);
        mLayout = (RelativeLayout) rootView.findViewById(R.id.mainRelative);

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
        loadPreviousEvents();
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

        this.setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume (){
        super.onResume();

        ready = true;
        Log.d(LOG_TAG, "OutputFragment is ready");

        if (id != -1) loadRecentEvents();
        appendOutput();

    }

    @Override
    public void onPause(){
        super.onPause();

        ready = false;
        Log.d(LOG_TAG, "OutputFragment is being paused");

    }

    private void loadRecentEvents(){
        List<ConnectivityOutput> eventsList = null;

        DatabaseOperations databaseOperations = new DatabaseOperations(getContext());
        databaseOperations.openRead();
        eventsList = databaseOperations.getRecentConnectivityOutputs(lastId);
        lastId = databaseOperations.getLastId();
        databaseOperations.close();

        Collections.reverse(eventsList);

        if (eventsList != null) {
            for (ConnectivityOutput event : eventsList) {
                OutputData outputData = new OutputData(event.getDetails(), event.getTimestamp());
                mOutputCache.add(outputData);
                Log.d(LOG_TAG, "Cache size=" + mOutputCache.size());
            }
        }

    }

    private void loadPreviousEvents(){
        List<ConnectivityOutput> eventsList = null;

        DatabaseOperations databaseOperations = new DatabaseOperations(getContext());
        databaseOperations.openRead();
        eventsList = databaseOperations.getSomeConnectivityOutputs(10);
        lastId = databaseOperations.getLastId();
        databaseOperations.close();

        if (eventsList != null) {
            for (ConnectivityOutput event : eventsList) {
                OutputData outputData = new OutputData(event.getDetails(), event.getTimestamp());
                mOutputCache.add(outputData);
                Log.d(LOG_TAG, "Cache size=" + mOutputCache.size());
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private synchronized void appendOutput() {
        if ((mOutputCache.size() == 0) || (ready == false)) {
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
            Log.d(LOG_TAG, "Scrolling with addition: " + (-added));
            mScrollView.scrollBy(0, -added);
        }
    }

    /*public void addOutput(String value, String time){

        OutputData output = new OutputData(value, time);
        if (mOutputCache != null && ready == true) {
            mOutputCache.add(output);
            Log.d(LOG_TAG, "Recv msg. Cache size="+mOutputCache.size());
            appendOutput();
        }
    }*/

    public void addOutput(long id){
        if (mOutputCache != null && ready == true) {
            lastId = id;
            DatabaseOperations databaseOperations = new DatabaseOperations(getContext());
            databaseOperations.openRead();
            ConnectivityOutput connectivityOutput = databaseOperations.getConnectivityOutputById(id);
            databaseOperations.close();

            if (connectivityOutput != null) {
                OutputData output = new OutputData(connectivityOutput.getDetails(), connectivityOutput.getTimestamp());

                mOutputCache.add(output);
                Log.d(LOG_TAG, "Recv msg. Cache size=" + mOutputCache.size());
                appendOutput();
            }
        }
        else{
            Log.d(LOG_TAG, "Recv msg. Not ready");
        }
    }


}
