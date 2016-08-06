package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.engine.PhonyLocationEngine;

/**
 * Created by 狗蛋儿 on 2016/8/6.
 * 手机归属地查询界面
 */
public class PhoneLocationActivity extends Activity {

    private EditText mEt_phone;
    private Button mBt_query;
    private TextView mTv_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    private void initEvent() {
        mBt_query.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mEt_phone.getText().toString();
                if (TextUtils.isEmpty(phone)){
                    //Toast.makeText(PhoneLocationActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(500);
                    return;
                }
                String location = PhonyLocationEngine.locationQuery(phone, getApplicationContext());
                mTv_address.setText(location);
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_phonelocation);
        mEt_phone = (EditText) findViewById(R.id.et_phonelocation_number);
        mBt_query = (Button) findViewById(R.id.bt_phonelocation_query);
        mTv_address = (TextView) findViewById(R.id.tv_phonelocation_address);

    }
}
