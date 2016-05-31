package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.service.LocationService;
import com.example.zzphoneguard.utils.MyConstants;
import com.example.zzphoneguard.utils.SaveData;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by 狗蛋儿 on 2016/4/17.
 * 手机防盗界面
 */
public class LostFindActivity extends Activity {
    private Button bt_setup;
    private ImageView iv_isSafe;
    private TextView tv_safeNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (SaveData.getBoolean(getApplicationContext(), MyConstants.ISSETUP, false)){
            initView();
            initData();
            initEvent();
        }else{
            Intent intent = new Intent(LostFindActivity.this,Setup1LostFindActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 组件的点击事件
     */
    private void initEvent() {
        bt_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LostFindActivity.this,Setup1LostFindActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * 初始化组件的数据
     */
    private void initData() {
        if (SaveData.getBoolean(getApplicationContext(),MyConstants.OPENLOSTFIND,false)){
            iv_isSafe.setImageResource(R.drawable.lock);
        }else{
            iv_isSafe.setImageResource(R.drawable.unlock);
        }
        tv_safeNumber.setText(SaveData.getString(getApplicationContext(),MyConstants.SAFENUMBER,""));
    }

    /**
     * 初始化界面布局
     */
    private void initView() {
        setContentView(R.layout.lostfind_activity);
        iv_isSafe = (ImageView) findViewById(R.id.iv_lostfind_issafe);
        bt_setup = (Button) findViewById(R.id.bt_setup_lostfind);
        tv_safeNumber = (TextView) findViewById(R.id.tv_lostfind_safenumber);
    }
}
