package com.example.zzphoneguard.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.db.LockedDao;
import com.example.zzphoneguard.engine.AppManagerEngine;
import com.example.zzphoneguard.mode.AppBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 狗蛋儿 on 2016/10/8.
 */
public class BaseLockOrUnlockFragment extends Fragment {

    protected static final int LOADING = 1;
    protected static final int FINISH = 2;
    protected TextView tv_lab;
    protected ListView lv_datas;
    protected TextView tv_listView_tag;
    protected ProgressBar pb_loading;
    protected MyAdapter adapter = new MyAdapter();

    protected List<AppBean> unlockedSystemDatas = new ArrayList<>();
    protected List<AppBean> unlockedUserDatas = new ArrayList<>();
    protected LockedDao dao;

    protected List<String> allLockedPacks;//所有加锁的app的包名

    public List<String> getAllLockedPacks() {
        return allLockedPacks;
    }

    public void setAllLockedPacks(List<String> allLockedPacks) {
        this.allLockedPacks = allLockedPacks;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = new LockedDao(getActivity());
    }

    private void initEvent() {
        lv_datas.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem <= unlockedUserDatas.size()) {
                    tv_listView_tag.setText("个人软件（" + unlockedUserDatas.size() + "）");
                } else {
                    tv_listView_tag.setText("系统软件（" + unlockedSystemDatas.size() + "）");
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_unlock, null);
        tv_lab = (TextView) view.findViewById(R.id.tv_fragment_unlocked_lab);
        lv_datas = (ListView) view.findViewById(R.id.lv_fragment_unlocked_datas);
        tv_listView_tag = (TextView) view.findViewById(R.id.tv_fragment_unlocked_listview_tag);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_fragment_loading);
        lv_datas.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        initEvent();
    }


    protected void initData() {
        new Thread() {
            @Override
            public void run() {
                synchronized (new Object()) {
                    handler.obtainMessage(LOADING).sendToTarget();
                    List<AppBean> allApks = AppManagerEngine.getAllApks(getActivity());
                    unlockedSystemDatas.clear();
                    unlockedUserDatas.clear();
                    for (AppBean bean : allApks) {
                        if (isMyData(bean.getPackName())) {
                            if (bean.isSystem()) {
                                unlockedSystemDatas.add(bean);
                            } else {
                                unlockedUserDatas.add(bean);
                            }
                        }
                    }
                    handler.obtainMessage(FINISH).sendToTarget();
                }

            }
        }.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_loading.setVisibility(View.VISIBLE);
                    tv_listView_tag.setVisibility(View.GONE);
                    lv_datas.setVisibility(View.GONE);

                    break;
                case FINISH:
                    pb_loading.setVisibility(View.GONE);
                    tv_listView_tag.setVisibility(View.VISIBLE);
                    lv_datas.setVisibility(View.VISIBLE);

                    setTitleLabText(tv_lab);

                    tv_listView_tag.setText("个人软件（" + unlockedUserDatas.size() + "）");

                    adapter.notifyDataSetChanged();

                    break;
                default:
                    break;
            }
        }
    };


    private class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_lock;
    }

    private class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return unlockedSystemDatas.size() + 1 + unlockedUserDatas.size() + 1;
        }


        @Override
        public Object getItem(int position) {
            if (position <= unlockedUserDatas.size()) {
                return unlockedUserDatas.get(position - 1);
            } else {
                return unlockedSystemDatas.get(position - 2 - unlockedUserDatas.size());
            }
        }


        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView tv_userTable = new TextView(getActivity());
                tv_userTable.setText("个人软件（" + unlockedUserDatas.size() + "）");
                tv_userTable.setTextColor(Color.WHITE);
                tv_userTable.setBackgroundColor(Color.GRAY);//背景为灰色
                return tv_userTable;
            } else if (position == unlockedUserDatas.size() + 1) {
                TextView tv_userTable = new TextView(getActivity());
                tv_userTable.setText("系统软件（" + unlockedSystemDatas.size() + "）");
                tv_userTable.setTextColor(Color.WHITE);
                tv_userTable.setBackgroundColor(Color.GRAY);//背景为灰色
                return tv_userTable;
            } else {
                //界面的缓存
                ViewHolder viewHolder = new ViewHolder();
                if (convertView != null && convertView instanceof RelativeLayout) {
                    viewHolder = (ViewHolder) convertView.getTag();

                } else {
                    convertView = View.inflate(getContext(), R.layout.item_fragment_unlock_listview_item, null);
                    viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_fragment_unlock_listview_item_icon);
                    viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_fragment_unlock_listview_item_title);
                    viewHolder.iv_lock = (ImageView) convertView.findViewById(R.id.iv_fragment_unlock_listview_item_lock);
                    convertView.setTag(viewHolder);
                }

                final AppBean bean = (AppBean) getItem(position);
                if (bean == null) {
                    return convertView;
                }
                viewHolder.iv_icon.setImageDrawable(bean.getIcon());

                viewHolder.tv_name.setText(bean.getAppName());

                setImageViewEventAndBackground(viewHolder.iv_lock,convertView, bean.getPackName());
                return convertView;
            }
        }
    }

    /**
     * 设置标题lab的内容
     *
     * @param tv_lab
     */
    public void setTitleLabText(TextView tv_lab) {

    }

    /**
     * @param packName
     * @return 判断该软件是否在加锁的软件数据库中
     */
    public boolean isMyData(String packName) {
        return false;
    }

    /**
     * 设置ImageView的背景和点击事件
     *  @param iv_lock
     * @param convertView
     * @param packName
     */
    public void setImageViewEventAndBackground(ImageView iv_lock, View convertView, final String packName) {

    }
}
