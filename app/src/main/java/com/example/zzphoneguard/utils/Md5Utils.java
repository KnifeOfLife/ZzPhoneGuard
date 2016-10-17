package com.example.zzphoneguard.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5加密类
 */
public class Md5Utils {
    /**
     *
     * @param path  文件的路径
     * @return  返回文件的MD5值
     */
    public static String getFileMD5(String path){
        StringBuilder builder=new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(new File(path));
            //获取MD5加密器
            MessageDigest digest=MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[10240];
            int len = fis.read(buffer);
            while (len!=-1){
                digest.update(buffer,0,len);
                len = fis.read(buffer);
            }
            byte[] dg = digest.digest();
            for (Byte b:dg) {

                int  d=b&0xff;//去掉前三位 的6个0
                String s = Integer.toHexString(d);//把整数转化成16进制字符串
                if (s.length()==1){
                    s="0"+s;
                }
                s = s.toUpperCase();
                builder.append(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder+"";
    }

    /**
     *
     * @param str 传入需要加密的数
     * @return  返回加密后的数值
     */
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
