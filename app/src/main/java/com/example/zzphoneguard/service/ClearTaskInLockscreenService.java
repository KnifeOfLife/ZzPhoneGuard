package com.example.zzphoneguard.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;

/**
 * 该服务主要完成锁屏注册广播或者取消注册
 */
public class ClearTaskInLockscreenService extends Service {

    private ClearTaskReceiver receiver;
    private ActivityManager am;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ClearTaskReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
            for (RunningAppProcessInfo runningAppProcessInfo:runningAppProcesses){
                am.killBackgroundProcesses(runningAppProcessInfo.processName);
                System.out.println("锁屏清理进程");
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        receiver = new ClearTaskReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
