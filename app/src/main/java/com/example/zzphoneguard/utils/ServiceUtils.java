package com.example.zzphoneguard.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by 狗蛋儿 on 2016/4/21.
 */
public class ServiceUtils {
    /**
     * 判断服务是否处于运行状态
     * @return
     */
    public static boolean isRunningService(Context context,String serviceName){
        boolean isChecked = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(50);
        for (ActivityManager.RunningServiceInfo runningService:runningServices) {
            if (runningService.service.getClassName().equals(serviceName)){
                isChecked = true;
                break;
            }
        }
        return isChecked;
    }
}
