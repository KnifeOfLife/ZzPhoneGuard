package com.example.zzphoneguard.engine;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by 狗蛋儿 on 2016/10/16.
 * 流量统计的业务类
 */
public class ConnectivityEngine {
    /**
     *
     * @return
     *        接受的流量信息
     */
    public static String getReceive(int uid, Context context){
        String res = null;
        //读取流量信息文件 /proc/uid_stat/uid/tcp_rcv
        String path = "/proc/uid_stat/"+uid+"/tcp_rcv";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            String line = reader.readLine();
            long size = Long.parseLong(line);
            res = android.text.format.Formatter.formatFileSize(context,size);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
    /**
     *
     * @return
     *        发送的流量信息
     */
    public static String getSend(int uid,Context context){
        String res = null;
        //读取流量信息文件 /proc/uid_stat/uid/tcp_snd
        String path = "/proc/uid_stat/"+uid+"/tcp_snd";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            String line = reader.readLine();
            long size = Long.parseLong(line);
            res = android.text.format.Formatter.formatFileSize(context,size);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
