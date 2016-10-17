package com.example.zzphoneguard.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.mode.UrlBean;
import com.example.zzphoneguard.utils.MyConstants;
import com.example.zzphoneguard.utils.SaveData;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 *SplashActivity
 *
 */

public class SplashActivity extends Activity {
    private static final int LOADMAIN =0 ;
    private static final int SHOWUPDATEDIALOG = 1;
    private int load=0;
    private Button bt_load;
    private RelativeLayout relativeLayout;
    private TextView tv_versionName;
    private int versionCode;//版本号
    private UrlBean parse;//uri的对象
    private long startTimeMillis;//开始运行时间
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOADMAIN:
                    if (load==0) {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case SHOWUPDATEDIALOG:
                    if (load==0) {
                        showUpdateDialog();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 展示是否更新新版本的对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提醒")
                .setMessage("有最新版本，新版本有如下特性："+parse.getDesc())
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //更新apk
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_splash);
        tv_versionName = (TextView) findViewById(R.id.tv_splash_version_name);
        bt_load = (Button) findViewById(R.id.bt_load);
        bt_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                load=1;
            }
        });
        initAnimation();//加载渐变动画
        initData();//初始化版本信息
        if (SaveData.getBoolean(getApplicationContext(), MyConstants.AOTUUPDATE,false)){
            //如果设置中心设置了自动更新
            checkVersion();//联网检查版本信息
        }
        //拷贝手机归属地数据库
        copyDB("address.db");
        //拷贝病毒数据库
        copyDB("antivirus.db");

    }

    /**
     * 拷贝数据库
     * @param dbName assets目录下的文件名
     */
    private void copyDB(final String dbName) {
        new Thread(){
            public void run() {
                //判断文件是否存在 如果存在就不拷贝
                File file = new File("/data/data/"+getPackageName()+"/files/"+dbName);
                if (file.exists()){
                    return;
                }
                try {
                    AssetManager assets  = getAssets();
                    InputStream is = null;
                    is = assets.open(dbName);
                    FileOutputStream fos = openFileOutput(dbName, Context.MODE_PRIVATE);
                    byte[] buffer = new byte[10240];
                    int len = is.read(buffer);
                    int counts = 1;
                    while (len!=-1){
                        fos.write(buffer,0,len);
                        //每次读到100k的时候，刷新缓冲区的数据到文件中
                        if (counts%10==0){
                            fos.flush();
                        }
                        len = is.read(buffer);
                        counts++;
                    }
                    fos.flush();
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    /**
     * 获取自己的版本信息
     */
    private void initData() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            tv_versionName.setText("开发版 "+versionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 联网检查版本信息
     */
    private void checkVersion() {
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("http://192.168.1.113:8080/guard.json");
                    HttpURLConnection coon = (HttpURLConnection) url.openConnection();
                    coon.setConnectTimeout(5000);
                    coon.setRequestMethod("GET");
                    int code = coon.getResponseCode();
                    if (code==200){
                        InputStream is = coon.getInputStream();
                        BufferedReader reader=new BufferedReader(new InputStreamReader(is));
                        String line = reader.readLine();

                        StringBuilder response = new StringBuilder();
                        while(line!=null){
                            response.append(line);
                            line = reader.readLine();
                        }
                        parse = parseJson(response);

                        reader.close();
                        coon.disconnect();
                        isNewVersion(parse);
                    }

                } catch (Exception e) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                Thread.sleep(3000);
                                if (load == 0) {
                                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }catch (Exception e){

                            }
                        }
                    }).start();
                }
            }
        }.start();
    }

    /**
     * 判断是否有新版本，该方法在子线程中运行
     */
    private void isNewVersion(UrlBean urlBean) {
        SystemClock.sleep(3000);
        int version = urlBean.getVersionCode();
        if (versionCode==version){//如果版本一致
            Message msg = Message.obtain();
            msg.what = LOADMAIN;
            handler.sendMessage(msg);
        }else if(versionCode!=version){//如果检查出最近版本
            Message msg = Message.obtain();
            msg.what = SHOWUPDATEDIALOG;
            handler.sendMessage(msg);
        }
    }

    /**
     * 解析Json数据并封装到bean对象中
     */
    private UrlBean parseJson(StringBuilder response) {

        UrlBean urlBean = new UrlBean();
        try {
            JSONObject jsonObject = new JSONObject(response + "");
            int version = jsonObject.getInt("version");
            String apkPath = jsonObject.getString("url");
            String desc = jsonObject.getString("desc");
            System.out.println("描述信息：" + desc);
//            byte[] bytes = Base64.decode(desc.getBytes(), 1024);
//            desc = new String(bytes, "GBK");
            urlBean.setUrl(apkPath);
            urlBean.setVersionCode(version);
            urlBean.setDesc(desc);
        }catch(Exception e){

        }

        return urlBean;
    }


    /**
     *  动画的显示
     */
    private void initAnimation(){
        //创建动画 0.0f表示透明 1.0f表示完全显示
        AlphaAnimation aa=new AlphaAnimation(0.0f,1.0f);
        //设置动画播放的时间
        aa.setDuration(3000);
        //界面停留在动画结束时的状态
        aa.setFillAfter(true);
        //显示动画，根布局设置动画
        relativeLayout.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!SaveData.getBoolean(getApplicationContext(),MyConstants.AOTUUPDATE,false)){
                    //如果设置中心没有设置自动更新，则直接进入主界面
                    Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
