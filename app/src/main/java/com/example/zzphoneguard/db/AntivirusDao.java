package com.example.zzphoneguard.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by 狗蛋儿 on 2016/10/16.
 * 病毒的业务类
 */
public class AntivirusDao {
    /**
     *
     * @param version
     *        传递的服务器的病毒版本
     * @return      病毒库的版本是否一致
     */
    public static boolean isNewVurus(int version){
        boolean res = false;
        SQLiteDatabase database = SQLiteDatabase.openDatabase("/data/data/com.example.zzphoneguard/files/antivirus.db",
                null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.rawQuery("select 1 from version where subcnt =?",
                new String[]{version+""});
        if (cursor.moveToNext()){
            res = true;
        }
        cursor.close();
        database.close();
        return res;
    }
    /**
     * 添加一个新的病毒软件
     *
     * @param md5 病毒文件的md5值
     * @param desc 病毒的描述信息
     */
    public static void addVirus(String md5, String desc) {
        SQLiteDatabase database = SQLiteDatabase.openDatabase("/data/data/com.example.zzphoneguard/files/antivirus.db",
                null, SQLiteDatabase.OPEN_READONLY);
        ContentValues values = new ContentValues();
        values.put("md5", md5);
        values.put("type", 6);
        values.put("name", "com.exanple.zzPhoneGuard");
        values.put("desc", desc);
        database.insert("datable", null, values);
        database.close();
    }

    /**
     *
     * @param md5
     *          文件的md5值
     * @return
     *          是否是病毒文件
     */
    public static boolean isVirus(String md5){
        boolean res = false;
        SQLiteDatabase database = SQLiteDatabase.openDatabase("/data/data/com.example.zzphoneguard/files/antivirus.db",
                null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.rawQuery("select 1 from datable where md5 =?",
                new String[]{md5});
        if (cursor.moveToNext()){
            res = true;
        }

        cursor.close();
        database.close();
        return res;
    }
}
