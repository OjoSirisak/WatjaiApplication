package com.example.android.bluetoothlegatt;

import android.app.Application;

import com.example.android.bluetoothlegatt.Bluetooth.BluetoothLeService;
import java.util.ArrayList;

/**
 * Created by USER on 12/11/2560.
 */

public class GlobalService extends Application {
    public static BluetoothLeService mBluetoothLeService;
    public static ArrayList<Float> ecgData;
}
