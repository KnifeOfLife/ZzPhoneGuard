package com.example.zzphoneguard.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.example.zzphoneguard.service.LostFindService;
import com.example.zzphoneguard.utils.MyConstants;
import com.example.zzphoneguard.utils.SaveData;

/**
 * Created by user on 2016/4/26.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //检测SIM卡是否发生变化
        String oldsim = SaveData.getString(context, MyConstants.SIM, "");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String newsim = tm.getSimSerialNumber();
        if (!oldsim.equals(newsim)){
            String safeNumber = SaveData.getString(context,MyConstants.SAFENUMBER,"");
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(safeNumber,"","我是小偷",null,null);
        }
        //自动启动防盗服务
        if (SaveData.getBoolean(context,MyConstants.LOSTFIND,false)){
            //开机自动启动防盗服务
            Intent service = new Intent(context, LostFindService.class);
            context.startService(service);
        }
    }
}
