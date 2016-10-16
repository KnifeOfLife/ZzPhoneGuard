package com.example.zzphoneguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 狗蛋儿 on 2016/10/8.
 * 创建程序锁的数据库，一张表，一个列：packname
 */
public class LockedDB extends SQLiteOpenHelper {


    public LockedDB(Context context) {
        super(context, "locked.db", null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table locked(_id integer primary key autoincrement,packname text)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table locked");
        onCreate(db);
    }
}
