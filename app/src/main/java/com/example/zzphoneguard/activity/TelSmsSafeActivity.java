package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.db.BlackNumberDao;
import com.example.zzphoneguard.db.BlackNumberTable;
import com.example.zzphoneguard.mode.BlackNumberBean;
import com.example.zzphoneguard.utils.MyConstants;

import java.util.*;

/**
 * Created by 狗蛋儿 on 2016/5/8.
 * 通讯中心的界面，主要用于添加黑名单拦截电话或短信
 */
public class TelSmsSafeActivity extends Activity {

    private AlertDialog dialog;//添加黑名单对话框
    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private static final int DATANUMBER = 20;
    private Button bt_addtelsmssafe;//初始化添加按钮
    private ListView lv_telsmssafe;//初始化listview
    private TextView tv_nodata;
    private ProgressBar pb_loading;
    private List<BlackNumberBean>  datas = new ArrayList<BlackNumberBean>();//存放黑名单数据的容器
    private BlackNumberDao dao;//获取黑名单数据库
    private Myadapter adapter;
    private PopupWindow pw;//-2表示包裹内容
    private View contentView;
    private ScaleAnimation sa;
    private List<BlackNumberBean> moreDatas;//动态加载更多数据的容器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();//初始化界面
        initData();//初始化数据
        initEvent();//初始化事件
        initPopupwindow();//弹出窗体，功能：让用户可以从联系人，电话记录，短信记录中添加黑名单
    }

    /**
     * 弹出窗体
     */
    private void initPopupwindow() {
        contentView = View.inflate(getApplicationContext(), R.layout.popup_blacknumber,null);
        TextView tv_shoudong = (TextView) contentView.findViewById(R.id.tv_popup_black_shoudong);
        TextView tv_contacts = (TextView) contentView.findViewById(R.id.tv_popup_black_contacts);
        TextView tv_phonelog = (TextView) contentView.findViewById(R.id.tv_popup_black_phonelog);
        TextView tv_smslog = (TextView) contentView.findViewById(R.id.tv_popup_black_smslog);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_popup_black_shoudong://手动添加联系人
                        showInputBlackNumberDialog("");
                        break;
                    case R.id.tv_popup_black_contacts://从联系人添加
                        Intent intent = new Intent(TelSmsSafeActivity.this,ContactsActivity.class);
                        startActivityForResult(intent,1);
                        break;
                    case R.id.tv_popup_black_phonelog://从电话记录中添加

                        break;
                    case R.id.tv_popup_black_smslog://从短信记录中添加

                        break;
                    default:
                        break;
                }
                closePopupWindow();
            }
        };
        tv_shoudong.setOnClickListener(listener);
        tv_contacts.setOnClickListener(listener);
        tv_phonelog.setOnClickListener(listener);
        tv_smslog.setOnClickListener(listener);
        pw = new PopupWindow(contentView,-2,-2);
        //显示动画要有背景
        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sa = new ScaleAnimation(1,1,0,1, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0f);
        sa.setDuration(300);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data!=null){
            String phone = data.getStringExtra(MyConstants.SAFENUMBER);
            showInputBlackNumberDialog(phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 关闭弹窗
     */
    private void closePopupWindow(){
        if (pw!=null&&pw.isShowing()){
            pw.dismiss();
        }
    }

    /**
     * 显示弹窗
     */
    private void showPopupWindow(){
        if (pw!=null&&pw.isShowing()){
            pw.dismiss();
        }else {
            int[] location = new int[2];
            bt_addtelsmssafe.getLocationInWindow(location);
            contentView.startAnimation(sa);
            pw.showAtLocation(bt_addtelsmssafe, Gravity.RIGHT | Gravity.TOP,
                    location[0]-(getWindowManager().getDefaultDisplay().getWidth()-bt_addtelsmssafe.getWidth()),
                    location[1] - 50 + bt_addtelsmssafe.getHeight() + 70);
        }
    }

    private void initEvent() {
        lv_telsmssafe.setOnScrollListener(new AbsListView.OnScrollListener() {
            /*
            状态改变调用此方法
            SCROLL_STATE_FLING 惯性滑动
            SCROLL_STATE_IDLE 滑动停止
            SCROLL_STATE_TOUCH_SCROLL 按住滑动
            三种状态每次改变都会出发此方法
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //监控禁止的状态SCROLL_STATE_IDLE
                //当出现SCROLL_STATE_IDLE的时候，判断是否显示最后一条数据，如果是 则加载更多数据
                if (scrollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    //最后显示的数据的位置
                    int lastVisiblePosition = lv_telsmssafe.getLastVisiblePosition();
                    if (lastVisiblePosition==datas.size()-1){
                        initData();
                    }
                }
            }
            /*
            按住滑动的事件
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        bt_addtelsmssafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showInputBlackNumberDialog();
                showPopupWindow();
            }
        });
    }
    /**
     * 手动添加黑名单的对话框
     */
    private void showInputBlackNumberDialog(String phone) {
        AlertDialog.Builder ab = new AlertDialog.Builder(TelSmsSafeActivity.this);
        View view = View.inflate(getApplicationContext(), R.layout.addblacknumberdialog_view,null);
        final EditText ed_blackNumber = (EditText) view.findViewById(R.id.ed_telsmssafe_blackunumber);
        final CheckBox cb_smsMode = (CheckBox) view.findViewById(R.id.cb_telsmssafe_smsmode);
        final CheckBox cb_telMode = (CheckBox) view.findViewById(R.id.cb_telsmssafe_telmode);
        Button bt_addBlackNumber = (Button) view.findViewById(R.id.bt_telsmssafe_ok);
        Button bt_cancleAddBlackNumber = (Button) view.findViewById(R.id.bt_telsmssafe_cancle);
        ed_blackNumber.setText(phone);
        bt_cancleAddBlackNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bt_addBlackNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = ed_blackNumber.getText().toString().trim();
                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(TelSmsSafeActivity.this, "黑名单不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!cb_smsMode.isChecked()&&!cb_telMode.isChecked()){
                    Toast.makeText(TelSmsSafeActivity.this, "请至少选择一种拦截模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                int mode = 0;
                if (cb_telMode.isChecked()){
                    mode = BlackNumberTable.TEL;
                }
                if (cb_smsMode.isChecked()){
                    mode = BlackNumberTable.SMS;
                }
                if (cb_smsMode.isChecked()&&cb_telMode.isChecked()){
                    mode = BlackNumberTable.ALL;
                }
                BlackNumberBean bean = new BlackNumberBean();
                bean.setPhone(phone);
                bean.setMode(mode);
                dao.add(bean);
                datas.remove(bean);//靠equals()和hashCode（）两个方法判断数据是否一致
                datas.add(0,bean);
                lv_telsmssafe.setSelection(0);
                dialog.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
        ab.setView(view);

        dialog = ab.create();
        dialog.show();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOADING://处于加载状态
                    pb_loading.setVisibility(View.VISIBLE);
                    lv_telsmssafe.setVisibility(View.GONE);
                    tv_nodata.setVisibility(View.GONE);

                    break;
                case FINISH:
                    //判断是否有数据
                    if (moreDatas.size()!=0) {
                        //如果有数据
                        pb_loading.setVisibility(View.GONE);
                        lv_telsmssafe.setVisibility(View.VISIBLE);
                        tv_nodata.setVisibility(View.GONE);

                        adapter.notifyDataSetChanged();
                    }else {
                        //如果没有数据
                        if (datas.size()!=0){
                            Toast.makeText(TelSmsSafeActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        pb_loading.setVisibility(View.GONE);
                        lv_telsmssafe.setVisibility(View.GONE);
                        tv_nodata.setVisibility(View.VISIBLE);
                        break;
                    }
                default:
                    break;
            }
        }
    };
    /**
     * 初始化数据
     */
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                //取数据之前发个message，显示ujiazai数据的进度条
                handler.obtainMessage(LOADING).sendToTarget();
                //取数据
                moreDatas = dao.getMoreData(DATANUMBER,datas.size());
                datas.addAll(moreDatas);
                //发送数据结果
                handler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        setContentView(R.layout.acticity_telsmssafe);
        bt_addtelsmssafe = (Button) findViewById(R.id.bt_addtelsmssafe);
        lv_telsmssafe = (ListView) findViewById(R.id.lv_telsmssafe_number);
        tv_nodata = (TextView) findViewById(R.id.tv_telsmssafe_nodata);
        pb_loading = (ProgressBar) findViewById(R.id.pb_telsmssafe_loading);
        dao = new BlackNumberDao(this);
        adapter = new Myadapter();
        lv_telsmssafe.setAdapter(adapter);
    }


    private class ItemView{
        TextView tv_phone;
        TextView tv_mode;
        ImageView iv_delete;
    }


    private class Myadapter extends BaseAdapter{
        @Override
        public int getCount() {
            return datas.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ItemView itemView = null;
            if (convertView==null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_telsmssafe, null);
                itemView = new ItemView();
                itemView.tv_phone = (TextView) convertView.findViewById(R.id.tv_telsmssafe_listview_phone);
                itemView.tv_mode = (TextView) convertView.findViewById(R.id.tv_telsmssafe_listview_mode);
                itemView.iv_delete = (ImageView) convertView.findViewById(R.id.iv_telsmssafe_listview_delete);
                convertView.setTag(itemView);//设置标记给contentView
            }else{
                itemView = (ItemView) convertView.getTag();
            }
            final BlackNumberBean bean = datas.get(position);
            itemView.tv_phone.setText(bean.getPhone());
            switch (bean.getMode()){
                case BlackNumberTable.SMS:
                    itemView.tv_mode.setText("短信拦截");
                    break;
                case BlackNumberTable.TEL:
                    itemView.tv_mode.setText("电话拦截");
                    break;
                case BlackNumberTable.ALL:
                    itemView.tv_mode.setText("全部拦截");
                    break;
                default:
                    break;
            }
            itemView.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TelSmsSafeActivity.this);
                    builder.setTitle("注意").setMessage("是否删除这个数据").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dao.delete(bean.getPhone());//从数据库中删除数据
                            datas.remove(position);//删除容器中对应数据
                            adapter.notifyDataSetChanged();//更新listView
                        }
                    }).setNegativeButton("取消",null);
                    builder.show();
                }
            });
            return convertView;
        }
    }
}
