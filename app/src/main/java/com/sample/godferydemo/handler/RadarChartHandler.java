package com.sample.godferydemo.handler;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * 六要素图表
 * Created by chen on 2016/4/19.
 */
public class RadarChartHandler {

    private RadarChart mChart;
    private String[] mParties = new String[]{"心肺", "速度", "灵敏", "耐力", "柔韧"};

    public RadarChartHandler(RadarChart chart) {
        this.mChart = chart;
    }

    public void init() {
        mChart.setDescription("");
        mChart.setWebLineWidth(1f);
        mChart.setWebLineWidthInner(0.75f);
        mChart.setWebAlpha(100);
        mChart.setWebColor(ColorTemplate.rgb("#ffffff"));
        mChart.setWebColorInner(ColorTemplate.rgb("#ffffff"));
        //设置高亮
        mChart.setHighlightPerTapEnabled(false);

        setData();

        mChart.animateXY(1400, 1400, Easing.EasingOption.EaseInOutQuad, Easing.EasingOption.EaseInOutQuad);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setTextColor(ColorTemplate.rgb("#ffffff"));
        xAxis.setSpaceBetweenLabels(4);

        YAxis yAxis = mChart.getYAxis();
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinValue(0f);
        yAxis.setDrawGridLines(false);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setEnabled(false);

        mChart.getYAxis().setEnabled(false);
    }

    public void setData() {
        float mult = 150;
        int cnt = 5;

        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals = new ArrayList<>();

        for (int i = 0; i < cnt; i++) {
            yVals.add(new Entry((float) (Math.random() * mult) + mult / 2, i));
            xVals.add(mParties[i % mParties.length]);
        }

        RadarDataSet set = new RadarDataSet(yVals,"set");
        set.setColor(ColorTemplate.rgb("#D7524B"));
        set.setFillColor(ColorTemplate.rgb("#D7524B"));
        set.setDrawFilled(true);
        set.setLineWidth(2f);

        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(set);

        RadarData data = new RadarData(xVals,sets);
        data.setValueTextSize(8f);
        data.setDrawValues(false);

        mChart.setData(data);

        mChart.invalidate();
    }

}
