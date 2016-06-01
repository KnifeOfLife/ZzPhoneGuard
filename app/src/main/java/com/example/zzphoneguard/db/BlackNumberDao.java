package com.example.zzphoneguard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.zzphoneguard.db.BlackNumberDbhelper;
import com.example.zzphoneguard.db.BlackNumberTable;
import com.example.zzphoneguard.mode.BlackNumberBean;
import java.util.*;

/**
 * Created by 狗蛋儿 on 2016/5/8.
 * 对黑名单表进行一些操作
 */
public class BlackNumberDao {
    private BlackNumberDbhelper dbhelper;
    public BlackNumberDao(Context context){
        this.dbhelper=new BlackNumberDbhelper(context);
    }

    /**
     *
     * @param phone
     * 电话
     * @return
     * 拦截模式：1：短信 2：电话 3：全部拦截
     */
    public int getMode(String phone){
        SQLiteDatabase database = dbhelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select "+BlackNumberTable.MODE+" from "+BlackNumberTable.BLACKTABLE+" where "+BlackNumberTable.PHONE+"=?",new String[]{phone});
        int mode = 0;
        if (cursor.moveToNext()){
            mode = cursor.getInt(0);
        }else{
            mode = 0;
        }
        return mode;
    }

    /**
     * 添加黑名单号码
     * @param bean
     */
    public void add(BlackNumberBean bean){
        add(bean.getPhone(),bean.getMode());
    }

    /**
     * 添加黑名单号码
     * @param phone
     * @param mode
     */
    public void add(String phone,int mode){
        delete(phone);
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values  =new ContentValues();
        values.put(BlackNumberTable.PHONE,phone);
        values.put(BlackNumberTable.MODE,mode);
        db.insert(BlackNumberTable.BLACKTABLE,null,values);
        db.close();
    }

    /**
     * 获取黑名单号码
     * @return
     */
    public List<BlackNumberBean> getAllData(){
        List<BlackNumberBean> datas = new  ArrayList<BlackNumberBean>();
        SQLiteDatabase database = dbhelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + BlackNumberTable.PHONE + "," + BlackNumberTable.MODE + " from " + BlackNumberTable.BLACKTABLE, null);
        while (cursor.moveToNext()){
            //数据的封装
            BlackNumberBean bean = new BlackNumberBean();
            bean.setPhone(cursor.getString(0));
            bean.setMode(cursor.getInt(1));
            datas.add(bean);
        }
        cursor.close();
        database.close();
        return datas;
    }

    /**
     * 修改黑名单的模式
     * @param phone
     * @param mode
     */
    public void update(String phone,int mode){
        SQLiteDatabase database = this.dbhelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(BlackNumberTable.MODE,mode);
        database.update(BlackNumberTable.BLACKTABLE,values,BlackNumberTable.PHONE+"=?",new String[]{phone});
        database.close();
    }

    /**
     * 删除黑名单中的号码
     * @param phone
     */
    public void delete(String phone){
        SQLiteDatabase database = this.dbhelper.getWritableDatabase();
        database.delete(BlackNumberTable.BLACKTABLE,BlackNumberTable.PHONE+"=?",new String[]{phone});
        database.close();
    }

    /**
     *
     * @return
     * 总数据个数
     */
    public int getTotalRows(){
        SQLiteDatabase database = dbhelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select count(1) from" + BlackNumberTable.BLACKTABLE, null);
        cursor.moveToNext();
        int totalRows = cursor.getInt(0);
        cursor.close();
        database.close();
        return totalRows;
    }

    /**
     *
     * @param dataNumber 分批加载的数据条目数
     *
     * @param startIndex 取数据的起始位置
     * @return 分批加载数据
     */
    public List<BlackNumberBean> getMoreData(int dataNumber,int startIndex){
        List<BlackNumberBean> datas = new ArrayList<BlackNumberBean>();
        SQLiteDatabase database = dbhelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + BlackNumberTable.PHONE + "," + BlackNumberTable.MODE + " from " + BlackNumberTable.BLACKTABLE + " limit ?,?", new String[]{startIndex + "", dataNumber + ""});
        while (cursor.moveToNext()){
            BlackNumberBean bean =new BlackNumberBean();
            bean.setPhone(cursor.getString(0));
            bean.setMode(cursor.getInt(1));
            datas.add(bean);
        }
        cursor.close();
        database.close();
        return datas;
    }
}
