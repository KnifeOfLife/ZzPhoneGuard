package com.example.zzphoneguard.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.example.zzphoneguard.activity.WatchDogEnterPassActivity;
import com.example.zzphoneguard.db.LockedDao;
import com.example.zzphoneguard.db.LockedTable;

import java.util.List;

/**
 * Created by 狗蛋儿 on 2016/10/11.
 * 看门狗服务
 */
public class WatchDogService extends Service {

    private boolean isWatch;
    private LockedDao lockedDao;
    private WatchDogReceiver receiver;
    private String shuRen;
    private static UsageStatsManager usageStatsManager;
    private List<String> allLockedDatas;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册内容观察者
        ContentObserver observer = new ContentObserver(new Handler()) {

            @Override
            public void onChange(boolean selfChange) {
                new Thread(){
                    public void run() {
                        LockedDao dao = new LockedDao(getApplicationContext());
                        //读取dao层读取数据
                        allLockedDatas = dao.getAllLockedDatas();

                    };

                }.start();
                super.onChange(selfChange);
            }

        };
        getContentResolver().registerContentObserver(LockedTable.uri, true, observer);

        lockedDao = new LockedDao(getApplicationContext());
        allLockedDatas = lockedDao.getAllLockedDatas();
        receiver = new WatchDogReceiver();
        IntentFilter filter = new IntentFilter("com.example.zzphoneguard.service.watchdog");
        filter.addAction(Intent.ACTION_SCREEN_OFF);//锁屏
        filter.addAction(Intent.ACTION_SCREEN_ON);//亮屏
        //注册广播获取熟人，并在锁屏状态下停止看门狗服务
        registerReceiver(receiver,filter);
        watchDog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isWatch = false;
        unregisterReceiver(receiver);
    }

    private void watchDog(){
        new Thread(){
            @Override
            public void run() {
                isWatch = true;
                while (isWatch){
                   /* List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);//获取最新任务栈
                    RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    String packageName = runningTaskInfo.topActivity.getPackageName();//任务栈顶部的Activity
*/
                    String packageName = getLauncherTopApp(getApplicationContext());
                    if (allLockedDatas.contains(packageName)){

                        if (packageName.equals(shuRen)){
                            //什么也不做
                        }else {
                            Intent intent = new Intent(getApplicationContext(), WatchDogEnterPassActivity.class);
                            //把包名传递给输入密码的界面
                            intent.putExtra("packname", packageName);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }else {
                        //什么也不做
                    }
                    SystemClock.sleep(50);//每隔50毫秒监控任务栈
                    System.out.println(packageName);

                }
            }
        }.start();
    }

    /**
     * 看门狗的广播接收者
     */
    private class WatchDogReceiver extends BroadcastReceiver{


        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.example.zzphoneguard.service.watchdog")) {
                shuRen = intent.getStringExtra("packname");
            }else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                //清空熟人，停止看门狗
                shuRen = "";
                isWatch = false;
            }else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                //启动看门狗
                watchDog();
            }
        }
    }

    /**
     *
     * @param context
     * @return  获取android栈顶程序
     */
    public static String getLauncherTopApp(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks.get(0).topActivity.getPackageName();
            }
        } else {
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 10000;
            if (usageStatsManager == null) {
                usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            }
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = usageStatsManager.queryEvents(beginTime, endTime);
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.getPackageName();
                }
            }
            if (!android.text.TextUtils.isEmpty(result)) {
                return result;
            }
        }
        return "";
    }




}
