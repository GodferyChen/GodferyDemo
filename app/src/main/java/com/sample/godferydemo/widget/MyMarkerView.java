package com.sample.godferydemo.widget;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.sample.godferydemo.R;

import java.util.Locale;

/**
 * Created by chen on 2016/4/19.
 */
public class MyMarkerView extends MarkerView{

    private TextView tvContent;

    public MyMarkerView(Context context, int layoutResource){
        super(context,layoutResource);

        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if(e instanceof CandleEntry){
            CandleEntry ce = (CandleEntry) e;
            tvContent.setText(Utils.formatNumber(ce.getHigh(),0,true));
        }else {
            tvContent.setText(String.format(Locale.getDefault(),"%1.2f",e.getVal()));
        }
    }

    @Override
    public int getXOffset(float xpos) {
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        return -getHeight();
    }
}
