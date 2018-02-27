package com.example.android.bluetoothlegatt.Activity;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bluetoothlegatt.Dao.WatjaiMeasure;
import com.example.android.bluetoothlegatt.Manager.Contextor;
import com.example.android.bluetoothlegatt.Manager.HttpManager;
import com.example.android.bluetoothlegatt.R;
import com.example.android.bluetoothlegatt.View.NotificationListItem;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    private ListView notificationListView;
    private ArrayList<WatjaiMeasure> notifications;
    private NotificationListAdapter notificationListAdapter;
    private ProgressBar progressBar;
    private SharedPreferences prefs;
    private String patId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefs = getSharedPreferences("loginStatus", MODE_PRIVATE);
        if (prefs != null) {
            patId = prefs.getString("PatID", "DEFAULT");
        }
        notificationListView = (ListView) findViewById(R.id.notificationList);
        progressBar = (ProgressBar) findViewById(R.id.notiProgressBar);
        notificationListAdapter = new NotificationListAdapter();

        Call<ArrayList<WatjaiMeasure>> call = HttpManager.getInstance().getService().loadWatjaiMeasureAlert(patId, "");
        call.enqueue(new Callback<ArrayList<WatjaiMeasure>>() {
            @Override
            public void onResponse(Call<ArrayList<WatjaiMeasure>> call, Response<ArrayList<WatjaiMeasure>> response) {
                if (response.isSuccessful()) {
                    notifications = response.body();
                    notificationListAdapter.addNotification(notifications);
                    notificationListView.setAdapter(notificationListAdapter);
                    progressBar.setVisibility(View.GONE);
                    notificationListView.setOnItemClickListener(clickNotification);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<WatjaiMeasure>> call, Throwable throwable) {

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

    /***
     * variable
     */

    AdapterView.OnItemClickListener clickNotification = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            WatjaiMeasure notification = notifications.get(position);
            if (notification != null) {
                Intent intent = new Intent(getApplicationContext(), DescriptionNotificationActivity.class);
                notification.setMeasuringData(null);
                intent.putExtra("notification", notification);
                TextView textView = findViewById(R.id.tvNotificationDescription);
                ImageView imageView = (ImageView) view.findViewById(R.id.readStatus);
                if (!notification.getReadStatus()) {
                    imageView.setImageResource(R.drawable.bg_noti_read);
                    textView.setTextColor(Color.parseColor("#7d7777"));
                    Call<Object> call = HttpManager.getInstance().getService().changeReadStatus(notification.getMeasuringId());
                    call.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            if (response.isSuccessful()) {
                                System.out.println("success");
                            } else {
                                try {
                                    System.out.println(response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable throwable) {
                            System.out.println(throwable.toString());
                        }
                    });
                }
                startActivity(intent);
            }
        }
    };

    /****
     *  inner class
     */

    private class NotificationListAdapter extends BaseAdapter {
        ArrayList<WatjaiMeasure> notifications;

        public NotificationListAdapter() {
            super();
            notifications = new ArrayList<WatjaiMeasure>();
        }

        public void addNotification(ArrayList<WatjaiMeasure> notification) {
            if (notification != null) {
                notifications.addAll(0, notification);
            }

        }

        public WatjaiMeasure getNotification(int position) {
            return notifications.get(position);
        }

        @Override
        public int getCount() {
            return notifications.size();
        }

        @Override
        public WatjaiMeasure getItem(int position) {
            return notifications.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NotificationListItem item;

            if (convertView == null) {
                item = new NotificationListItem(Contextor.getInstance().getContext());
            } else {
                item = new NotificationListItem(parent.getContext());
            }

            WatjaiMeasure watjaiMeasure = notifications.get(position);

            String year = watjaiMeasure.getMeasuringTime();
            String month = watjaiMeasure.getMeasuringTime();
            String day = watjaiMeasure.getMeasuringTime();

            year = year.substring(0,4);
            int yearr = Integer.parseInt(year) + 543;
            month = month.substring(5,7);
            day = day.substring(8,10);
            String time = watjaiMeasure.getMeasuringTime();
            time = time.substring(11,16);
            String dateNotification = day + "/"  + month +  "/" + yearr + " " + time;

            if (watjaiMeasure != null) {
                item.setTimeText(dateNotification);
                item.setDescriptionText(watjaiMeasure.getComment());
            }

            if  (watjaiMeasure.getReadStatus()) {
                item.setColorDescriptionText("#7d7777");
                item.setReadStatus();
            } else {
                item.setColorDescriptionText("#ed1b24");
            }

            return item;
        }
    }
}