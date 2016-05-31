package com.example.zzphoneguard.mode;

/**
 * 获取url的信息的封装
 */
public class UrlBean {
    private int  versionCode;//版本号
    private String url;//apk下的路径
    private String desc;//新版本的信息

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setVersionCode(int  versionCode) {
        this.versionCode = versionCode;
    }

    public String getDesc() {
        return desc;
    }

    public String getUrl() {
        return url;
    }

    public int  getVersionCode() {
        return versionCode;
    }
}
