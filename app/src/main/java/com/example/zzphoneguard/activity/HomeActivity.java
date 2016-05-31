package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.utils.Md5Utils;
import com.example.zzphoneguard.utils.MyConstants;
import com.example.zzphoneguard.utils.SaveData;

/**
 * Created by 狗蛋儿 on 2016/4/15.
 */
public class HomeActivity extends Activity {
    private GridView homeMenu;
    private MyAdapter adapter;
    private EditText ed_passwordone;
    private EditText ed_passwordtwo;
    private Button bt_savapassword;
    private Button bt_canclepassword;
    private int icons[]={R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app
            ,R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan
            ,R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings};
    private String names[]={"手机防盗","通讯卫士","软件管家"
            ,"进程管理","流量统计","病毒查杀"
            ,"缓存清理","高级工具","设置中心"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvents();
    }

    /**
     * 初始化组件的事件
     */
    private void initEvents() {
        homeMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://手机防盗
                        if (!TextUtils.isEmpty(SaveData.getString(getApplicationContext(), MyConstants.PASSWORD, null))) {
                            showCheckPasswordDialog();
                            break;
                        }
                        showSettingPasswordDialog();
                        break;
                    case 1: {//通讯中心
                        Intent intent = new Intent(HomeActivity.this, TelSmsSafeActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 8:{//设置中心
                        Intent intent = new Intent(HomeActivity.this, SettingCenterActivity.class);
                        startActivity(intent);
                        break;
                    }
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 登陆密码对话框
     */
    private void showCheckPasswordDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
        View view=View.inflate(getApplicationContext(), R.layout.checkpassworddialog_home_view, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        final EditText  ed_checkpasseord= (EditText) view.findViewById(R.id.ed_dialog_password);
        Button bt_cacle= (Button) view.findViewById(R.id.bt_dialog_password_cancle);
        Button bt_ok= (Button) view.findViewById(R.id.bt_dialog_password_ok);
        bt_cacle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password=ed_checkpasseord.getText().toString().trim();
                password=Md5Utils.md5(password);
                if (password.equals(SaveData.getString(getApplicationContext(), MyConstants.PASSWORD, null))){
                    Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                }else{
                    Toast.makeText(HomeActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        adapter=new MyAdapter();
        homeMenu.setAdapter(adapter);
    }

    /**
     * 构造器
     */
    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return icons.length;
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
            View v=View.inflate(HomeActivity.this,R.layout.item_home_grid,null);
            ImageView iv_icon= (ImageView) v.findViewById(R.id.iv_item_grid_icon);
            TextView tv_name= (TextView) v.findViewById(R.id.tv_item_grad_name);
            iv_icon.setImageResource(icons[position]);
            tv_name.setText(names[position]);
            return v;
        }
    }


    /**
     * 初始化组件
     */
    private void initView() {
        setContentView(R.layout.homeactivity_layout);
        homeMenu = (GridView) findViewById(R.id.gd_home_menu);

    }

    /**
     *弹出设置密码对话框
     */
    private void showSettingPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        View view = View.inflate(getApplicationContext(), R.layout.alertdialog_view, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        ed_passwordone= (EditText) view.findViewById(R.id.ed_passwordone);
        ed_passwordtwo= (EditText) view.findViewById(R.id.ed_passwordtwo);
        bt_savapassword= (Button) view.findViewById(R.id.bt_dialog_savepassword);
        bt_canclepassword = (Button) view.findViewById(R.id.bt_dialog_canclepassword);
        bt_canclepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bt_savapassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwordone=ed_passwordone.getText().toString().trim();
                String passwordtwo=ed_passwordtwo.getText().toString().trim();
                if (TextUtils.isEmpty(passwordone)||TextUtils.isEmpty(passwordtwo)){
                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }else if (!passwordone.equals(passwordtwo)){
                    Toast.makeText(getApplicationContext(), "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    passwordone= Md5Utils.md5(passwordone);
                    Log.d("password:", passwordone);
                    SaveData.putString(getApplicationContext(), MyConstants.PASSWORD, passwordone);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
}
