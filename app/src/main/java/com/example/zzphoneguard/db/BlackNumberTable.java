package com.example.zzphoneguard.db;

/**
 * Created by 狗蛋儿 on 2016/5/8.
 * 黑名单表的结构
 */
public interface BlackNumberTable {
    String PHONE="phone";//黑名单号码列
    String MODE= "mode";//黑名单拦截模式
    String BLACKTABLE="blacktb";//黑名单表名
    int SMS = 1;
    int TEL = 2;
    int ALL = 3;

}
