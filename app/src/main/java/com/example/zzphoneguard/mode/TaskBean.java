package com.example.zzphoneguard.mode;

import android.graphics.drawable.Drawable;

/**
 * Created by 狗蛋儿 on 2016/10/5.
 * 进程的数据封装类
 */
public class TaskBean {
    private Drawable icon;//apk图标
    private String name;//apk名字
    private String packName;//apk的包名
    private long memSize;//apk占用的内存大小
    private boolean isSystem;
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
