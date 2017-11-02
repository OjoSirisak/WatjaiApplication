package com.example.android.bluetoothlegatt.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.android.bluetoothlegatt.Dao.WatjaiMeasure;
import com.example.android.bluetoothlegatt.Manager.Contextor;
import com.example.android.bluetoothlegatt.Manager.HttpManager;
import com.example.android.bluetoothlegatt.R;
import com.example.android.bluetoothlegatt.View.NotificationListItem;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    ListView listView;
    NotificationListAdapter notificationListAdapter;
    ArrayList<WatjaiMeasure> watjaiMeasure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        watjaiMeasure = new ArrayList<>();
        watjaiMeasure = (ArrayList<WatjaiMeasure>) getIntent().getSerializableExtra("notification");
        listView = (ListView) findViewById(R.id.notificationList);
        notificationListAdapter = new NotificationListAdapter();
        notificationListAdapter.addNotification(watjaiMeasure);
        listView.setAdapter(notificationListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WatjaiMeasure measure = notificationListAdapter.getItem(position);
                Intent intent = new Intent(NotificationActivity.this, DescriptionNotificationActivity.class);
                intent.putExtra("measure", measure);
                if (measure != null) {
                    if (measure.getReadStatus().equalsIgnoreCase("unread")) {
                        ImageView imageView = (ImageView) view.findViewById(R.id.readStatus);
                        imageView.setImageResource(R.drawable.bg_listnotificationpng);
                        measure.setId(null);
                        measure.setReadStatus("read");
                        Call<WatjaiMeasure> call = HttpManager.getInstance().getService().chageReadStatus(measure, measure.getMeasuringId());
                        call.enqueue(new Callback<WatjaiMeasure>() {
                            @Override
                            public void onResponse(Call<WatjaiMeasure> call, Response<WatjaiMeasure> response) {
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
                            public void onFailure(Call<WatjaiMeasure> call, Throwable throwable) {
                                System.out.println(throwable.toString());
                            }
                        });
                    }
                    startActivity(intent);
                }
            }
        });
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

class NotificationListAdapter extends BaseAdapter {
    ArrayList<WatjaiMeasure> notifications;

    public NotificationListAdapter() {
        super();
        notifications = new ArrayList<WatjaiMeasure>();
    }

    public void addTopNotification(ArrayList<WatjaiMeasure> newNotification) {
        if (newNotification != null) {
            notifications.addAll(notifications.size(), newNotification);
        }
    }

    public void addNotification(ArrayList<WatjaiMeasure> notification) {
        if (notification != null) {
            notifications.addAll(0, notification);
        }

    }

    public WatjaiMeasure getNotifaction(int position) {
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

        if (watjaiMeasure != null) {
            item.setTimeText(watjaiMeasure.getAlertTime());
            item.setDescriptionText(watjaiMeasure.getComment());
        }

        if  (watjaiMeasure.getReadStatus().equalsIgnoreCase("read")) {
            item.setReadStatus();
        }

        /*String dateStart = "11/03/14 09:29:58Z";
        String dateStop = watjaiMeasure.getAlertTime();

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

        return item;
    }
}