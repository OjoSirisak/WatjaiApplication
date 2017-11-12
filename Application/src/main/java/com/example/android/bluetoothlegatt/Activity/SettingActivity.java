package com.example.android.bluetoothlegatt.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bluetoothlegatt.Bluetooth.DeviceScanActivity;
import com.example.android.bluetoothlegatt.GlobalService;
import com.example.android.bluetoothlegatt.R;

public class SettingActivity extends AppCompatActivity {

    private Button ScanButton;
    private Button DisconnectButton;
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ScanButton = (Button)findViewById(R.id.scan);
        DisconnectButton = (Button)findViewById(R.id.disconnect);
        text = (TextView)findViewById(R.id.textView2);

        if(GlobalService.mBluetoothLeService.getConnectState() == 2){
            text.setText(GlobalService.mBluetoothLeService.getDeviceName());
        }else{
            text.setText("Please Scan a Device first");
        }

        ScanButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent scanIntent = new Intent(getApplicationContext(), DeviceScanActivity.class);
                startActivity(scanIntent);
            }
        });

        DisconnectButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                GlobalService.mBluetoothLeService.disconnect();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
