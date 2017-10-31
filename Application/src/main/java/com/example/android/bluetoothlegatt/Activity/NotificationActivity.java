package com.example.android.bluetoothlegatt.Activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.Dao.PatientItemDao;
import com.example.android.bluetoothlegatt.Dao.WatjaiMeasure;
import com.example.android.bluetoothlegatt.Manager.Contextor;
import com.example.android.bluetoothlegatt.Manager.HttpManager;
import com.example.android.bluetoothlegatt.R;
import com.example.android.bluetoothlegatt.View.NotificationListItem;
import com.onesignal.OneSignal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

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

        return item;
    }
}