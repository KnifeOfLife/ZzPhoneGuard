package com.example.zzphoneguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsMessage;

import com.example.zzphoneguard.db.BlackNumberDao;
import com.example.zzphoneguard.db.BlackNumberTable;

public class TelSmsBlackService extends Service {

    private SmsReceiver receiver;
    private BlackNumberDao dao;

    public TelSmsBlackService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 短信监听者
     */
    private class SmsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] datas = (Object[]) intent.getExtras().get("pdus");
            for (Object sms:datas){
                SmsMessage sm = SmsMessage.createFromPdu((byte[]) sms);
                String address = sm.getOriginatingAddress();
                address=address.replace("+86","");
                System.out.println("短信发送放的号码："+address);
                //判断是否存在黑名单中
                int mode = dao.getMode(address);
                System.out.println("黑名单的拦截模式："+mode);

                if ((mode & BlackNumberTable.SMS)!=0){
                    abortBroadcast();
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化黑名单的业务类
        dao = new BlackNumberDao(getApplicationContext());

        //注册短信的监听广播
        receiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receiver,filter);
        //注册电话的监听广播

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消短信的监听广播
        unregisterReceiver(receiver);
        //取消电话的监听广播

    }
}
