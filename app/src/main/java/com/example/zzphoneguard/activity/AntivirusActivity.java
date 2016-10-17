package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.db.AntivirusDao;
import com.example.zzphoneguard.engine.AppManagerEngine;
import com.example.zzphoneguard.mode.AppBean;
import com.example.zzphoneguard.utils.Md5Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 狗蛋儿 on 2016/10/16.
 * 扫描病毒界面
 */
public class AntivirusActivity extends Activity {

    private static final int SCANNING = 0;
    private static final int FINISH = 1;
    private static final int MESSAGE = 2;
    private ImageView iv_scan;
    private TextView tv_scanAppName;
    private ProgressBar pb_scan;
    private LinearLayout ll_results;
    private RotateAnimation ra;
    private AntivirusDao dao;
    private List<AppBean> allApks = new ArrayList<>();
    private int progress = 0;
    private boolean isRun = false;
    private int antivirusNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initAnimation();
        //startScan();
        checkAntivirus();//联网检查病毒库最新版本
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRun = false;//退出关闭线程

    }

    /**
     * 联网检查病毒库最新版本
     */
    private void checkAntivirus() {

        AlertDialog.Builder ab = new Builder(this);
        final AlertDialog alertDialog = ab.setTitle("注意")
                .setMessage("正在云查杀")
                .create();
        alertDialog.show();
        RequestParams params = new RequestParams("http://192.168.1.107:8080/VirusServer/servlet/getversion");
        params.setConnectTimeout(5000);
        x.http().get(params, new CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                alertDialog.dismiss();
                int newVersion = Integer.parseInt(s);
                if (dao.isNewVurus(newVersion)){
                    //是最新病毒库
                    alertDialog.dismiss();
                    startScan();
                }else {
                    //不是最新病毒库
                    updateAntivirus();
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                alertDialog.dismiss();
                Toast.makeText(AntivirusActivity.this, "云查杀失败，请检查网络", Toast.LENGTH_SHORT).show();
                startScan();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 更新病毒库
     */
    private void updateAntivirus() {
        AlertDialog.Builder ab = new Builder(this);
        ab.setMessage("注意")
                .setMessage("检查到最新病毒库，是否更新？")
                .setPositiveButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestParams params = new RequestParams("http://192.168.1.107:8080/VirusServer/servlet/getviruses");
                        params.setConnectTimeout(5000);
                        x.http().get(params, new CommonCallback<String>() {
                            @Override
                            public void onSuccess(String s) {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    String md5 = jsonObject.getString("md5");
                                    String desc = jsonObject.getString("desc");
                                    dao.addVirus(md5,desc);
                                    Toast.makeText(AntivirusActivity.this, "更新病毒库成功", Toast.LENGTH_SHORT).show();
                                    startScan();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable throwable, boolean b) {
                                Toast.makeText(AntivirusActivity.this, "获取最新病毒库失败", Toast.LENGTH_SHORT).show();
                                startScan();
                            }

                            @Override
                            public void onCancelled(CancelledException e) {

                            }

                            @Override
                            public void onFinished() {

                            }
                        });
                    }
                })
                .setNegativeButton("取消", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startScan();
                    }
                })
                .show();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANNING://正在扫描时
                    iv_scan.startAnimation(ra);
                    break;
                case MESSAGE:
                    pb_scan.setMax(allApks.size());
                    pb_scan.setProgress(progress);
                    AntivirusBean bean = (AntivirusBean) msg.obj;
                    TextView tv = new TextView(AntivirusActivity.this);
                    if (bean.isVirus) {
                        tv.setTextColor(Color.RED);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    tv.setText(bean.packName);
                    tv_scanAppName.setText("正在扫描："+bean.packName);

                    //加载到线性布局中
                    ll_results.addView(tv, 0);//每次加到最前面
                    break;
                case FINISH:
                    iv_scan.clearAnimation();
                    tv_scanAppName.setText("扫描完毕，发现病毒软件"+antivirusNum+"个！");
                    break;
                default:
                    break;
            }
        }
    };

    private class AntivirusBean {
        String packName;
        boolean isVirus;
    }

    /**
     * 开始扫描病毒
     */
    private void startScan() {
        new Thread() {
            @Override
            public void run() {
                handler.obtainMessage(SCANNING).sendToTarget();

                isRun = true;

                //病毒个数
                antivirusNum = 0;
                allApks = AppManagerEngine.getAllApks(getApplicationContext());
                AntivirusBean bean = new AntivirusBean();//存放每个app扫描的结果信息
                for (AppBean appBean : allApks) {

                    if (!isRun){
                        //停止线程运行
                        return;
                    }

                    bean.packName = appBean.getPackName();
                    if (dao.isVirus(Md5Utils.getFileMD5(appBean.getAppPath()))) {
                        bean.isVirus = true;
                        antivirusNum++;
                    } else {
                        bean.isVirus = false;
                    }
                    progress++;
                    Message msg = handler.obtainMessage(MESSAGE);
                    msg.obj = bean;
                    handler.sendMessage(msg);
                }
                handler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        ra = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(600);
        ra.setRepeatCount(Animation.INFINITE);//无限次数
        //修改动画旋转插入器，数学函数
        ra.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_antivirus);
        iv_scan = (ImageView) findViewById(R.id.iv_antivirus_scan);
        tv_scanAppName = (TextView) findViewById(R.id.tv_antivirus_title);
        pb_scan = (ProgressBar) findViewById(R.id.pb_antivirus_scanprogress);
        ll_results = (LinearLayout) findViewById(R.id.ll_antivirus_results);
        dao = new AntivirusDao();
    }
}
