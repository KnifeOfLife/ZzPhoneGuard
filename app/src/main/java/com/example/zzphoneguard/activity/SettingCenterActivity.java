package com.example.zzphoneguard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zzphoneguard.R;
import com.example.zzphoneguard.service.ComingPhoneService;
import com.example.zzphoneguard.service.TelSmsBlackService;
import com.example.zzphoneguard.utils.MyConstants;
import com.example.zzphoneguard.utils.SaveData;
import com.example.zzphoneguard.utils.ServiceUtils;
import com.example.zzphoneguard.view.SettingCenterItemView;

/**
 * Created by 狗蛋儿 on 2016/5/4.
 */
public class SettingCenterActivity extends Activity {
    private SettingCenterItemView sciv_aotuupdate;//自动更新设置
    private SettingCenterItemView sciv_telsmsblack;//黑名单拦截设置
    private SettingCenterItemView sciv_phoneLocation;//来电归属地设置
    private TextView tv_locationStyle_content;
    private ImageView iv_changeStyle;
    private String[] styleName = new String[]{"卫士蓝","金属灰","苹果绿","活力橙","半透明"};
    private RelativeLayout rl_locationStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView();
        initEvent();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        sciv_aotuupdate.setChecked(SaveData.getBoolean(getApplicationContext(),MyConstants.AOTUUPDATE,false));
        sciv_telsmsblack.setChecked(ServiceUtils.isRunningService(getApplicationContext(),"com.example.zzphoneguard.service.TelSmsBlackService"));
        sciv_phoneLocation.setChecked(ServiceUtils.isRunningService(getApplicationContext(),"com.example.zzphoneguard.service.ComingPhoneService"));
        int index = Integer.parseInt(SaveData.getString(getApplicationContext(), MyConstants.STYLEINDEX,"0"));
        tv_locationStyle_content.setText(styleName[index]);
    }

    /**
     * 初始化组件事件
     */
    private void initEvent() {
        sciv_aotuupdate.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sciv_aotuupdate.setChecked(!sciv_aotuupdate.isChecked());
                SaveData.putBoolean(getApplicationContext(), MyConstants.AOTUUPDATE,sciv_aotuupdate.isChecked());
            }
        });
        sciv_telsmsblack.setItemClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ServiceUtils.isRunningService(getApplicationContext(),"com.example.zzphoneguard.service.TelSmsBlackService")){
                    Intent intent = new Intent(SettingCenterActivity.this, TelSmsBlackService.class);
                    stopService(intent);
                    sciv_telsmsblack.setChecked(false);
                }else{
                    Intent intent =new Intent(SettingCenterActivity.this,TelSmsBlackService.class);
                    startService(intent);
                    sciv_telsmsblack.setChecked(true);
                }
            }
        });
        sciv_phoneLocation.setItemClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ServiceUtils.isRunningService(getApplicationContext(),"com.example.zzphoneguard.service.ComingPhoneService")){
                    Intent intent = new Intent(SettingCenterActivity.this, ComingPhoneService.class);
                    stopService(intent);
                    sciv_phoneLocation.setChecked(false);
                }else{
                    Intent intent =new Intent(SettingCenterActivity.this,ComingPhoneService.class);
                    startService(intent);
                    sciv_phoneLocation.setChecked(true);
                }

            }
        });
        rl_locationStyle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder ab = new Builder(SettingCenterActivity.this);
                ab.setSingleChoiceItems(styleName, Integer.parseInt(SaveData.getString(getApplicationContext(),MyConstants.STYLEINDEX,"0")), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SaveData.putString(getApplicationContext(),MyConstants.STYLEINDEX,which+"");
                        tv_locationStyle_content.setText(styleName[which]);
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = ab.create();
                dialog.show();
            }
        });


    }

    /**
     * 初始化布局
     */
    private void initView() {
        setContentView(R.layout.activity_settingcenter);
        sciv_aotuupdate = (SettingCenterItemView) findViewById(R.id.sciv_settingconter_aotuupdate);
        sciv_telsmsblack = (SettingCenterItemView) findViewById(R.id.sciv_settingcenter_telsmsblack);
        sciv_phoneLocation = (SettingCenterItemView) findViewById(R.id.sciv_settingcenter_phonelocationservice);
        tv_locationStyle_content = (TextView) findViewById(R.id.tv_settingcenter_locationstyle_content);
        iv_changeStyle = (ImageView) findViewById(R.id.iv_settingcenter_locationstyle_select);
        rl_locationStyle = (RelativeLayout) findViewById(R.id.rl_settingcenter_locationstyle);
    }

}
