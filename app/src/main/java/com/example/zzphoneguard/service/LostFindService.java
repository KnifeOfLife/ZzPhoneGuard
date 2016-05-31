package com.example.zzphoneguard.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;

import com.example.zzphoneguard.R;


public class LostFindService extends Service {
    private boolean isPlay;//判断音乐是否已经播放

    public LostFindService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private SMSReceiver receiver;
    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new SMSReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receiver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private class SMSReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            Object datas[] = (Object[]) extras.get("pdus");
            for (Object data:datas){
                SmsMessage sm = SmsMessage.createFromPdu((byte[]) data);
                String messageBody = sm.getMessageBody();
                if (messageBody.equals("#*gps*#")){
                    Intent intent2 = new Intent(context,LocationService.class);
                    startService(intent2);
                    abortBroadcast();
                }else if (messageBody.equals("#*lockscreen*#")){
                    DevicePolicyManager dpm= (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                    dpm.lockNow();//锁屏

                    abortBroadcast();
                }else if(messageBody.equals("#*wipedata*#")){
                    DevicePolicyManager dpm= (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                    dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);//清除内存卡中的数据
                    abortBroadcast();
                }else if (messageBody.equals("#*music*#")){
                    abortBroadcast();
                    if (isPlay){
                        return;
                    }
                    MediaPlayer mp=MediaPlayer.create(getApplicationContext(), R.raw.she);
                    mp.setVolume(1,1 );//设置声道
                    mp.start();
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            isPlay=false;
                        }
                    });
                    isPlay=true;
                }
            }
        }
    }
}
