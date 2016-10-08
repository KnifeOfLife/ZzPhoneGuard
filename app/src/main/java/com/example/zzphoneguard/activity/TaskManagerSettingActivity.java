package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.service.ClearTaskInLockscreenService;
import com.example.zzphoneguard.utils.MyConstants;
import com.example.zzphoneguard.utils.SaveData;
import com.example.zzphoneguard.utils.ServiceUtils;

/**
 * Created by 狗蛋儿 on 2016/10/6.
 */
public class TaskManagerSettingActivity extends Activity {

    private CheckBox cb_clear;//锁屏清理进程
    private CheckBox cb_showSystemApk;//显示系统进程

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    private void initData() {
        if (ServiceUtils.isRunningService(getApplicationContext(),"com.example.zzphoneguard.service.ClearTaskInLockscreenService")){
            cb_clear.setChecked(true);
        }else {
            cb_clear.setChecked(false);
        }
        cb_showSystemApk.setChecked(SaveData.getBoolean(getApplicationContext(),MyConstants.SHOWSYSTEMTASKS,true));
    }

    private void initEvent() {
        cb_clear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Intent service = new Intent(TaskManagerSettingActivity.this, ClearTaskInLockscreenService.class);
                    startService(service);
                }else {
                    Intent service = new Intent(TaskManagerSettingActivity.this, ClearTaskInLockscreenService.class);
                    stopService(service);
                }
            }
        });
        cb_showSystemApk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveData.putBoolean(getApplicationContext(), MyConstants.SHOWSYSTEMTASKS,isChecked);
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_taskmanager_setting);
        cb_clear = (CheckBox) findViewById(R.id.cb_taskmanager_setting_lockscreen_clear);
        cb_showSystemApk = (CheckBox) findViewById(R.id.cb_taskmanager_setting_lockscreen_showsystemapk);
    }

}
