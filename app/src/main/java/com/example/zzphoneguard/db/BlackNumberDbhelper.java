package com.example.zzphoneguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 狗蛋儿 on 2016/5/8.
 * 黑名单数据库
 */
public class BlackNumberDbhelper extends SQLiteOpenHelper{


    public BlackNumberDbhelper(Context context) {
        super(context, "blacknumber.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacktb(_id integer primary key autoincrement,phone text,mode integer)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table blacktb");
        onCreate(db);
    }
}
