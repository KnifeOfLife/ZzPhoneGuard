package com.example.zzphoneguard.activity;

import com.example.zzphoneguard.engine.ReadContactsEngine;
import com.example.zzphoneguard.mode.ContactBean;

import java.util.List;

/**
 * Created by 狗蛋儿 on 2016/5/31.
 */
public class PhoneLogActivity extends BaseContactsTelSmsActivity {
    @Override
    public List<ContactBean> getDatas() {
        return ReadContactsEngine.readPhoneLog(getApplicationContext());
    }
}
