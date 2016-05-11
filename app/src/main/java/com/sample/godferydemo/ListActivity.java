package com.sample.godferydemo;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import a.b.c.activity.BaseActivity;

/**
 * Created by chen on 2016/5/11.
 */
public class ListActivity extends BaseActivity{

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.layout_header_list);

        ListView listView = (ListView) findViewById(R.id.listView);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            list.add("嘿嘿"+i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout
                .simple_list_item_1,list);
        listView.setAdapter(adapter);
        LayoutAnimationController lac = new LayoutAnimationController(AnimationUtils
                .loadAnimation(mActivity,R.anim.zoom_in));
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        listView.setLayoutAnimation(lac);
        listView.startLayoutAnimation();
    }
}
