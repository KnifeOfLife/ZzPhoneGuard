package com.example.zzphoneguard.engine;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.example.zzphoneguard.mode.TaskBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 狗蛋儿 on 2016/10/5.
 * 获取所有运行中的进程的信息
 */
public class TaskManagerEngine {
    /**
     * @return 获取运行中的进程
     */
    public static List<TaskBean> getAllRunningTaskInfos(Context context) {
        List<TaskBean> datas = new ArrayList<>();
        // 获取运行中的进程
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        // 获取包管理器
        PackageManager pm = context.getPackageManager();
        // 获取运行中的进程

        List<RunningAppProcessInfo> runningAppProcesses = am
                .getRunningAppProcesses();


        for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            TaskBean bean = new TaskBean();
            // apk的 包名
            String processName = runningAppProcessInfo.processName;// 包名
            // 设置apk的包名
            bean.setPackName(processName);

            // 获取apk的图标和名字

            //有些进程是无名进程
            PackageInfo packageInfo = null;
            try {
                packageInfo = pm.getPackageInfo(processName, 0);
            } catch (NameNotFoundException e) {
                continue;//继续循环 不添加没有名字的进程

            }
            //获取apk的图标
            bean.setIcon(packageInfo.applicationInfo.loadIcon(pm));
            //获取apk的名字
            bean.setName(packageInfo.applicationInfo.loadLabel(pm) + "");

            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0){
                //系统apk
                bean.setSystem(true);
            } else {
                bean.setSystem(false);//用户apk
            }

            //获取占用的内存大小

            android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            //获取占用内存 以byte单位
            long totalPrivateDirty = processMemoryInfo[0].getTotalPrivateDirty() * 1024;// 获取运行中占用的内存
            bean.setMemSize(totalPrivateDirty);

            datas.add(bean);// 添加一个进程信息
        }


        return datas;
    }

    /**
     * @param context
     * @return 获取可用内存的大小
     */
    public static long getAvailMemSize(Context context) {
        long size = 0;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        size = memoryInfo.availMem;
        return size;
    }

    /**
     * @param context
     * @return 获取总内存的大小
     */
    public static long getTotalMemSize(Context context) {
        long size = 0;
        /*
        //适用于api 16
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        //获取总内存大小
        size = memoryInfo.totalMem;*/
        File file = new File("/proc/meminfo");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String totalMemInfo = reader.readLine();
            int startIndex = totalMemInfo.indexOf(':');
            int endIndex = totalMemInfo.indexOf('k');
            totalMemInfo = totalMemInfo.substring(startIndex + 1, endIndex).trim();
            size = Long.parseLong(totalMemInfo);
            size *= 1024;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

}
