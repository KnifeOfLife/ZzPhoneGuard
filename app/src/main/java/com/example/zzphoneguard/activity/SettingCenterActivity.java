package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.service.TelSmsBlackService;
import com.example.zzphoneguard.utils.MyConstants;
import com.example.zzphoneguard.utils.SaveData;
import com.example.zzphoneguard.utils.ServiceUtils;
import com.example.zzphoneguard.view.SettingCenterItemView;

/**
 * Created by 狗蛋儿 on 2016/5/4.
 */
public class SettingCenterActivity extends Activity {
    private SettingCenterItemView sciv_aotuupdate;//自动更新设置
    private SettingCenterItemView sciv_telsmsblack;//黑名单拦截设置
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
        sciv_aotuupdate.setChecked(SaveData.getBoolean(getApplicationContext(),MyConstants.AOTUUPDATE,false));
        sciv_telsmsblack.setChecked(ServiceUtils.isRunningService(getApplicationContext(),"com.example.zzphoneguard.service.TelSmsBlackService"));
    }

    /**
     * 初始化组件事件
     */
    private void initEvent() {
        sciv_aotuupdate.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sciv_aotuupdate.setChecked(!sciv_aotuupdate.isChecked());
                SaveData.putBoolean(getApplicationContext(), MyConstants.AOTUUPDATE,sciv_aotuupdate.isChecked());
            }
        });
        sciv_telsmsblack.setItemClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ServiceUtils.isRunningService(getApplicationContext(),"com.example.zzphoneguard.service.TelSmsBlackService")){
                    Intent intent = new Intent(SettingCenterActivity.this, TelSmsBlackService.class);
                    stopService(intent);
                    sciv_telsmsblack.setChecked(false);
                }else{
                    Intent intent =new Intent(SettingCenterActivity.this,TelSmsBlackService.class);
                    startService(intent);
                    sciv_telsmsblack.setChecked(true);
                }
            }
        });
    }

    /**
     * 初始化布局
     */
    private void initView() {
        setContentView(R.layout.activity_settingcenter);
        sciv_aotuupdate = (SettingCenterItemView) findViewById(R.id.sciv_settingconter_aotuupdate);
        sciv_telsmsblack = (SettingCenterItemView) findViewById(R.id.sciv_settingcenter_telsmsblack);
    }

}
