/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothlegatt.Bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.Dao.WatjaiMeasureSendData;
import com.example.android.bluetoothlegatt.Dao.WatjaiNormal;
import com.example.android.bluetoothlegatt.GlobalService;
import com.example.android.bluetoothlegatt.Manager.HttpManager;
import com.jjoe64.graphview.series.DataPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.bluetoothlegatt.GlobalService.ecgData;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private String mBluetoothDeviceName;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    private WatjaiNormal watjaiNormal;
    private WatjaiMeasureSendData watjaiMeasureSendData;
    private SharedPreferences prefs;
    private String patId;

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data));
                collectData(new String(data));
            }
        }
        sendBroadcast(intent);
    }
    float ecg;
    int numCount =0;
    String temp = "";

    private void collectData(String data){
        if(GlobalService.ecgData == null){
            GlobalService.ecgData = new ArrayList<Float>();
        }
        try{
            if(numCount > 0){
                temp += data.substring(0,numCount);
                ecg = Float.parseFloat(temp);
                GlobalService.ecgData.add(ecg);
                temp = "";
                numCount = 0;
            }

            int i = data.indexOf(';');
            while(i >= 0){
                int k = data.length();
                System.out.println("i : " + i + "//k : " + k);
                if(i+4 <= k-1){

                    ecg = Float.parseFloat(data.substring(i+1,i+5));
                    GlobalService.ecgData.add(ecg);
                }else{
                    temp = data.substring(i+1,k);
                    numCount = 4-temp.length();
                }
                i = data.indexOf(';',i+1);
            }
            Log.e(TAG,"ECG SIZE : " + ecgData.size());
            if(GlobalService.ecgData.size() > 9600){
                sendEcgData();
                GlobalService.ecgData = null;
            }

        }catch(Exception e){
            Log.e(TAG,"error : " + e + "\n DATA : " + data);
        }


    }

    private void sendEcgData() {
        prefs = getSharedPreferences("loginStatus", MODE_PRIVATE);
        if (prefs != null) {
            patId = prefs.getString("PatID", "DEFAULT");
        }

        if(watjaiNormal!=null){
            watjaiNormal = null;
        }
        if(watjaiMeasureSendData!=null){
            watjaiMeasureSendData = null;
        }
        watjaiNormal = new WatjaiNormal();
        watjaiNormal.setMeasureData(GlobalService.ecgData);
        watjaiNormal.setPatId(patId);
        watjaiNormal.setMeasureId(null);
        watjaiNormal.setMeasureTime(null);
        watjaiNormal.setId(null);

        watjaiMeasureSendData = new WatjaiMeasureSendData();
        watjaiMeasureSendData.setMeasuringData(GlobalService.ecgData);
        watjaiMeasureSendData.setPatId(patId);

        Call<WatjaiNormal> call = HttpManager.getInstance().getService().insertECG(watjaiNormal);
        call.enqueue(new Callback<WatjaiNormal>() {
            @Override
            public void onResponse(Call<WatjaiNormal> call, Response<WatjaiNormal> response) {
                if (response.isSuccessful()) {

                } else {

                }
            }

            @Override
            public void onFailure(Call<WatjaiNormal> call, Throwable throwable) {

            }
        });

        Call<WatjaiMeasureSendData> detecing = HttpManager.getInstance().getService().insertECGtoDetecing(watjaiMeasureSendData);
        detecing.enqueue(new Callback<WatjaiMeasureSendData>() {
            @Override
            public void onResponse(Call<WatjaiMeasureSendData> call, Response<WatjaiMeasureSendData> response) {
                if (response.isSuccessful()) {

                } else {

                }
            }

            @Override
            public void onFailure(Call<WatjaiMeasureSendData> call, Throwable throwable) {

            }
        });
    }


    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    public final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public int getConnectState(){
        return mConnectionState;
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        resetDevice();
    }

    public void resetDevice(){
        mBluetoothDeviceAddress = null;
        mBluetoothDeviceName = null;
    }
    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic){
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
        return true;
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    public BluetoothGattService getUUID(){
        if(mBluetoothGatt == null) return null;

        return mBluetoothGatt.getService(UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB"));
    }

    public void setDevice(BluetoothDevice device){
        mBluetoothDeviceAddress = device.getAddress();
        mBluetoothDeviceName = device.getName();
    }

    public String getDeviceAddress(){
        return mBluetoothDeviceAddress;
    }
    public String getDeviceName(){
        return mBluetoothDeviceName;
    }

    public void doStartService(){
        BluetoothGattService serv = getUUID();
        if(serv == null){
            Log.e(TAG,"service not found");
        }

        BluetoothGattCharacteristic charac = serv.getCharacteristic(UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB"));
        if (charac == null) {
            Log.e(TAG, "char not found!");

        }

        if(mNotifyCharacteristic != null){
            setCharacteristicNotification(mNotifyCharacteristic, false);
            mNotifyCharacteristic = null;
        }
        if(BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0){
            mNotifyCharacteristic = charac;
           setCharacteristicNotification(charac, true);
        }

        charac.setValue("C");
        boolean status = writeCharacteristic(charac);
        Log.e(TAG,"Write Status : " + status);



        // mBluetoothLeService.readCharacteristic(charac);



    }

    public void doStopService(){
        BluetoothGattService serv = getUUID();
        if(serv == null){
            Log.e(TAG,"service not found");
        }

        BluetoothGattCharacteristic charac = serv.getCharacteristic(UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB"));
        if (charac == null) {
            Log.e(TAG, "char not found!");

        }

        if(mNotifyCharacteristic != null){
           setCharacteristicNotification(mNotifyCharacteristic, false);
            mNotifyCharacteristic = null;
        }
        if(BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0){
            mNotifyCharacteristic = charac;
            setCharacteristicNotification(charac, false);
        }

        charac.setValue("F");
        boolean status = writeCharacteristic(charac);
        Log.e(TAG,"Write Status : " + status);
    }


}
