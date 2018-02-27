package com.example.android.bluetoothlegatt.Manager;

import android.content.Context;

public class CounterNotification {

    private static CounterNotification instance;
    private int countNOti=0;
    public static CounterNotification getInstance() {
        if (instance == null)
            instance = new CounterNotification();
        return instance;
    }

    private Context mContext;

    private CounterNotification() {
        mContext = Contextor.getInstance().getContext();
    }

    public void plusNotification() {
        countNOti++;
    }
    public int getCountNotification() {
        return countNOti;
    }

    public  void resetCountNotification() {
        countNOti = 0;
    }


}
