package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.example.zzphoneguard.R;

/**
 * Created by 狗蛋儿 on 2016/9/22.
 * 火箭尾气
 */
public class RocketSmokeActivity  extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocketsmoke);

        ImageView iv_m = (ImageView) findViewById(R.id.iv_rocket_smoke_m);
        //烟柱子
        ImageView iv_t = (ImageView) findViewById(R.id.iv_rocket_smoke_t);


        //动画
        AlphaAnimation aa = new AlphaAnimation(0.0f,1.0f);
        aa.setDuration(1000);
        //比例动画
        ScaleAnimation sa = new ScaleAnimation(0f, 1f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f);
        sa.setDuration(1000);

        iv_m.startAnimation(aa);
        AnimationSet as = new AnimationSet(true);
        as.addAnimation(aa);
        as.addAnimation(sa);

        iv_t.startAnimation(as);

        //2秒后关闭Activity
        new Thread(){
            public void run() {
                //2秒后关闭当前Activity
                SystemClock.sleep(1000);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        finish();
                    }
                });

            };
        }.start();

    }
}
