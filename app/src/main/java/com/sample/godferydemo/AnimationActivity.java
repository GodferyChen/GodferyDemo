package com.sample.godferydemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import a.b.c.activity.BaseActivity;

public class AnimationActivity extends BaseActivity implements View.OnClickListener {

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        image = (ImageView) findViewById(R.id.image);
        findViewById(R.id.scale).setOnClickListener(this);
        findViewById(R.id.rotate).setOnClickListener(this);
        findViewById(R.id.translate).setOnClickListener(this);
        findViewById(R.id.alpha).setOnClickListener(this);
        findViewById(R.id.continue_btn).setOnClickListener(this);
        findViewById(R.id.continue_btn2).setOnClickListener(this);
        findViewById(R.id.flash).setOnClickListener(this);
        findViewById(R.id.move).setOnClickListener(this);
        findViewById(R.id.change).setOnClickListener(this);
        findViewById(R.id.layout).setOnClickListener(this);
        findViewById(R.id.frame).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Animation loadAnimation;
        switch (v.getId()){
            case R.id.scale://缩放
                loadAnimation = AnimationUtils.loadAnimation(mActivity,R.anim.scale);
                image.startAnimation(loadAnimation);
                break;
            case R.id.rotate://旋转
                loadAnimation = AnimationUtils.loadAnimation(mActivity,R.anim.rotate);
                image.startAnimation(loadAnimation);
                break;
            case R.id.translate://位移
                loadAnimation = AnimationUtils.loadAnimation(mActivity,R.anim.translate);
                image.startAnimation(loadAnimation);
                break;
            case R.id.alpha://透明
                loadAnimation = AnimationUtils.loadAnimation(mActivity,R.anim.translate);
                image.startAnimation(loadAnimation);
                break;
            case R.id.continue_btn:
                loadAnimation = AnimationUtils.loadAnimation(mActivity,R.anim.translate);
                final Animation animation0 = AnimationUtils.loadAnimation(mActivity,R.anim.rotate);
                loadAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        image.startAnimation(animation0);
                    }
                });
                break;
            case R.id.continue_btn2:
                loadAnimation = AnimationUtils.loadAnimation(mActivity,R.anim.continue_anim);
                image.startAnimation(loadAnimation);
                break;
            case R.id.flash:
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f,1.0f);
                alphaAnimation.setDuration(100);
                alphaAnimation.setRepeatCount(10);
                //倒序重复REVERSE  正序重复RESTART
                alphaAnimation.setRepeatMode(Animation.REVERSE);
                image.startAnimation(alphaAnimation);
                break;
            case R.id.move:
                TranslateAnimation translateAnimation = new TranslateAnimation(-50,50,0,0);
                translateAnimation.setDuration(100);
                //无限的
                translateAnimation.setRepeatCount(Animation.INFINITE);
                translateAnimation.setRepeatMode(Animation.REVERSE);
                image.startAnimation(translateAnimation);
                break;
            case R.id.change:
                startActivity(new Intent(mActivity,RadarChartActivity.class));
                overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
                break;
            case R.id.layout:
                startActivity(new Intent(mActivity,ListActivity.class));
                break;
            case R.id.frame:
                image.setImageResource(R.drawable.anim_list);
                AnimationDrawable animationDrawable = (AnimationDrawable) image.getDrawable();
                animationDrawable.start();
                break;
            default:
                break;
        }
    }
}
