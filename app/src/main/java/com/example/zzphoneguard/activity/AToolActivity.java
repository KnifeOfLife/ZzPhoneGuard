package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.zzphoneguard.R;

/**
 * Created by 狗蛋儿 on 2016/8/6.
 * 高级工具：电话归属地查询，短信备份，程序锁的设置
 */
public class AToolActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
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
}
