package com.example.zzphoneguard.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.engine.ReadContactsEngine;
import com.example.zzphoneguard.mode.ContactBean;
import com.example.zzphoneguard.utils.MyConstants;

/**
 * 手机联系人的Activity
 */
public class ContactsActivity extends BaseContactsTelSmsActivity {


    @Override
    public List<ContactBean> getDatas() {
        return ReadContactsEngine.readContacts(getApplicationContext());
    }
}
