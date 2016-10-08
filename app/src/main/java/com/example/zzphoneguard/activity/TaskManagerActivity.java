package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.engine.TaskManagerEngine;
import com.example.zzphoneguard.mode.TaskBean;
import com.example.zzphoneguard.utils.MyConstants;
import com.example.zzphoneguard.utils.SaveData;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by 狗蛋儿 on 2016/10/5.
 */
public class TaskManagerActivity extends Activity {

    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private TextView tv_taskNumber;
    private TextView tv_memInfo;
    private TextView tv_list_tag;
    private ListView lv_taskDatas;
    private ProgressBar pb_loading;
    private List<TaskBean> sysTasks = new CopyOnWriteArrayList<>();
    private List<TaskBean> userTasks = new CopyOnWriteArrayList<>();
    private long availMem = 0;
    private long totalMem = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_loading.setVisibility(View.VISIBLE);
                    lv_taskDatas.setVisibility(View.GONE);
                    tv_list_tag.setVisibility(View.GONE);
                    break;
                case FINISH:
                    pb_loading.setVisibility(View.GONE);
                    lv_taskDatas.setVisibility(View.VISIBLE);
                    tv_list_tag.setVisibility(View.VISIBLE);
                    setTitleMessage();
                    adapter.notifyDataSetChanged();

