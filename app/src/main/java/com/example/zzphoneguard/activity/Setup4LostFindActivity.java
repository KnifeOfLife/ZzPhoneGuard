package com.example.zzphoneguard.activity;

import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.service.LostFindService;
import com.example.zzphoneguard.utils.MyConstants;
import com.example.zzphoneguard.utils.SaveData;
import com.example.zzphoneguard.utils.ServiceUtils;

/**
 * Created by 狗蛋儿 on 2016/4/17.
 */
public class Setup4LostFindActivity extends BaseSetupActivity {
    private CheckBox cb_isprotect;
    @Override
    public void initView() {
        setContentView(R.layout.setup4_lostfind_activity);
        cb_isprotect = (CheckBox) findViewById(R.id.cd_setup4_protect);
    }

    /**
     * 初始化组件数据
     */
    @Override
    public void initData() {
        if (ServiceUtils.isRunningService(getApplicationContext(),"com.example.zzphoneguard.service.LostFindService")){
            cb_isprotect.setChecked(true);
        }else{
            cb_isprotect.setChecked(false);
        }
        super.initData();
    }

    /**
     * 初始化组件的点击事件
     */
    @Override
    public void initEvent() {
        cb_isprotect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SaveData.putBoolean(getApplicationContext(),MyConstants.LOSTFIND,true);
                    Intent intent = new Intent(Setup4LostFindActivity.this, LostFindService.class);
                    startService(intent);
                    SaveData.putBoolean(getApplicationContext(),MyConstants.OPENLOSTFIND,true);
                }else{
                    SaveData.putBoolean(getApplicationContext(),MyConstants.LOSTFIND,false);

                    Intent intent = new Intent(Setup4LostFindActivity.this, LostFindService.class);
                    stopService(intent);
                    SaveData.putBoolean(getApplicationContext(),MyConstants.OPENLOSTFIND,false);
                }
            }
        });
        super.initEvent();
    }

    @Override
    public void nextActivity() {
        SaveData.putBoolean(getApplicationContext(),MyConstants.ISSETUP,true);
        startActivity(LostFindActivity.class);
    }

    @Override
    public void prevActivity() {
        startActivity(Setup3LostFindActivity.class);

    }
}
