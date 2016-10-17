package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.engine.AppManagerEngine;
import com.example.zzphoneguard.mode.AppBean;
import com.stericson.RootTools.RootTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 狗蛋儿 on 2016/9/24.
 * 软件管家的界面
 */
public class AppManagerActivity extends Activity {

    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private TextView tv_sdAvail;
    private TextView tv_romAvail;
    private ListView lv_datas;
    private ProgressBar pb_loading;

    //可用内存
    private long sdAvail;
    private long romAvail;

    private List<AppBean> userApks = new ArrayList<>();
    private List<AppBean> sysApks = new ArrayList<>();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOADING:
                    pb_loading.setVisibility(View.VISIBLE);
                    lv_datas.setVisibility(View.GONE);
                    tv_listview_lable.setVisibility(View.GONE);
                    break;
                case FINISH:
                    pb_loading.setVisibility(View.GONE);
                    lv_datas.setVisibility(View.VISIBLE);                    tv_listview_lable.setVisibility(View.GONE);
                    tv_listview_lable.setVisibility(View.VISIBLE);
                    tv_sdAvail.setText("SD卡可用空间："+ Formatter.formatFileSize(getApplicationContext(),sdAvail));
                    tv_romAvail.setText("ROM可用空间："+Formatter.formatFileSize(getApplicationContext(),romAvail));
                    tv_listview_lable.setText("个人软件（"+userApks.size()+"）");

                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    private MyAdapter adapter;
    private TextView tv_listview_lable;
    private PopupWindow popupWindow;
    private ScaleAnimation sa;
    private View popupView;
    private AppBean clickBean;
    private PackageManager pm;
    private BroadcastReceiver receiver;

    private class ViewHolder{
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_location;
        TextView tv_size;
    }

