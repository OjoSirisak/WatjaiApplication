package com.example.android.bluetoothlegatt.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.android.bluetoothlegatt.Dao.WatjaiMeasure;
import com.example.android.bluetoothlegatt.R;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class HistoryDetailActivity extends AppCompatActivity {
    private String measuringId;
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    private int totalEcg = 0;
    private WatjaiMeasure dao;
    private GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        measuringId = getIntent().getStringExtra("historyDetail");
        dao =  getIntent().getParcelableExtra("historyData");
        graph = (GraphView) findViewById(R.id.historyGraph);
        series = new LineGraphSeries<DataPoint>();
        if (dao != null) {
            totalEcg = dao.getMeasuringData().size();
            for (int l = 0; l < totalEcg; l++) {
                series.appendData(new DataPoint(lastX++, dao.getMeasuringData().get(l)), true, totalEcg);
            }
        }
        graph.addSeries(series);
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(5);
        viewport.setMinX(0);
        viewport.setMaxX(700);
        viewport.setScrollable(true);
        graph.getViewport().setScalableY(true);
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
