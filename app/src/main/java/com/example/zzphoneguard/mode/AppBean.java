package com.example.zzphoneguard.mode;

import android.graphics.drawable.Drawable;

/**
 * Created by 狗蛋儿 on 2016/10/1.
 * app信息
 */
public class AppBean {
    private Drawable icon;//app图标
    private String appName;//app名字
    private long size;//占用的大小
    private boolean isSd;//是否存在Sd卡
    private boolean isSystem;//是否是系统app
    private String packName;//包名
    private String appPath;//安装路径
    private int uid;//应用id

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public boolean isSd() {
        return isSd;
    }

    public void setSd(boolean sd) {
        isSd = sd;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
