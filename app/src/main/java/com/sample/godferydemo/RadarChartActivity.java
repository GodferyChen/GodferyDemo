package com.sample.godferydemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.RadarChart;
import com.sample.godferydemo.handler.RadarChartHandler;

public class RadarChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar_chart);

        RadarChart mChart = (RadarChart) findViewById(R.id.chart);

        new RadarChartHandler(mChart).init();
    }
}
