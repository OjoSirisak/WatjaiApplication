package com.example.android.bluetoothlegatt.Manager;

import android.content.Intent;
import android.util.Log;

import com.example.android.bluetoothlegatt.Activity.HelpMeActivity;
import com.example.android.bluetoothlegatt.Activity.NotificationActivity;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by SirisakPks on 1/11/2560.
 */

public class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        CounterNotification.getInstance().resetCountNotification();
        String notificationFrom, relativeTel, relativeName;

        if (data != null) {
            notificationFrom = data.optString("from", null);
            if (notificationFrom != null && notificationFrom.equalsIgnoreCase("measure")) {
                relativeName = data.optString("name", "");
                relativeTel = data.optString("tel", "");
                Intent intent = new Intent(Contextor.getInstance().getContext(), HelpMeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("relativeName", relativeName);
                intent.putExtra("relativeTel", relativeTel);
                Contextor.getInstance().getContext().startActivity(intent);
            } else {
                Intent intent = new Intent(Contextor.getInstance().getContext(), NotificationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                Contextor.getInstance().getContext().startActivity(intent);

            }
        } else {
            Intent intent = new Intent(Contextor.getInstance().getContext(), NotificationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            Contextor.getInstance().getContext().startActivity(intent);
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);


    }
}
