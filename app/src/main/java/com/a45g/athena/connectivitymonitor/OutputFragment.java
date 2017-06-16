package com.a45g.athena.connectivitymonitor;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import java.util.List;

public class OutputFragment extends Fragment {

    private String tag = "Connectivity Info:";

    private TextView mOutputText = null;
    private View mScrollView = null;
    private RelativeLayout mLayout = null;

    private int mScrollPos;
    private int mMaxScrollPosition;
    private List<OutputData> mOutputCache = null;
    private Runnable mOutputRunnable = null;
    private Runnable checkScrollRunnable = null;

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


    @SuppressLint("SetTextI18n")
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
}
