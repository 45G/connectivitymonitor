package com.a45g.athena.connectivitymonitor;

import android.content.Context;
import android.content.Intent;
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
        mRunShellScripts.setOnClickListener(checkboxClickListener);

        mSaveButton = (Button) rootView.findViewById(R.id.save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeApn();
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

    private void changeRoot(){
        Boolean oldRootP = Singleton.hasRootPermission();
        Boolean newRootP = mRunShellScripts.isChecked();
        if (oldRootP == newRootP) {
            Singleton.setRootPermission(newRootP);
            Log.d(LOG_TAG, "Saved new root permission: " + newRootP);
        }
    }

    View.OnClickListener checkboxClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean checked = ((CheckBox) view).isChecked();
            switch (view.getId()){
                case R.id.run_shell_scripts:
                    Boolean oldRootP = Singleton.hasRootPermission();
                    if (oldRootP != checked) {
                        Singleton.setRootPermission(checked);
                        Log.d(LOG_TAG, "Saved new root permission: " + checked);
                        if (checked == true){
                            getActivity().stopService(new Intent(getContext(), ConfigService.class));
                            getActivity().startService(new Intent(getContext(), ConfigService.class));
                        }
                    }
                    break;
            }
        }
    };


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
