package com.example.zzphoneguard.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.activity.RocketSmokeActivity;

/**
 * Created by 狗蛋儿 on 2016/9/22.
 * 小火箭发射
 */
public class RocketService extends Service {

    private LayoutParams params;
    private WindowManager wm;
    private View view;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            wm.updateViewLayout(view,params);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        initToastParams();
        showRocket();
        closeRocket();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeRocket();
    }

    /**
     * 关闭小火箭
     */
    private void closeRocket() {
        if (view!=null){
            wm.removeView(view);
            view = null;
        }
    }

    /**
     * 显示小火箭
     */
    private void showRocket() {
        //小火箭的布局

        view = View.inflate(getApplicationContext(), R.layout.rocket, null);
        ImageView iv_rocket = (ImageView) view.findViewById(R.id.iv_rocket);
        //获取小火箭的动画背景
        AnimationDrawable ad = (AnimationDrawable) iv_rocket.getBackground();
        //开始小火箭动画
        ad.start();

        //给小火箭加触摸事件，按住拖动小火箭到屏幕正下方，松开发射火箭
        view.setOnTouchListener(new OnTouchListener() {

            private float startX;
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println(event.getX() + ":" + event.getRawX());
                // 拖动土司
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:// 按下
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:// 按下移动，拖动
                        //新的 x  y坐标
                        float moveX = event.getRawX();//移动后的x坐标
                        float moveY = event.getRawY();//移动后的x坐标

                        //dx x方向的位置变化值 dy y方向的位置变化值
                        float dx = moveX - startX;
                        float dy = moveY - startY;
                        //改变土司的坐标
                        params.x += dx;
                        params.y += dy;
                        //重新获取新的x y坐标
                        startX = moveX;
                        startY = moveY;

                        //更新土司的位置
                        wm.updateViewLayout(view, params);
                        break;
                    case MotionEvent.ACTION_UP:// 松开
                        //判断位置 发射
                        //x轴方向 离两边框超过100,y轴方向大于200 就可以发射火箭
                        if (params.x > 100 && params.x + view.getWidth()< wm.getDefaultDisplay().getWidth() - 100 &&
                                params.y > 200){
                            //发射火箭
                            //1,火箭往上跑
                            //火箭在中心线上发射
                            params.x = (wm.getDefaultDisplay().getWidth() - view.getWidth()) / 2;
                            new Thread(){
                                public void run() {
                                    for (int j = 0; j < view.getHeight(); ) {
                                        SystemClock.sleep(50);//休眠50毫秒
                                        params.y -= j;
                                        j += 5;
                                        handler.obtainMessage().sendToTarget();
                                    }

                                    //关闭小火箭
                                    stopSelf();//关闭服务
                                };
                            }.start();

                            //2,烟的效果
                            Intent intent = new Intent(RocketService.this,RocketSmokeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//在任务栈
                            startActivity(intent);//启动烟的Activity
                        }
                        //冒烟的Activity
                    default:
                        break;
                }
                return false;
            }
        });

        wm.addView(view, params);//把小火箭加到窗体管理器
    }

    /**
     * 配置参数
     */
    private void initToastParams() {
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        //对齐方式左上角
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		/* | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE */
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        //初始化土司的位置
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 土司天生不相应时间,改变类型
        params.setTitle("Toast");

    }

}
