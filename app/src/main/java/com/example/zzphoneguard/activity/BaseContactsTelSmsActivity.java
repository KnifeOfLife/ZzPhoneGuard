package com.example.zzphoneguard.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.engine.ReadContactsEngine;
import com.example.zzphoneguard.mode.ContactBean;
import com.example.zzphoneguard.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 手机联系人的Activity
 */
public abstract class BaseContactsTelSmsActivity extends ListActivity {
    private static final int LOADING = 0;
    private static final int FINISH = 1;
    private ListView list;
    private MyAdapter adapter;
    private List<ContactBean> contacts=new ArrayList<ContactBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = getListView();
        adapter = new MyAdapter();
        list.setAdapter(adapter);
        initData();
        initEvent();
    }

    /**
     * 初始化组件事件
     */
    private void initEvent() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactBean contactBean = contacts.get(position);
                String phone = contactBean.getPhone();
                Intent intent = new Intent();
                intent.putExtra(MyConstants.SAFENUMBER,phone);
                setResult(1,intent);
                finish();
            }
        });
    }


    private Handler handle=new Handler(){
        private ProgressDialog pd;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOADING:
                    pd = new ProgressDialog(BaseContactsTelSmsActivity.this);
                    pd.setTitle("正在玩命加载中");
                    pd.show();
                    break;
                case FINISH:
                    if (pd!=null){
                        pd.dismiss();
                        pd=null;
                    }
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 初始化组件数据
     */
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what=LOADING;
                handle.sendMessage(msg);
                SystemClock.sleep(300);
//                contacts = ReadContactsEngine.readContacts(getApplicationContext());
                contacts = getDatas();
                msg=Message.obtain();
                msg.what=FINISH;
                handle.sendMessage(msg);
            }
        }.start();
    }

    public abstract List<ContactBean> getDatas();
    private class MyAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return contacts.size();
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
            View v=View.inflate(getApplicationContext(), R.layout.item_contacts,null);
            TextView tv_name= (TextView) v.findViewById(R.id.item_contact_name);
            TextView tv_phone= (TextView) v.findViewById(R.id.item_contact_phone);
            ContactBean contact=contacts.get(position);
            tv_name.setText(contact.getName());
            tv_phone.setText(contact.getPhone() );
            return v;
        }
    }

}
