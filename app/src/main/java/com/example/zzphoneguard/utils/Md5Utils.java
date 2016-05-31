package com.example.zzphoneguard.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5加密类
 */
public class Md5Utils {
    public static String md5(String str){
        StringBuilder builder=new StringBuilder();
        try {
            MessageDigest digest=MessageDigest.getInstance("MD5");
            byte[] bytes = str.getBytes();//把字符串转化成字节串
            byte[] dg = digest.digest(bytes);//把加密后的字符串转化成字节串
            for (Byte b:dg) {

                int  d=b&0xff;//去掉前三位 的6个0
                String s = Integer.toHexString(d);//把整数转化成16进制字符串
                if (s.length()==1){
                    s="0"+s;
                }
                builder.append(s);
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return builder+"";
    }
}
