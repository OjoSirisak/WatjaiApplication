package com.example.android.bluetoothlegatt;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.android.bluetoothlegatt.Manager.Contextor;
import com.example.android.bluetoothlegatt.Manager.MyNotificationOpenedHandler;
import com.example.android.bluetoothlegatt.Manager.MyNotificationReceivedHandler;
import com.onesignal.OneSignal;


/**
 * Created by User on 20/3/2560.
 */

public class MainApplication extends Application {

    private SharedPreferences prefs;
    private String patId;

    @Override
    public void onCreate() {
        super.onCreate();

        // Restore Singleton data here

        // initialize ting(s) here
        prefs = getSharedPreferences("loginStatus", MODE_PRIVATE);
        if (prefs != null) {
            patId = prefs.getString("PatID", "DEFAULT");
            System.out.println(patId);
        }
        Contextor.getInstance().init(getApplicationContext());


        OneSignal.startInit(getApplicationContext())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .setNotificationOpenedHandler(new MyNotificationOpenedHandler())
                .setNotificationReceivedHandler(new MyNotificationReceivedHandler())
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        OneSignal.sendTag("patId", patId);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // Save Singleton data here

    }




}
