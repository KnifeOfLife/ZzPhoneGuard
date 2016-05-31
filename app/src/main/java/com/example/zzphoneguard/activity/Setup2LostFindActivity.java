package com.example.zzphoneguard.activity;


import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.utils.MyConstants;
import com.example.zzphoneguard.utils.SaveData;

/**
 * Created by 狗蛋儿 on 2016/4/17.
 */
public class Setup2LostFindActivity extends BaseSetupActivity {
    private Button bt_bind;
    private ImageView iv_setup;
    @Override
    public void initView() {
        setContentView(R.layout.setup2_lostfind_activity);
        bt_bind = (Button) findViewById(R.id.bt_setup2);
        iv_setup = (ImageView) findViewById(R.id.iv_setup2);
        if (TextUtils.isEmpty(SaveData.getString(getApplicationContext(), MyConstants.SIM,""))){
            iv_setup.setImageResource(R.drawable.unlock);

        }else{
            iv_setup.setImageResource(R.drawable.lock);

        }
    }

    @Override
    public void initEvent() {
        bt_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(SaveData.getString(getApplicationContext(), MyConstants.SIM,""))){
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = telephonyManager.getSimSerialNumber();
                    SaveData.putString(getApplicationContext(),MyConstants.SIM,simSerialNumber);
                    iv_setup.setImageResource(R.drawable.lock);
                }else{
                    SaveData.putString(getApplicationContext(),MyConstants.SIM,"");
                    iv_setup.setImageResource(R.drawable.unlock);
                }

            }
        });
        super.initEvent();
    }

    @Override
    public void next(View v) {
        if (TextUtils.isEmpty(SaveData.getString(getApplicationContext(), MyConstants.SIM,""))){
            Toast.makeText(getApplicationContext(), "请绑定SIM卡", Toast.LENGTH_SHORT).show();
            return;
        }
        super.next(v);
    }

    @Override
    public void nextActivity() {

        startActivity(Setup3LostFindActivity.class);
    }

    @Override
    public void prevActivity() {

        startActivity(Setup1LostFindActivity.class);
    }
}
