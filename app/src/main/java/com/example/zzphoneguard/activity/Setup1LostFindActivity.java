package com.example.zzphoneguard.activity;

import com.example.zzphoneguard.R;

/**
 * Created by 狗蛋儿 on 2016/4/17.
 * 手机防盗向导界面
 */
public class Setup1LostFindActivity extends BaseSetupActivity {

    @Override
    public void initView() {
        setContentView(R.layout.setup1_lostfind_activity);
    }

    @Override
    public void nextActivity() {
        startActivity(Setup2LostFindActivity.class);
    }

    @Override
    public void prevActivity() {

    }
}
