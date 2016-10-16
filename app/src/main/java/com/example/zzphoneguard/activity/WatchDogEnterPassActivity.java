package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zzphoneguard.R;

/**
 * Created by 狗蛋儿 on 2016/10/11.
 */
public class WatchDogEnterPassActivity extends Activity {

    private ImageView iv_icon;
    private EditText et_passWord;
    private Button bt_enter;
    private String packname;
    private HomeReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /**
     * 注册home键的广播
     */
    private class HomeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)){
                //如果是home键，回到主界面并关闭自己
                goHome();
            }
        }
    }

    private void initData() {
        //注册home键的广播
        receiver = new HomeReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(receiver,filter);

        //获取包名
        packname = getIntent().getStringExtra("packname");
        PackageManager packageManager = getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packname, 0);
            iv_icon.setImageDrawable(applicationInfo.loadIcon(packageManager));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initEvent() {
        bt_enter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String passWord = et_passWord.getText().toString().trim();
                if (TextUtils.isEmpty(passWord)){
                    Toast.makeText(WatchDogEnterPassActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passWord.equals("1234")){
                    Intent intent = new Intent();
                    intent.setAction("com.example.zzphoneguard.service.watchdog");
                    intent.putExtra("packname",packname);
                    sendBroadcast(intent);
                    finish();
                }else {
                    Toast.makeText(WatchDogEnterPassActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_watchdogenterpass);
        iv_icon = (ImageView) findViewById(R.id.iv_watchdog_enterpass_icon);
        et_passWord = (EditText) findViewById(R.id.et_watchdog_enterpass_password);
        bt_enter = (Button) findViewById(R.id.bt_watchdog_enterpass_enter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            goHome();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goHome() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
        finish();
    }
}
