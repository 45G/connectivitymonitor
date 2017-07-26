package com.a45g.athena.connectivitymonitor;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.checked;

public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String LOG_TAG = "SettingsFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView mApnText;
    private CheckBox mRunShellScripts;
    private CheckBox mEnableMPTCP;
    private Button mSaveButton;



   // private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        mApnText = (EditText) rootView.findViewById(R.id.apn_to_watch);
        mRunShellScripts = (CheckBox) rootView.findViewById(R.id.run_shell_scripts);
        mEnableMPTCP = (CheckBox) rootView.findViewById(R.id.enableMPTCP);

        mSaveButton = (Button) rootView.findViewById(R.id.save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeApn();
                checkboxVerify();
            }
        });

        return rootView;
    }

    private void changeApn(){
        String oldApn = Singleton.getApn();
        String newApn = mApnText.getText().toString();
        if (!oldApn.equals(newApn)) {
            Singleton.setApn(newApn);
            Log.d(LOG_TAG, "Saved new APN value: " + newApn);
        }

    }

    private void checkboxVerify(){
        boolean checkboxMPTCP = mEnableMPTCP.isChecked();
        boolean checkboxScripts = mRunShellScripts.isChecked();
        boolean oldConfigMPTCP = Singleton.isMPTCPNeeded();
        boolean oldConfigScripts = Singleton.areScriptsNeeded();

        if (oldConfigMPTCP != checkboxMPTCP) {
            Singleton.setMPTCPNeeded(checkboxMPTCP);
            Log.d(LOG_TAG, "Saved new value - MPTCP needed: " + checked);
            if (checkboxMPTCP == false) {
                ConfigService.startActionMPTCPDisable(getContext());
                Toast.makeText(getContext(), "Disabled MPTCP",
                        Toast.LENGTH_SHORT).show();
            }
            else{
                ConfigService.startActionMPTCPEnable(getContext());
                Toast.makeText(getContext(), "Enabled MPTCP",
                        Toast.LENGTH_SHORT).show();
            }
        }
        if (oldConfigScripts != checkboxScripts) {
            Singleton.setScriptsNeeded(checkboxScripts);
            Log.d(LOG_TAG, "Saved new value - run config scripts: " + checked);
            if (checkboxScripts == false){
                ConfigService.startActionMobileDataDisable(getContext());
                ConfigService.startActionWiFiDisable(getContext());
                Toast.makeText(getContext(), "Disabled MPTCP configuration scripts",
                        Toast.LENGTH_SHORT).show();
            }
            else{
                ConfigService.startActionMobileDataEnable(getContext());
                ConfigService.startActionWifiEnable(getContext());
                Toast.makeText(getContext(), "Enabled MPTCP configuration scripts",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }


}
