package com.example.zzphoneguard.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.engine.PhonyLocationEngine;
import com.example.zzphoneguard.utils.MyConstants;
import com.example.zzphoneguard.utils.SaveData;

/**
 * Created by 狗蛋儿 on 2016/9/7.
 * 来电显示归属地查询
 */
public class ComingPhoneService extends Service{

    private TelephonyManager tm;
    private PhoneStateListener listener;
    private LayoutParams params;
    private WindowManager wm;
    private View view;
    private float startX;
    private float startY;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        initToastParams();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state){
                    case TelephonyManager.CALL_STATE_IDLE://空闲，挂断，初始会先执行
                        closeLocationToast();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK://通话状态
                        closeLocationToast();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING://响铃状态
                        showLocationToast(incomingNumber);
                        break;
                    default:
                        break;
                }
            }
        };
        tm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * 初始化土司的参数
     */
    private void initToastParams() {
        params = new LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        params.gravity = Gravity.LEFT|Gravity.TOP;
        if (!TextUtils.isEmpty(SaveData.getString(getApplicationContext(), MyConstants.TOASTX,"0"))&&
                !TextUtils.isEmpty(SaveData.getString(getApplicationContext(), MyConstants.TOASTY,"0"))){
            params.x = Integer.parseInt(SaveData.getString(getApplicationContext(), MyConstants.TOASTX,"0"));
            params.y = Integer.parseInt(SaveData.getString(getApplicationContext(), MyConstants.TOASTY,"0"));
        }
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                /*| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE*/

    }

    /**
     * 关闭土司
     */
    private void closeLocationToast() {
        if (view !=null) {
            wm.removeView(view);
            view = null;
        }
    }

    /**
     * 显示土司
     * @param incomingNumber 来电号码
     */
    private void showLocationToast(String incomingNumber){
        int backgrounds[] = new int[]{R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,R.drawable.call_locate_green,
                R.drawable.call_locate_orange, R.drawable.call_locate_white};
        view = View.inflate(getApplicationContext(), R.layout.sys_toast,null);
        int index = Integer.parseInt(SaveData.getString(getApplicationContext(), MyConstants.STYLEINDEX,"0"));
        view.setBackgroundResource(backgrounds[index]);
        TextView tv_location = (TextView) view.findViewById(R.id.tv_toast_location);
        tv_location.setText(PhonyLocationEngine.locationQuery(incomingNumber,getApplicationContext()));
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float newX =  event.getRawX();
                        float newY = event.getRawY();
                        float dX = newX-startX;
                        float dY = newY-startY;
                        params.x += dX;
                        params.y += dY;
                        startX = newX;
                        startY = newY;
                        wm.updateViewLayout(view,params);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (params.x<0){
                            params.x = 0;
                        }else if (params.x+view.getWidth()>wm.getDefaultDisplay().getWidth()){
                            params.x = wm.getDefaultDisplay().getWidth()-view.getWidth();
                        }
                        if (params.y<0){
                            params.y = 0;
                        }else if (params.y+view.getHeight()>wm.getDefaultDisplay().getHeight()){
                            params.y = wm.getDefaultDisplay().getHeight()-view.getHeight();
                        }
                        SaveData.putString(getApplicationContext(), MyConstants.TOASTX,params.x+"");
                        SaveData.putString(getApplicationContext(), MyConstants.TOASTY,params.y+"");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        wm.addView(view, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
    }
}
