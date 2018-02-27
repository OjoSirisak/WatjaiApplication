package com.example.android.bluetoothlegatt.Manager;

import android.util.Log;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;
import org.json.JSONObject;


/**
 * Created by SirisakPks on 1/11/2560.
 */

public class MyNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler{
    @Override
    public void notificationReceived(OSNotification notification) {
        final int NOTIFICATION_ID = 0;
        JSONObject data = notification.payload.additionalData;
        String customKey;

        if (data != null) {
            customKey = data.optString("name", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
        }

        CounterNotification.getInstance().plusNotification();
    }
}
