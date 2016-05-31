package com.example.zzphoneguard.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.example.zzphoneguard.utils.MyConstants;
import com.example.zzphoneguard.utils.SaveData;

public class LocationService extends Service {
    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private LocationManager lm;
    private LocationListener listener;

    @Override
    public void onCreate() {
        super.onCreate();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float accuracy = location.getAccuracy();//精确度
                double altitude = location.getAltitude();//海拔高度
                double latitude = location.getLatitude();//纬度
                double longitude = location.getLongitude();//经度
                float speed = location.getSpeed();//移动速度
                StringBuilder builder = new StringBuilder();
                builder.append("精确度" + accuracy + "\n");
                builder.append("海拔高度" + altitude + "\n");
                builder.append("纬度" + latitude + "\n");
                builder.append("经度" + longitude + "\n");
                builder.append("移动速度" + speed + "\n");
                //发送短信
                String safenumber = SaveData.getString(LocationService.this, MyConstants.SAFENUMBER, "");
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(safenumber, "", builder + "", null, null);
                //关闭gps
                stopSelf();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);//是否产生费用
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置精确度
        String bestProvider = lm.getBestProvider(criteria, true);

        //注册监听回调
        lm.requestLocationUpdates(bestProvider, 0, 0, listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        lm.removeUpdates(listener);
        lm=null;
    }
}
