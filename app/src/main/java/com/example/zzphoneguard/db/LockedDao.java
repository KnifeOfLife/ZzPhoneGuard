package com.example.zzphoneguard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 狗蛋儿 on 2016/10/8.
 * 程序锁的数据存储层
 */
public class LockedDao {
    private LockedDB lockedDB;
    private Context context;

    public LockedDao(Context context){
        lockedDB = new LockedDB(context);
        this.context = context;
    }

    /**
     * 判断是否加锁
     * @param packName
     */
    public boolean isLocked(String packName){
        boolean res = false;
        SQLiteDatabase database = lockedDB.getReadableDatabase();
        Cursor cursor = database.rawQuery("select 1 from " + LockedTable.TABLENAME + " where " + LockedTable.PACKNAME + " =?",
                new String[]{packName});
        if (cursor.moveToNext()){
            res = true;
        }else {
            res = false;
        }
        cursor.close();
        database.close();
        return res;
    }

    /**
     *
     * @param packName
     * 要加锁的apk的包名
     */
    public void add(String packName){
        SQLiteDatabase database = lockedDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LockedTable.PACKNAME,packName);
        database.insert(LockedTable.TABLENAME, null,values);
        database.close();
        // 发送内容观察者的通知
        context.getContentResolver().notifyChange(LockedTable.uri, null);
    }

    /**
     *
     * @param packName
     * 要解锁的包名
     */
    public void remove(String packName){
        SQLiteDatabase database = lockedDB.getWritableDatabase();
        database.delete(LockedTable.TABLENAME, LockedTable.PACKNAME+"=?",new String[]{packName});
        database.close();
        // 发送内容观察者的通知
        context.getContentResolver().notifyChange(LockedTable.uri, null);

    }

    /**
     *
     * @return
     *    获取所有加锁的apk的包名
     */
    public List<String> getAllLockedDatas(){
        List<String> lockedName = new ArrayList<>();
        SQLiteDatabase database = lockedDB.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + LockedTable.PACKNAME + " from " + LockedTable.TABLENAME, null);
        while (cursor.moveToNext()){
            lockedName.add(cursor.getString(0));
        }
        cursor.close();
        database.close();
        return lockedName;
    }

}
