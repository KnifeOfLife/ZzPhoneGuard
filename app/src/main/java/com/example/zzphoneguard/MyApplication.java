package com.example.zzphoneguard;

import android.app.Application;

import org.xutils.x;

/**
 * Created by 狗蛋儿 on 2016/10/17.
 */
public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }
}
