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
import android.widget.ProgressBar;

import com.example.android.bluetoothlegatt.Dao.WatjaiMeasure;
import com.example.android.bluetoothlegatt.Fragment.MainFragment;
import com.example.android.bluetoothlegatt.Manager.Contextor;
import com.example.android.bluetoothlegatt.Manager.CounterNotification;
import com.example.android.bluetoothlegatt.Manager.HttpManager;
import com.example.android.bluetoothlegatt.R;
import com.example.android.bluetoothlegatt.View.HistoryListItem;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private ArrayList<WatjaiMeasure> history;
    private HistoryListAdapter historyListAdapter;
    private SharedPreferences prefs;
    private ProgressBar progressBar;
    private String patId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefs = getSharedPreferences("loginStatus", MODE_PRIVATE);
        if (prefs != null) {
            patId = prefs.getString("PatID", "DEFAULT");
        }
        historyListView = (ListView) findViewById(R.id.historyList);
        progressBar = findViewById(R.id.historyProgressBar);
        historyListAdapter = new HistoryListAdapter();

        Call<ArrayList<WatjaiMeasure>> call = HttpManager.getInstance().getService().loadHistory(patId);
        call.enqueue(new Callback<ArrayList<WatjaiMeasure>>() {
            @Override
            public void onResponse(Call<ArrayList<WatjaiMeasure>> call, Response<ArrayList<WatjaiMeasure>> response) {
                if (response.isSuccessful()) {
                    history = response.body();
                    historyListAdapter.addHistory(history);
                    progressBar.setVisibility(View.GONE);
                    historyListView.setAdapter(historyListAdapter);
                    historyListView.setOnItemClickListener(clickHistory);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<WatjaiMeasure>> call, Throwable throwable) {

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

    /***
     *  Variable
     */

    AdapterView.OnItemClickListener clickHistory = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView imageView = findViewById(R.id.heartRateStatus);
            WatjaiMeasure historyDetail = history.get(position);
            if (historyDetail != null) {
                Intent intent = new Intent(HistoryActivity.this, HistoryDetailActivity.class);
                intent.putExtra("historyDetail", historyDetail.getMeasuringId());
                intent.putExtra("historyData", historyDetail);
                startActivity(intent);
            }
        }
    };

    /***
     *  Inner class
     */

    private class HistoryListAdapter extends BaseAdapter {
        ArrayList<WatjaiMeasure> historys;

        public HistoryListAdapter() {
            super();
            historys = new ArrayList<WatjaiMeasure>();
        }

        public void addHistory(ArrayList<WatjaiMeasure> history) {
            if (history != null) {
                historys.addAll(0, history);
            }
        }

        public WatjaiMeasure getHistory(int position) {
            return historys.get(position);
        }

        @Override
        public int getCount() {
            return historys.size();
        }

        @Override
        public WatjaiMeasure getItem(int position) {
            return historys.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HistoryListItem item;

            if (convertView == null) {
                item = new HistoryListItem(Contextor.getInstance().getContext());
            } else {
                item = new HistoryListItem(parent.getContext());
            }

            WatjaiMeasure watjaiMeasure = historys.get(position);
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
                item.setMeasuringId(watjaiMeasure.getMeasuringId());
                item.setHeartRate(watjaiMeasure.getHeartRate()+" ครั้ง/นาที");
                item.setDateTime(dateNotification);
            }

            if (watjaiMeasure.getHeartRate() < 60) {
                item.setImageView(R.drawable.history_low);
            } else if (watjaiMeasure.getHeartRate() > 200) {
                item.setImageView(R.drawable.history_fast);
            } else if (watjaiMeasure.getHeartRate() > 60 && watjaiMeasure.getHeartRate() < 200 && watjaiMeasure.getMeasuringId().indexOf("ME") != -1) {
                item.setImageView(R.drawable.history_abnormal);
            }

            return item;
        }
    }

}




