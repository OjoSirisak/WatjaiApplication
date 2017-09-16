package com.example.android.bluetoothlegatt.Activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.android.bluetoothlegatt.Bluetooth.DeviceScanActivity;
import com.example.android.bluetoothlegatt.Fragment.MainFragment;
import com.example.android.bluetoothlegatt.Manager.Contextor;
import com.example.android.bluetoothlegatt.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static boolean isLoginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        isLoginStatus = getIntent().getBooleanExtra("status", false);


        if (isLoginStatus) {
            if (savedInstanceState == null) {
                setContentView(R.layout.activity_main);
            }
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }


        buttonProfile = (ImageButton) findViewById(R.id.btnProfile);
        buttonMeasure = (ImageButton) findViewById(R.id.btnMeasure);
        buttonHistory = (ImageButton) findViewById(R.id.btnHistory);
        buttonSetting = (ImageButton) findViewById(R.id.btnSetting);
        buttonHelp = (ImageButton) findViewById(R.id.btnHelp);
        buttonLogout = (ImageButton) findViewById(R.id.btnLogout);

        //isConnectBluetooth = getIntent().getBooleanExtra("bluetoothStatus", false);

        buttonMeasure.setOnClickListener(this);
        buttonProfile.setOnClickListener(this);

    }

    private ImageButton buttonProfile, buttonMeasure, buttonHistory, buttonSetting, buttonHelp, buttonLogout;
    private boolean isConnectBluetooth = false;






    @Override
    public void onClick(View v) {
        if (v == buttonProfile) {
            Intent intent = new Intent(Contextor.getInstance().getContext(), ProfileActivity.class);
            startActivity(intent);
        } else if (v == buttonMeasure) {
            /*if (isConnectBluetooth) {
                Intent intent = new Intent(getContext(), DeviceControlActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), DeviceScanActivity.class);
                startActivity(intent);
            }*/
            Intent intent = new Intent(Contextor.getInstance().getContext(), DeviceScanActivity.class);
            startActivity(intent);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("status", isLoginStatus);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isLoginStatus = savedInstanceState.getBoolean("status");
    }
}
