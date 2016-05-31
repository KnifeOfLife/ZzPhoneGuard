package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.utils.MyConstants;
import com.example.zzphoneguard.utils.SaveData;
import com.example.zzphoneguard.view.SettingConterItemView;

/**
 * Created by 狗蛋儿 on 2016/5/4.
 */
public class SettingCenterActivity extends Activity {
    private SettingConterItemView sctv_aotuupdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView();
        initEvent();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        sctv_aotuupdate.setChecked(SaveData.getBoolean(getApplicationContext(),MyConstants.AOTUUPDATE,false));
    }

    /**
     * 初始化组件事件
     */
    private void initEvent() {
        sctv_aotuupdate.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sctv_aotuupdate.setChecked(!sctv_aotuupdate.isChecked());
                SaveData.putBoolean(getApplicationContext(), MyConstants.AOTUUPDATE,sctv_aotuupdate.isChecked());
            }
        });
    }

    /**
     * 初始化布局
     */
    private void initView() {
        setContentView(R.layout.activity_settingcenter);
        sctv_aotuupdate = (SettingConterItemView) findViewById(R.id.svtv_setting_conter_aotuupdate);
    }

}
