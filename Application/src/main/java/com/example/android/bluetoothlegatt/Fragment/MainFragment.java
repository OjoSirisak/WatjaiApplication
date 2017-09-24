package com.example.android.bluetoothlegatt.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.android.bluetoothlegatt.Activity.HistoryActivity;
import com.example.android.bluetoothlegatt.Activity.MainActivity;
import com.example.android.bluetoothlegatt.Activity.ProfileActivity;
import com.example.android.bluetoothlegatt.Bluetooth.DeviceControlActivity;
import com.example.android.bluetoothlegatt.Bluetooth.DeviceScanActivity;
import com.example.android.bluetoothlegatt.MainApplication;
import com.example.android.bluetoothlegatt.Manager.Contextor;
import com.example.android.bluetoothlegatt.R;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    ImageButton buttonProfile, buttonMeasure, buttonHistory, buttonSetting, buttonHelp, buttonLogout;
    private boolean isBluetoothStatus = false;

    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState

        buttonProfile = (ImageButton) rootView.findViewById(R.id.btnProfile);
        buttonMeasure = (ImageButton) rootView.findViewById(R.id.btnMeasure);
        buttonHistory = (ImageButton) rootView.findViewById(R.id.btnHistory);
        buttonSetting = (ImageButton) rootView.findViewById(R.id.btnSetting);
        buttonHelp = (ImageButton) rootView.findViewById(R.id.btnHelp);
        buttonLogout = (ImageButton) rootView.findViewById(R.id.btnLogout);

        buttonMeasure.setOnClickListener(this);
        buttonProfile.setOnClickListener(this);
        buttonHistory.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == buttonProfile) {
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            startActivity(intent);
        } else if (v == buttonMeasure) {
            Intent intent = new Intent(getContext(), DeviceScanActivity.class);

            if (isBluetoothStatus) {
                Intent watjai = new Intent(getContext(), DeviceControlActivity.class);
                startActivity(watjai);
            } else {
                startActivity(intent);
            }
        } else if (v == buttonHistory) {
            Intent intent = new Intent(getContext(), HistoryActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance (Fragment level's variables) State here
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance (Fragment level's variables) State here
        //isBluetoothStatus = getActivity().getIntent().getBooleanExtra("bluetoothStatus", false);
    }

}
