package com.example.zzphoneguard.activity;

import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.db.LockedDao;
import com.example.zzphoneguard.db.LockedTable;
import com.example.zzphoneguard.fragment.LockedFragment;
import com.example.zzphoneguard.fragment.UnlockedFragment;

import java.util.List;

/**
 * Created by 狗蛋儿 on 2016/10/8.
 * 程序锁的界面
 */
public class LockedActivity extends FragmentActivity {

    private TextView tv_unlock;
    private TextView tv_lock;
    private FrameLayout fl_contnet;
    private UnlockedFragment unlockedFragment;
    private LockedFragment lockedFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {

        //注册内容观察者
        ContentObserver observer = new ContentObserver(new Handler()) {

            @Override
            public void onChange(boolean selfChange) {
                new Thread(){
                    public void run() {
                        LockedDao dao = new LockedDao(getApplicationContext());
                        //读取dao层读取数据
                        List<String> allLockedDatas = dao.getAllLockedDatas();

                        lockedFragment.setAllLockedPacks(allLockedDatas);
                        unlockedFragment.setAllLockedPacks(allLockedDatas);
                    };

                }.start();
                super.onChange(selfChange);
            }

        };
        getContentResolver().registerContentObserver(LockedTable.uri, true, observer);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if (v.getId() == R.id.tv_lockedactivity_unlocked) {
                    transaction.replace(R.id.fl_lockedactivity_content, unlockedFragment);
                    tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                    tv_lock.setBackgroundResource(R.drawable.tab_right_default);
                }else if (v.getId()==R.id.tv_lockedactivity_locked){
                    transaction.replace(R.id.fl_lockedactivity_content, lockedFragment);
                    tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
                    tv_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                }
                transaction.commit();
            }
        };
        tv_lock.setOnClickListener(listener);
        tv_unlock.setOnClickListener(listener);
    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {

                LockedDao dao = new LockedDao(getApplicationContext());
                List<String> allLockedDatas = dao.getAllLockedDatas();
                lockedFragment.setAllLockedPacks(allLockedDatas);
                unlockedFragment.setAllLockedPacks(allLockedDatas);
            }
        }.start();

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_lockedactivity_content, unlockedFragment);
        transaction.commit();
    }

    private void initView() {
        setContentView(R.layout.activity_lock);
        tv_unlock = (TextView) findViewById(R.id.tv_lockedactivity_unlocked);
        tv_lock = (TextView) findViewById(R.id.tv_lockedactivity_locked);
        fl_contnet = (FrameLayout) findViewById(R.id.fl_lockedactivity_content);
        unlockedFragment = new UnlockedFragment();
        lockedFragment = new LockedFragment();
    }
}
