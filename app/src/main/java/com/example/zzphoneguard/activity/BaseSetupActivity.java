package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.zzphoneguard.R;

/**
 * Created by 狗蛋儿 on 2016/4/18.
 */
public abstract class BaseSetupActivity extends Activity {
    private GestureDetector gd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initGesture();
        initEvent();

    }

    /**
     * 初始化组件的数据
     */
    public void initData() {
    }

    public void initEvent() {
    }

    /**
     * 要想手势识别器生效 必须绑定onTouch时间
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gd.onTouchEvent(event))
        return true;
    else
        return false;
    }

    /**
     * 初始化手势识别器
     */
    private void initGesture() {
        gd = new GestureDetector(new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            //e1 手指点击的坐标
            //e2 手指松开的坐标
            //velocityX x轴移动的速度
            //velocityY y轴移动的速度
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                if (Math.abs(e1.getY() - e2.getY()) > 100)
                    return false;
                if ((e1.getX() - e2.getX()) > 100
                        && Math.abs(velocityX) > 100){
                    next(null);
                }else if ((e2.getX() - e1.getX()) > 100
                        && Math.abs(velocityX) > 100){
                    prev(null);
                }
                return true;
            }
        });
    }

    public abstract void initView();

    public void next(View v){
        nextActivity();
        nextAnimation();
    }



    public void prev(View v){
        prevActivity();
        prevAnimation();
    }

    /**
     * 上一步的页面进来的动画
     */
    private void prevAnimation() {
        overridePendingTransition(R.anim.prev_in,R.anim.prev_out);
    }


    public void startActivity(Class type){
        Intent intent = new Intent(this,type);
        startActivity(intent);
        finish();
    }
    /**
     * 下一步的页面进来的动画
     */
    private void nextAnimation() {
        overridePendingTransition(R.anim.next_in,R.anim.next_out);
    }

    public abstract void nextActivity();
    public abstract void prevActivity();
}
