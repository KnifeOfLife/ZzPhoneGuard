package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.engine.AppManagerEngine;
import com.example.zzphoneguard.engine.ConnectivityEngine;
import com.example.zzphoneguard.mode.AppBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 狗蛋儿 on 2016/10/16.
 * 流量统计的页面
 */
public class ConnectivityActivity extends Activity {
    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private ListView lv_datas;

    private MyAdapter adapter = new MyAdapter();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    lv_datas.setVisibility(View.GONE);
                    pb_loading.setVisibility(View.VISIBLE);
                    break;
                case FINISH:
                    lv_datas.setVisibility(View.VISIBLE);
                    pb_loading.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };
    private List<AppBean> allApks = new ArrayList<>();
    private ProgressBar pb_loading;
    private ConnectivityManager cm;

    private class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
        ImageView iv_detail;
    }

    private class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return allApks.size();
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
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_liuliang_listview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_liuliang_listview_item_icon);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_liuliang_listview_item_title);
                viewHolder.iv_detail = (ImageView) convertView.findViewById(R.id.iv_liuliang_listview_item_detail);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final AppBean bean = allApks.get(position);
            viewHolder.iv_icon.setImageDrawable(bean.getIcon());
            viewHolder.tv_title.setText(bean.getAppName());
            viewHolder.iv_detail.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String rev = ConnectivityEngine.getReceive(bean.getUid(), getApplicationContext());
                    String snd = ConnectivityEngine.getSend(bean.getUid(), getApplicationContext());
                    showConnectivityMess(cm.getActiveNetworkInfo().getTypeName() + "\n接收的流量：" + rev + "\n发送的流量：" + snd);
                }
            });
            return convertView;
        }
    }

    /**
     * 显示流量信息的对话框
     */
    private void showConnectivityMess(String message) {
        AlertDialog.Builder ab = new Builder(this);
        ab.setTitle("流量信息")
                .setMessage(message)
                .setPositiveButton("确定", null);
        ab.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {

    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                handler.obtainMessage(LOADING).sendToTarget();
                allApks = AppManagerEngine.getAllApks(getApplicationContext());
                for (int i=0;i<allApks.size();i++) {
                    AppBean bean = allApks.get(i);
                    if (ConnectivityEngine.getReceive(bean.getUid(),getApplicationContext())==null){
                        allApks.remove(i);
                        i--;
                    }
                }
                handler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    private void initView() {
        setContentView(R.layout.activity_liuliang);
        lv_datas = (ListView) findViewById(R.id.lv_liuliang_datas);
        pb_loading = (ProgressBar) findViewById(R.id.pb_liuliang_loading);
        lv_datas.setAdapter(adapter);
        cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    }
}
