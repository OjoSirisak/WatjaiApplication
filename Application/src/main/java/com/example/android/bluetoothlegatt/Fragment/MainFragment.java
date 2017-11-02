package com.example.android.bluetoothlegatt.Fragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.bluetoothlegatt.Activity.BlankNotificationActivity;
import com.example.android.bluetoothlegatt.Activity.DescriptionNotificationActivity;
import com.example.android.bluetoothlegatt.Activity.HistoryActivity;
import com.example.android.bluetoothlegatt.Activity.NotificationActivity;
import com.example.android.bluetoothlegatt.Activity.ProfileActivity;
import com.example.android.bluetoothlegatt.Bluetooth.DeviceControlActivity;
import com.example.android.bluetoothlegatt.Bluetooth.DeviceScanActivity;
import com.example.android.bluetoothlegatt.Dao.WatjaiMeasure;
import com.example.android.bluetoothlegatt.Manager.Contextor;
import com.example.android.bluetoothlegatt.Manager.CounterNotification;
import com.example.android.bluetoothlegatt.Manager.HttpManager;
import com.example.android.bluetoothlegatt.R;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;



/**
 * Created by nuuneoi on 11/16/2014.
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    private ImageButton buttonProfile, buttonMeasure, buttonHistory, buttonSetting, buttonHelp, buttonNotification;
    private TextView countNotification;
    private boolean isBluetoothStatus = false;
    private ArrayList<WatjaiMeasure> watjaiMeasure;
    Thread t;
    private int countNoti;

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
        watjaiMeasure = new ArrayList<WatjaiMeasure>();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        buttonProfile = (ImageButton) rootView.findViewById(R.id.btnProfile);
        buttonMeasure = (ImageButton) rootView.findViewById(R.id.btnMeasure);
        buttonHistory = (ImageButton) rootView.findViewById(R.id.btnHistory);
        buttonSetting = (ImageButton) rootView.findViewById(R.id.btnSetting);
        buttonHelp = (ImageButton) rootView.findViewById(R.id.btnHelp);
        buttonNotification = (ImageButton) rootView.findViewById(R.id.btnNotification);
        countNotification = (TextView) rootView.findViewById(R.id.countNotification);

        buttonMeasure.setOnClickListener(this);
        buttonProfile.setOnClickListener(this);
        buttonHistory.setOnClickListener(this);
        buttonNotification.setOnClickListener(this);
        buttonHelp.setOnClickListener(this);

        t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        countNoti = CounterNotification.getInstance().getCountNotification();
                        if  (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // update TextView here!
                                    if (countNoti>0) {
                                        showCountNotification();
                                    } else {
                                        hideCountNotification();
                                    }
                                    Call<ArrayList<WatjaiMeasure>> call = HttpManager.getInstance().getService().loadWatjaiMeasureAlert("PA1709001","");
                                    NetworkCall task = new NetworkCall();
                                    task.execute(call);
                                }
                            });
                        } else {
                            isInterrupted();
                        }
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

        /*String dateStart = "11/03/14 09:29:58";
        String dateStop = "11/03/14 09:33:43";

        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Get msec from each, and subtract.
        long diff = d2.getTime() - d1.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        long diffDay = diff / (24 * 60 * 60 * 1000);
        System.out.println("Time in seconds: " + diffSeconds + " seconds.");
        System.out.println("Time in minutes: " + diffMinutes + " minutes.");
        System.out.println("Time in hours: " + diffHours + " hours.");
        System.out.println("Time in Day: " + diffDay + " days.");*/
    }

    @Override
    public void onResume() {
        super.onResume();
        if (countNoti>0) {
            showCountNotification();
        } else {
            hideCountNotification();
        }
    }

    private void checkAlert() {
        /*Call<ArrayList<WatjaiMeasure>> call = HttpManager.getInstance().getService().loadWatjaiMeasureAlert("PA1709001","");
        NetworkCallRefresh refresh = new NetworkCallRefresh();
        NetworkCall task = new NetworkCall();
        refresh.execute(call);
        task.execute(call);*/
        /*call.enqueue(new Callback<ArrayList<WatjaiMeasure>>() {
            @Override
            public void onResponse(Call<ArrayList<WatjaiMeasure>> call, Response<ArrayList<WatjaiMeasure>> response) {
                if (response.isSuccessful()) {
                    watjaiMeasure = response.body();
                    if (watjaiMeasure.size() > 0) {
                        countNoti = watjaiMeasure.size();
                        lastIdMeasure = watjaiMeasure.get(countNoti-1).getMeasuringId();
                        showCountNotification();
                    }
                } else {
                    try {
                        Toast.makeText(getContext(),
                                response.errorBody().string(),
                                Toast.LENGTH_SHORT)
                                .show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<WatjaiMeasure>> call, Throwable throwable) {
                if (Locale.getDefault().getLanguage().equals("th")) {
                    Toast.makeText(getContext(), "กรุณาเชื่อมต่ออินเทอร์เน็ต", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Disconnect internet", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

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
        } else if (v == buttonNotification) {
            if  (watjaiMeasure.size() > 0 ) {
                Intent noti = new Intent(getContext(), NotificationActivity.class);
                noti.putExtra("notification", watjaiMeasure);
                startActivity(noti);
                countNoti = 0;
                CounterNotification.getInstance().resetCountNotification();
            } else {
                Intent blank = new Intent(getContext(), BlankNotificationActivity.class);
                startActivity(blank);
            }
        } else if (v == buttonHelp) {

        }

    }

    private void showCountNotification() {
        countNotification.setVisibility(View.VISIBLE);
        countNotification.setText(countNoti+"");
    }

    private void hideCountNotification() {
        countNotification.setVisibility(View.GONE);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance (Fragment level's variables) State here
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance (Fragment level's variables) State here
    }

    /*private class NetworkCallRefresh extends AsyncTask<Call, Integer, ArrayList<WatjaiMeasure>> {
        int refreshTime=100;

        @Override
        protected void onPostExecute(ArrayList<WatjaiMeasure> result) {
            watjaiMeasure = result;
        }

        @Override
        protected ArrayList<WatjaiMeasure> doInBackground(Call... params) {

            try {
                for (int i=0; i<refreshTime; i++) {
                    Call<ArrayList<WatjaiMeasure>> call = HttpManager.getInstance().getService().loadWatjaiMeasureAlert("PA1709001","");
                    Response<ArrayList<WatjaiMeasure>> response = call.execute();
                    if (response.body() != null) {
                        watjaiMeasure = response.body();
                        //countNoti = watjaiMeasure.size();
                        Thread.sleep(1000);
                    }
                }
                return watjaiMeasure;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

    }*/

    private class NetworkCall extends AsyncTask<Call, Integer, ArrayList<WatjaiMeasure>> {

        @Override
        protected void onPostExecute(ArrayList<WatjaiMeasure> result) {
            watjaiMeasure = result;
        }

        @Override
        protected ArrayList<WatjaiMeasure> doInBackground(Call... params) {

            try {
                Call<ArrayList<WatjaiMeasure>> call = HttpManager.getInstance().getService().loadWatjaiMeasureAlert("PA1709001","");
                Response<ArrayList<WatjaiMeasure>> response = call.execute();
                watjaiMeasure = response.body();
                return watjaiMeasure;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}


