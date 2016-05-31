package com.example.zzphoneguard.mode;

/**
 * Created by 狗蛋儿 on 2016/5/8.
 * 黑名单的封装类
 */
public class BlackNumberBean {
    private String phone;
    private int mode;


    @Override
    public boolean equals(Object o) {
        if (o instanceof BlackNumberBean){
            BlackNumberBean bean = (BlackNumberBean) o;
            return phone.equals(bean.getPhone());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return phone.hashCode();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getMode() {

        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
