package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.engine.SmsEngine;
import com.example.zzphoneguard.engine.SmsEngine.BaikeProgress;

/**
 * Created by 狗蛋儿 on 2016/8/6.
 * 高级工具：电话归属地查询，短信备份，程序锁的设置
 */
public class AToolActivity extends Activity {

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    private void initView() {
        setContentView(R.layout.activity_atool);
    }

    /**
     * 号码归属地查询点击事件
     * @param v
     */
    public void phoneQuery(View v){
        Intent intent = new Intent(this,PhoneLocationActivity.class);
        startActivity(intent);
    }

    /**
     * 短信的备份点击事件
     * @param v
     */
    public void smsBaike(View v){
        SmsEngine.smsBaikeJson(AToolActivity.this, new BaikeProgress() {
            @Override
            public void show() {
                pd.show();
            }

            @Override
            public void dismiss() {
                pd.dismiss();
            }

            @Override
            public void setMax(int counts) {
                pd.setMax(counts);
            }

            @Override
            public void setProgress(int progress) {
                pd.setProgress(progress);
            }
        });
    }
    /**
     * 短信的还原点击事件
     * @param v
     */
    public void smsRestore(View v){
        SmsEngine.smsRestoreJson(AToolActivity.this, new BaikeProgress() {
            @Override
            public void show() {
                pd.show();
            }

            @Override
            public void dismiss() {
                pd.dismiss();
            }

            @Override
            public void setMax(int counts) {
                pd.setMax(counts);
            }

            @Override
            public void setProgress(int progress) {
                pd.setProgress(progress);
            }
        });
    }
}
