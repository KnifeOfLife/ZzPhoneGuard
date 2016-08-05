package com.example.zzphoneguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.example.zzphoneguard.db.BlackNumberDao;
import com.example.zzphoneguard.db.BlackNumberTable;

import java.lang.reflect.Method;

public class TelSmsBlackService extends Service {

    private SmsReceiver receiver;
    private BlackNumberDao dao;
    private PhoneStateListener listener;
    private TelephonyManager tm;

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
        //注册电话的监听 采用TelephonyManager类
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, final String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state){
                    case TelephonyManager.CALL_STATE_IDLE://挂断的状态

                        break;
                    case TelephonyManager.CALL_STATE_RINGING://响铃的状态
                        int mode = dao.getMode(incomingNumber);
                        if ((mode&BlackNumberTable.TEL)!=0){
                            System.out.println("挂断电话");
                            //挂断电话之前注册内容观察者
                            getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true,
                                    new ContentObserver(new Handler()) {
                                        @Override
                                        public void onChange(boolean selfChange) {
                                            super.onChange(selfChange);
                                            deleteCallLog(incomingNumber);
                                            getContentResolver().unregisterContentObserver(this);
                                        }
                                    });
                            endCall();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK://通话的状态

                        break;
                    default:
                        break;


                }
            }
        };
        tm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);

    }

    /**
     * 删除电话日志
     * @param incomingNumber
     */
    private void deleteCallLog(String incomingNumber) {
        Uri uri = Uri.parse("content://call_log/calls");
        getContentResolver().delete(uri,"number=?",new String[]{incomingNumber});

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消短信的监听广播
        unregisterReceiver(receiver);
        //取消电话的监听
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);

    }

    /**
     * 挂断电话
     */
    protected void endCall(){
        try {
            Class cla = Class.forName("android.os.ServiceManager");
            Method method = cla.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null,Context.TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