                    break;
                default:
                    break;
            }
        }
    };

    private void setTitleMessage() {
        if (SaveData.getBoolean(getApplicationContext(), MyConstants.SHOWSYSTEMTASKS, true)) {
            tv_taskNumber.setText("运行中的进程：" + (sysTasks.size() + userTasks.size()));
        }else {
            tv_taskNumber.setText("运行中的进程：" + userTasks.size());
        }
        String availMemFormat = Formatter.formatFileSize(getApplicationContext(), TaskManagerActivity.this.availMem);
        String totalMemFormat = Formatter.formatFileSize(getApplicationContext(), TaskManagerActivity.this.totalMem);
        tv_memInfo.setText("可用/总内存：" + availMemFormat + "/" + totalMemFormat);
    }

    private MyAdapter adapter;
    private ActivityManager am;

    private class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_memSize;
        CheckBox cb_checked;
    }

    private class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            setTitleMessage();
            if (!SaveData.getBoolean(getApplicationContext(), MyConstants.SHOWSYSTEMTASKS, true)) {
                return userTasks.size() + 1;
            }
            return sysTasks.size() + 1 + userTasks.size() + 1;
        }


        @Override
        public TaskBean getItem(int position) {
            TaskBean bean = null;
            if (position == 0 || position == userTasks.size() + 1) {
                return bean;
            } else if (position <= userTasks.size()) {
                bean = userTasks.get(position - 1);
            } else {
                bean = sysTasks.get(position - userTasks.size() - 2);
            }
            return bean;
        }


        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView tv_userTable = new TextView(getApplicationContext());
                tv_userTable.setText("个人软件（" + userTasks.size() + "）");
                tv_userTable.setTextColor(Color.WHITE);
                tv_userTable.setBackgroundColor(Color.GRAY);//背景为灰色
                return tv_userTable;
            } else if (position == userTasks.size() + 1) {
                TextView tv_userTable = new TextView(getApplicationContext());
                tv_userTable.setText("系统软件（" + sysTasks.size() + "）");
                tv_userTable.setTextColor(Color.WHITE);
                tv_userTable.setBackgroundColor(Color.GRAY);//背景为灰色
                return tv_userTable;
            } else {
                //界面的缓存
                ViewHolder viewHolder = new ViewHolder();
                if (convertView != null && convertView instanceof RelativeLayout) {
                    viewHolder = (ViewHolder) convertView.getTag();

                } else {
                    convertView = View.inflate(getApplicationContext(), R.layout.item_taskmanager_listview_item, null);
                    viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_taskmanager_listview_item_icon);
                    viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_taskmanager_listview_item_title);
                    viewHolder.tv_memSize = (TextView) convertView.findViewById(R.id.tv_taskmanager_listview_item_memsize);
                    viewHolder.cb_checked = (CheckBox) convertView.findViewById(R.id.tv_taskmanager_listview_item_checked);
                    //绑定Tag
                    convertView.setTag(viewHolder);
                }
                final TaskBean bean = getItem(position);

                final ViewHolder viewHolder1 = viewHolder;
                convertView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bean.getPackName().equals(getPackageName())) {
                            bean.setChecked(false);
                        }
                        viewHolder1.cb_checked.setChecked(!viewHolder1.cb_checked.isChecked());
                    }
                });

                viewHolder.iv_icon.setImageDrawable(bean.getIcon());
                viewHolder.tv_title.setText(bean.getName());
                viewHolder.tv_memSize.setText(Formatter.formatFileSize(getApplicationContext(), bean.getMemSize()));
                viewHolder.cb_checked.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        bean.setChecked(isChecked);
                    }
                });
                viewHolder.cb_checked.setChecked(bean.isChecked());

                if (bean.getPackName().equals(getPackageName())) {
                    viewHolder.cb_checked.setVisibility(View.GONE);
                }else {
                    viewHolder.cb_checked.setVisibility(View.VISIBLE);
                }

                return convertView;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initDate();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDate();
    }

    private void initEvent() {
        lv_taskDatas.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //判断显示位置
                if (firstVisibleItem >= userTasks.size() + 1) {
                    tv_list_tag.setText("系统软件（" + sysTasks.size() + "）");
                } else {
                    tv_list_tag.setText("个人软件（" + userTasks.size() + "）");
                }
            }
        });

    }

    private void initDate() {
        new Thread() {
            @Override
            public void run() {
                handler.obtainMessage(LOADING).sendToTarget();
                availMem = TaskManagerEngine.getAvailMemSize(getApplicationContext());
                totalMem = TaskManagerEngine.getTotalMemSize(getApplicationContext());
                SystemClock.sleep(500);
                List<TaskBean> allTaskDatas = TaskManagerEngine.getAllRunningTaskInfos(getApplicationContext());
                sysTasks.clear();
                userTasks.clear();
                for (TaskBean bean : allTaskDatas) {
                    if (bean.isSystem()) {
                        sysTasks.add(bean);
                    } else {
                        userTasks.add(bean);
                    }
                }

                System.out.println("allTaskDatas:" + allTaskDatas.size() + "  sysTasks:" + sysTasks.size() + "  userTasks:" + userTasks.size());

                handler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    private void initView() {
        setContentView(R.layout.activity_taskmanager);
        tv_taskNumber = (TextView) findViewById(R.id.tv_taskmanager_tasknumber);
        tv_memInfo = (TextView) findViewById(R.id.tv_taskmanager_meminfo);
        lv_taskDatas = (ListView) findViewById(R.id.lv_taskmanager_appdatas);
        tv_list_tag = (TextView) findViewById(R.id.tv_taskmanager_lable);
        pb_loading = (ProgressBar) findViewById(R.id.pb_taskmanager_loaddatas);
        adapter = new MyAdapter();
        lv_taskDatas.setAdapter(adapter);
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    }

    /**
     * 清除按钮点击事件
     *
     * @param view
     */
    public void clearTask(View view) {

        long clearMem = 0;//清理进程的内存的数量
        int clearNum = 0;//清理的进程的个数
        for (TaskBean bean : userTasks) {
            if (bean.isChecked()) {
                clearNum++;
                clearMem += bean.getMemSize();

                am.killBackgroundProcesses(bean.getPackName());

                userTasks.remove(bean);
            }
        }

        for (TaskBean bean : sysTasks) {
            if (bean.isChecked()) {
                clearNum++;
                clearMem += bean.getMemSize();

                am.killBackgroundProcesses(bean.getPackName());

                sysTasks.remove(bean);
            }
        }

        Toast.makeText(TaskManagerActivity.this,
                "清理了" + clearNum + "个进程，释放了" +
                        Formatter.formatFileSize(getApplicationContext(), clearMem) + "内存",
                Toast.LENGTH_LONG).show();
        availMem += clearMem;
        setTitleMessage();

        adapter.notifyDataSetChanged();
    }

    /**
     * 全选按钮点击事件
     *
     * @param view
     */
    public void selectAll(View view) {
        for (TaskBean bean : userTasks) {
            if (bean.getPackName().equals(getPackageName())) {
                bean.setChecked(false);
                continue;
            }
            bean.setChecked(true);
        }
        for (TaskBean bean : sysTasks) {
            bean.setChecked(true);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 反选按钮点击事件
     *
     * @param view
     */
    public void unSelect(View view) {
        for (TaskBean bean : userTasks) {
            if (bean.getPackName().equals(getPackageName())) {
                bean.setChecked(false);
                continue;
            }
            bean.setChecked(!bean.isChecked());
        }
        for (TaskBean bean : sysTasks) {
            bean.setChecked(!bean.isChecked());
        }
        adapter.notifyDataSetChanged();

    }

    /**
     * 设置按钮点击事件
     *
     * @param view
     */
    public void setting(View view) {
        Intent intent = new Intent(TaskManagerActivity.this, TaskManagerSettingActivity.class);
        startActivity(intent);
    }

}
