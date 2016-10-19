package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.engine.AppManagerEngine;
import com.example.zzphoneguard.mode.AppBean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 狗蛋儿 on 2016/10/18.
 * 缓存管理界面
 */
public class CacheActivity extends Activity {

    private static final int SCANNING = 0;
    private static final int FINISH = 1;
    private static final int CLEARALL = 2;
    private ProgressBar pb_loaing;
    private ListView lv_message;
    private TextView tv_nodata;
    private MyAdapter adapter = new MyAdapter();

    private List<CacheInfo> cacheDatas = new ArrayList<>();
    private PackageManager pm;
    private List<AppBean> allApks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        scanAllApp();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANNING:
                    pb_loaing.setVisibility(View.VISIBLE);
                    lv_message.setVisibility(View.GONE);
                    tv_nodata.setVisibility(View.GONE);
                    break;
                case CLEARALL:
                    pb_loaing.setVisibility(View.GONE);
                    lv_message.setVisibility(View.GONE);
                    tv_nodata.setVisibility(View.VISIBLE);
                    //一键清理后发送过来
                    tv_nodata.setText(msg.obj + "");
                    break;
                case FINISH:
                    pb_loaing.setVisibility(View.GONE);

                    if (cacheDatas.size() == 0) {
                        //没有缓存信息
                        lv_message.setVisibility(View.GONE);
                        tv_nodata.setVisibility(View.VISIBLE);
                    } else {
                        //有缓存信息
                        lv_message.setVisibility(View.VISIBLE);
                        tv_nodata.setVisibility(View.GONE);

                        adapter.notifyDataSetChanged();
                    }


                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 遍历所有App，查看是否有缓存信息
     */
    private void scanAllApp() {
        new Thread() {
            @Override
            public void run() {
                handler.obtainMessage(SCANNING).sendToTarget();
                cacheDatas.clear();
                //遍历所有安装的apk
                allApks = AppManagerEngine.getAllApks(getApplicationContext());
                for (AppBean appBean : allApks) {
                    getAppCacheSize(appBean.getPackName());
//                    SystemClock.sleep(50);
                }


            }
        }.start();
    }

    private void initView() {
        setContentView(R.layout.activity_cache);
        pb_loaing = (ProgressBar) findViewById(R.id.pb_cache_scaning_cache);
        lv_message = (ListView) findViewById(R.id.lv_cache_message);
        tv_nodata = (TextView) findViewById(R.id.tv_cache_nodata);
        //获取缓存的远程aidl对象
        pm = getPackageManager();
        lv_message.setAdapter(adapter);

    }

    private class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_cacheSize;
    }

    private class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return cacheDatas.size();
        }


        @Override
        public Object getItem(int position) {
            return null;
        }


        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            if (convertView != null && convertView instanceof RelativeLayout) {
                viewHolder = (ViewHolder) convertView.getTag();

            } else {
                convertView = View.inflate(getApplicationContext(), R.layout.item_cache_linearlayout_item, null);
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_cache_linearlayout_item_icon);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_cache_linearlayout_item_title);
                viewHolder.tv_cacheSize = (TextView) convertView.findViewById(R.id.tv_cache_linearlayout_item_size);
                convertView.setTag(viewHolder);
            }

            CacheInfo cacheInfo = cacheDatas.get(position);

            viewHolder.iv_icon.setImageDrawable(cacheInfo.icon);
            viewHolder.tv_title.setText(cacheInfo.name);
            viewHolder.tv_cacheSize.setText(cacheInfo.cacheSize);


            return convertView;
        }
    }

    /**
     * 一键清理点击事件
     *
     * @param v
     */
    public void clearAll(View v) {
        Class clazz = pm.getClass();
        try {
            Method method = clazz.getDeclaredMethod("freeStorageAndNotify",
                    long.class, IPackageDataObserver.class);
            method.invoke(pm, Long.MAX_VALUE, new ClearCache());// 结果回调在IPackageStatsObserver的对象中
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取缓存信息大小
     *
     * @param packageName
     */
    private void getAppCacheSize(String packageName) {
        Class clazz = pm.getClass();//获取PackageManager的类类型
        try {
            Method method = clazz.getMethod("getPackageSizeInfo",
                    String.class, IPackageStatsObserver.class);
            // 把包名传递给回调对象
            //getcacheInfo.packName = packageName;
            method.invoke(pm, packageName, new GetCacheInfo(packageName));// 结果回调在IPackageStatsObserver的对象中
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int counts = 0;
    /**
     * 获取缓存大小
     */
    private class GetCacheInfo extends IPackageStatsObserver.Stub {
        String packName;

        public GetCacheInfo(String packName) {
            this.packName = packName;
        }
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            synchronized (new Object()) {
                counts++;
                //判断是否有缓存
                if (pStats.cacheSize > 0) {
                    //有缓存，把app和对应的缓存信息记录（放到容器里）
                    //图标，名字，缓存大小
                    CacheInfo cacheInfo = new CacheInfo();
                    try {
                        PackageInfo packageInfo = pm.getPackageInfo(packName, 0);
                        cacheInfo.icon = packageInfo.applicationInfo.loadIcon(pm);
                        cacheInfo.name = packageInfo.applicationInfo.loadLabel(pm) + "";
                        cacheInfo.cacheSize = Formatter.formatFileSize(getApplicationContext(), pStats.cacheSize);
                        cacheInfo.cacheSizeLong = pStats.cacheSize;
                    } catch (NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    System.out.println("APP名字：" + cacheInfo.name + "缓存大小：" + Formatter.formatFileSize(getApplicationContext(), pStats.cacheSize));
                    cacheDatas.add(cacheInfo);
                    handler.obtainMessage(FINISH).sendToTarget();
                }
                if (counts==allApks.size()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "缓存查找完毕", Toast.LENGTH_SHORT).show();
                        }
                    });
                    handler.obtainMessage(FINISH).sendToTarget();
                }
            }
        }
    }


    /**
     * 清理缓存
     */
    private class ClearCache extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            Message msg = handler.obtainMessage(CLEARALL);
            long totalSize = 0;
            for (CacheInfo cacheInfo : cacheDatas) {
                totalSize += cacheInfo.cacheSizeLong;
            }
            cacheDatas.clear();
            msg.obj = "垃圾文件清理成功，本次为您节省了" + Formatter.formatFileSize(getApplicationContext(), totalSize) + "空间";
            handler.sendMessage(msg);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    scanAllApp();
                    Toast.makeText(getApplicationContext(), "清理完毕", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 缓存信息的封装类
     */
    private class CacheInfo {
        Drawable icon;//图标
        String name;//名字
        long cacheSizeLong;
        String cacheSize;//缓存大小
    }
}
