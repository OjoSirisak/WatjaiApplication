package com.example.android.bluetoothlegatt.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.bluetoothlegatt.Dao.WatjaiMeasure;
import com.example.android.bluetoothlegatt.R;

public class DescriptionNotificationActivity extends AppCompatActivity {

    private WatjaiMeasure watjaiMeasure;
    TextView tvDate, tvTime, tvComment;
    String date, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_notification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvComment = (TextView) findViewById(R.id.tvComment);
        watjaiMeasure = new WatjaiMeasure();
        watjaiMeasure = getIntent().getParcelableExtra("notification");
        tvComment.setText(watjaiMeasure.getComment());

        String year = watjaiMeasure.getMeasuringTime();
        String month = watjaiMeasure.getMeasuringTime();
        String day = watjaiMeasure.getMeasuringTime();

        year = year.substring(0,4);
        int yearr = Integer.parseInt(year) + 543;
        month = month.substring(5,7);
        day = day.substring(8,10);
        String time = watjaiMeasure.getMeasuringTime();
        time = time.substring(11,16);
        String dateNotification = day + "/"  + month +  "/" + yearr;
        tvDate.setText(dateNotification);
        tvTime.setText(time);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
