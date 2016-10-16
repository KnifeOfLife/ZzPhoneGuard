package com.example.zzphoneguard.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.utils.MyConstants;
import com.example.zzphoneguard.utils.SaveData;

/**
 * Created by 狗蛋儿 on 2016/4/17.
 */
public class Setup3LostFindActivity extends BaseSetupActivity {
    private EditText ed_safenumber;
    private Button bt_selectNumber;

    @Override
    public void initView() {
        setContentView(R.layout.setup3_lostfind_activity);
        ed_safenumber = (EditText) findViewById(R.id.ed_safenumber);
        bt_selectNumber = (Button) findViewById(R.id.bt_setup3_selectnumber);
    }

    /**
     * 初始化组件的点击事件
     */
    @Override
    public void initEvent() {
        super.initEvent();
        bt_selectNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setup3LostFindActivity.this,ContactsActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    public void initData() {
        ed_safenumber.setText(SaveData.getString(getApplicationContext(),MyConstants.SAFENUMBER,""));
        super.initData();
    }

    @Override
    public void nextActivity() {
        startActivity(Setup4LostFindActivity.class);

    }

    @Override
    public void prevActivity() {
        startActivity(Setup2LostFindActivity.class);

    }

    /**
     * 如果安全号码为空 不允许跳转页面
     * @param v
     */
    @Override
    public void next(View v) {
        String safeNumber = ed_safenumber.getText().toString().trim();
        if (TextUtils.isEmpty(safeNumber)){
            Toast.makeText(getApplicationContext(), "安全号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }else {
            SaveData.putString(getApplicationContext(),MyConstants.SAFENUMBER,safeNumber);
        }
        super.next(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data!=null){
            String phone = data.getStringExtra(MyConstants.SAFENUMBER);
            if (TextUtils.isEmpty(phone)){
                Toast.makeText(Setup3LostFindActivity.this, "没有好友选择联系人", Toast.LENGTH_SHORT).show();
            }
            ed_safenumber.setText(phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