    private class MyAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return userApks.size()+1+sysApks.size()+1;
        }

        /**
         * 通过位置获取数据
         * @param position
         * @return
         */
        @Override
        public AppBean getItem(int position) {
            AppBean bean = null;
            if (position<=userApks.size()){
                bean = userApks.get(position-1);
            }else {
                bean = sysApks.get(position-2-userApks.size());
            }
            return bean;
        }


        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position==0){
                TextView tv_userTable = new TextView(getApplicationContext());
                tv_userTable.setText("个人软件（"+userApks.size()+"）");
                tv_userTable.setTextColor(Color.WHITE);
                tv_userTable.setBackgroundColor(Color.GRAY);//背景为灰色
                return tv_userTable;
            }else if (position == userApks.size()+1){
                TextView tv_userTable = new TextView(getApplicationContext());
                tv_userTable.setText("系统软件（"+sysApks.size()+"）");
                tv_userTable.setTextColor(Color.WHITE);
                tv_userTable.setBackgroundColor(Color.GRAY);//背景为灰色
                return tv_userTable;
            }else {
                //界面的缓存
                ViewHolder viewHolder = new ViewHolder();
                if (convertView != null &&convertView instanceof RelativeLayout){
                    viewHolder = (ViewHolder) convertView.getTag();

                }else {
                    convertView = View.inflate(getApplicationContext(),R.layout.item_appmanager_listview_item,null);
                    viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_appmanager_listview_item_icon);
                    viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_appmanager_listview_item_title);
                    viewHolder.tv_location = (TextView) convertView.findViewById(R.id.tv_appmanager_listview_item_location);
                    viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_appmanager_listview_item_size);
                    //绑定Tag
                    convertView.setTag(viewHolder);
                }

                AppBean bean = getItem(position);
                viewHolder.iv_icon.setImageDrawable(bean.getIcon());
                if (bean.isSd()){
                    viewHolder.tv_location.setText("SD存储");
                }else {
                    viewHolder.tv_location.setText("ROM存储");
                }
                viewHolder.tv_title.setText(bean.getAppName());
                viewHolder.tv_size.setText(Formatter.formatFileSize(getApplicationContext(),bean.getSize()));
                return convertView;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
        initPopupWindow();
        initRemoveAppReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /**
     * 注册删除app的广播接收者
     */
    private void initRemoveAppReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initData();
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        //配置数据模式
        filter.addDataScheme("package");
        registerReceiver(receiver,filter);
    }

    private void closePopupWindow(){
        if (popupWindow!=null&&popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }

    private void showPopupWindow(View parent,int x,int y){
        closePopupWindow();
        popupWindow.showAtLocation(parent, Gravity.LEFT|Gravity.TOP,x,y);
        popupView.startAnimation(sa);
    }

    /**
     * 初始化弹出窗体
     */
    private void initPopupWindow() {
        popupView = View.inflate(getApplicationContext(), R.layout.popup_appmanager,null);
        LinearLayout ll_remove = (LinearLayout) popupView.findViewById(R.id.ll_popup_appmanager_remove);
        LinearLayout ll_start = (LinearLayout) popupView.findViewById(R.id.ll_popup_appmanager_start);
        LinearLayout ll_share = (LinearLayout) popupView.findViewById(R.id.ll_popup_appmanager_share);
        LinearLayout ll_setting = (LinearLayout) popupView.findViewById(R.id.ll_popup_appmanager_setting);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.ll_popup_appmanager_remove://卸载
                        removeApp();
                        break;
                    case R.id.ll_popup_appmanager_start://启动
                        startApp();
                        break;
                    case R.id.ll_popup_appmanager_share://分享
                        shareApp();
                        break;
                    case R.id.ll_popup_appmanager_setting://设置
                        settingApp();
                        break;
                    default:
                        break;
                }
                closePopupWindow();
            }
        };

        ll_remove.setOnClickListener(listener);
        ll_start.setOnClickListener(listener);
        ll_share.setOnClickListener(listener);
        ll_setting.setOnClickListener(listener);

        popupWindow = new PopupWindow(popupView,-2,-2);
        //有动画效果必须设置背景
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//透明的背景
        //弹出窗体的位移动画
        sa = new ScaleAnimation(0f,1f,0.5f,1f,
                Animation.RELATIVE_TO_SELF,0f,
                Animation.RELATIVE_TO_SELF,0.5f);
        sa.setDuration(500);
    }

    private void settingApp() {
        Intent intent = new Intent(
                "android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + clickBean.getPackName()));
        startActivity(intent);
    }

    private void shareApp() {
        /*
        <action android:name="android.intent.action.SEND" />
               <category android:name="android.intent.category.DEFAULT" />
               <data android:mimeType="text/plain" />
         */
        Intent intent = new Intent("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"分享应用:"+clickBean.getAppName());
        startActivity(intent);


    }

    private void startApp() {
        String packName = clickBean.getPackName();
        pm.getLaunchIntentForPackage(packName);
        //通过包名获取意图
        Intent launchIntentForPackage = pm.getLaunchIntentForPackage(packName);
        if (launchIntentForPackage == null){
            Toast.makeText(AppManagerActivity.this, "该app没有启动界面", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(launchIntentForPackage);

    }

    private void removeApp() {
        /*
        action android:name="android.intent.action.VIEW" />
                <action android:name="android.intent.action.DELETE" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="package" />
         */
        if (!clickBean.isSystem()) {
            Intent intent = new Intent("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + clickBean.getPackName()));
            startActivity(intent);
        }else {
            try {
                //判断是否Root刷机
                if(!RootTools.isRootAvailable()){
                    Toast.makeText(AppManagerActivity.this, "没有Root权限", Toast.LENGTH_SHORT).show();
                    return;
                }
                //是否root权限授权给 当前的app
                if (!RootTools.isAccessGiven()){
                    Toast.makeText(AppManagerActivity.this, "没有Root权限", Toast.LENGTH_SHORT).show();
                    return;
                }

                RootTools.sendShell("mount -o remount rw /system",8000);//设置命令的超时时间为8秒
                RootTools.sendShell("rm -r "+clickBean.getAppPath(),8000);
                RootTools.sendShell("mount -o remount r /system",8000);//设置命令的超时时间为8秒
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initEvent() {
        lv_datas.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取当前的点击位置的值。如果点击标签不做处理
                if (position==userApks.size()+1){
                    return;
                }
                //如果点击条目，获取当前条目的信息,lv_datas.getItemAtPosition()本质上是调用适配器的getItem（）方法
                clickBean = (AppBean) lv_datas.getItemAtPosition(position);
                int[] location = new int[2];
                view.getLocationInWindow(location);
                showPopupWindow(view,location[0]+150,location[1]);
            }
        });
        //listview滑动事件的处理
        lv_datas.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                closePopupWindow();
                //判断显示位置
                if (firstVisibleItem >=userApks.size()+1){
                    tv_listview_lable.setText("系统软件（"+sysApks.size()+"）");
                }else {
                    tv_listview_lable.setText("个人软件（"+userApks.size()+"）");
                }
            }
        });
    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                handler.obtainMessage(LOADING).sendToTarget();
                List<AppBean> datas = AppManagerEngine.getAllApks(getApplicationContext());
                Log.d("app",datas.size()+"");
                sysApks.clear();
                userApks.clear();
                for (AppBean appBean:datas){
                    if (appBean.isSystem()){
                        sysApks.add(appBean);
                    }else {
                        userApks.add(appBean);
                    }
                }
                sdAvail = AppManagerEngine.getSDAvail();
                romAvail = AppManagerEngine.getRomAvail();
                handler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    private void initView() {
        setContentView(R.layout.activity_appmanager);
        tv_sdAvail = (TextView) findViewById(R.id.tv_appmanager_sdsize);
        tv_romAvail = (TextView) findViewById(R.id.tv_appmanager_romsize);
        lv_datas = (ListView) findViewById(R.id.lv_appmanager_appdatas);
        pb_loading = (ProgressBar) findViewById(R.id.pb_appmanager_loaddatas);
        tv_listview_lable = (TextView) findViewById(R.id.tv_appmanager_lable);
        adapter = new MyAdapter();
        lv_datas.setAdapter(adapter);
        pm = getPackageManager();
    }
}
