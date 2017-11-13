package com.example.android.bluetoothlegatt.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.android.bluetoothlegatt.Dao.WatjaiMeasure;
import com.example.android.bluetoothlegatt.Manager.Contextor;
import com.example.android.bluetoothlegatt.Manager.CounterNotification;
import com.example.android.bluetoothlegatt.Manager.HttpManager;
import com.example.android.bluetoothlegatt.R;
import com.example.android.bluetoothlegatt.View.NotificationListItem;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    private ListView listView;
    private NotificationListAdapter notificationListAdapter;
    private ArrayList<WatjaiMeasure> watjaiMeasure;
    private SharedPreferences prefs;
    private String patId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefs = getSharedPreferences("loginStatus", MODE_PRIVATE);
        if (prefs != null) {
            patId = prefs.getString("PatID", "DEFAULT");
        }
        watjaiMeasure = new ArrayList<>();
        watjaiMeasure = (ArrayList<WatjaiMeasure>) getIntent().getSerializableExtra("notification");
        listView = (ListView) findViewById(R.id.notificationList);
        CounterNotification.getInstance().resetCountNotification();
        notificationListAdapter = new NotificationListAdapter();
        notificationListAdapter.addNotification(watjaiMeasure);
        listView.setAdapter(notificationListAdapter);
        listView.setOnItemClickListener(listNotification);
        notificationListAdapter = new NotificationListAdapter();
        if (watjaiMeasure == null) {
            NetworkCall task = new NetworkCall();
            task.execute();
            try {
                Thread.sleep(1000);
                listView.setAdapter(notificationListAdapter);
                listView.setOnItemClickListener(listNotification);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            notificationListAdapter.addNotification(watjaiMeasure);
            listView.setAdapter(notificationListAdapter);
            listView.setOnItemClickListener(listNotification);
        }


        /*t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                Call<ArrayList<WatjaiMeasure>> call = HttpManager.getInstance().getService().loadWatjaiMeasureAlert("PA1709001", "");
                                NetworkCall task = new NetworkCall();
                                task.execute(call);
                                notificationListAdapter = new NotificationListAdapter();
                                notificationListAdapter.addNotification(watjaiMeasure);
                                listView.setAdapter(notificationListAdapter);
                                listView.setOnItemClickListener(listNotification);
                            }
                        });
                        Thread.sleep(10000);
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();*/
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
    AdapterView.OnItemClickListener listNotification = new AdapterView.OnItemClickListener() {
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
    };

    /****
     *  inner class
     */

    private class NetworkCall extends AsyncTask<Call, Integer, ArrayList<WatjaiMeasure>> {

        @Override
        protected void onPostExecute(ArrayList<WatjaiMeasure> result) {
            watjaiMeasure = result;
            notificationListAdapter.addNotification(watjaiMeasure);
        }

        @Override
        protected ArrayList<WatjaiMeasure> doInBackground(Call... params) {

            try {
                Call<ArrayList<WatjaiMeasure>> call = HttpManager.getInstance().getService().loadWatjaiMeasureAlert(patId, "");
                Response<ArrayList<WatjaiMeasure>> response = call.execute();
                watjaiMeasure = response.body();
                return watjaiMeasure;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

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

            String year = watjaiMeasure.getAlertTime();
            String month = watjaiMeasure.getAlertTime();
            String day = watjaiMeasure.getAlertTime();

            year = year.substring(0,4);
            int yearr = Integer.parseInt(year) + 543;
            month = month.substring(5,7);
            day = day.substring(8,10);
            String time = watjaiMeasure.getAlertTime();
            time = time.substring(11,16);
            String dateNotification = day + "/"  + month +  "/" + yearr + " " + time;

            if (watjaiMeasure != null) {
                item.setTimeText(dateNotification);
                item.setDescriptionText(watjaiMeasure.getComment());
            }

            if  (watjaiMeasure.getReadStatus().equalsIgnoreCase("read")) {
                item.setReadStatus();
            }

            return item;
        }
    }
}