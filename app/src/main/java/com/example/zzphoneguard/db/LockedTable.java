package com.example.zzphoneguard.db;

import android.net.Uri;

/**
 * Created by 狗蛋儿 on 2016/10/8.
 * 程序所数据库表的结构
 */
public interface LockedTable {
    String TABLENAME = "locked";//程序锁的表名
    String PACKNAME = "packname";//程序锁的列名
    Uri uri = Uri.parse("content://goudaner/locked");
}
