package com.example.zzphoneguard.engine;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 电话归属地的业务封装类
 * Created by 狗蛋儿 on 2016/8/5.
 */
public class PhonyLocationEngine {

    public static String locationQuery(String phoneNumber,Context context){
        String location = phoneNumber;
        Pattern p = Pattern.compile("1{1}[3578]{1}[0-9]{9}");
        Matcher m = p.matcher(phoneNumber);
        boolean b = m.matches();
        if (b){
            //手机号
            //如果是手机号
            location = mobileQuery(phoneNumber,context);
        }else if (phoneNumber.length()>=11){
            //如果是固定电话
            location = phoneQuery(phoneNumber,context);
        }else {
            location = phoneNumber;
        }
        return location;
    }
    /*
     *固定号码查询
     */
    public static String phoneQuery(String phoneNumber, Context context){
        String res = phoneNumber;
        SQLiteDatabase database = SQLiteDatabase.openDatabase("/data/data/com.example.zzphoneguard/files/address.db", null, SQLiteDatabase.OPEN_READONLY);
        String quHao ="";
        if (phoneNumber.charAt(1)=='1'||phoneNumber.charAt(1)=='2'){
            quHao = phoneNumber.substring(1,3);
        }else {
            quHao = phoneNumber.substring(1,4);
        }
        Cursor cursor = database.rawQuery("select location from data2 where area = ?", new String[]{quHao});
        if (cursor.moveToNext()){
            res = cursor.getString(0);
        }
        return res;
    }

    /*
     *手机号码查询
     */
    public static String mobileQuery(String phoneNumber, Context context){
        String res = phoneNumber;
        SQLiteDatabase database = SQLiteDatabase.openDatabase("/data/data/com.example.zzphoneguard/files/address.db", null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.rawQuery("select location from data2 where id = (select outKey from data1 where id = ?)", new String[]{phoneNumber.substring(0,7)});
        if (cursor.moveToNext()){
            res = cursor.getString(0);
        }
        return res;
    }
}
