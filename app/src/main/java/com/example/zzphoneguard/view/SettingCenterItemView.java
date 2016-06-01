package com.example.zzphoneguard.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.zzphoneguard.R;


/**
 * Created by 狗蛋儿 on 2016/5/4.
 */
public class SettingCenterItemView extends LinearLayout {
    private TextView tv_title;
    private TextView tv_content;
    private CheckBox cb_check;
    private String[] contents;
    private View item;
    public SettingCenterItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initEvent();
        String title = attrs.getAttributeValue("http://schemas.android.com/apk/res-aotu", "title");
        String content = attrs.getAttributeValue("http://schemas.android.com/apk/res-aotu", "content");
        contents = content.split("-");
        tv_title.setText(title);
        tv_content.setText(contents[0]);
        tv_content.setTextColor(Color.RED);
    }

    /**
     * Item的点击事件
     * @param listener
     */
    public void setItemClickListener(OnClickListener listener){
        item.setOnClickListener(listener);
    }

    /**
     * 设置checkbox是否被点击
     */
    public void setChecked(boolean isChecked){
        cb_check.setChecked(isChecked);
    }

    /**
     * 获得checkbox的点击状态
     * @return
     */
    public boolean isChecked(){
        return cb_check.isChecked();
    }

    /**
     * 初始化组件事件
     */
    private void initEvent() {
       /* item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_check.setChecked(!cb_check.isChecked());
            }
        });*/
        cb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    tv_content.setTextColor(Color.BLACK);
                    tv_content.setText(contents[1]);
                }else{
                    tv_content.setTextColor(Color.RED);
                    tv_content.setText(contents[0]);

                }
            }
        });
    }

    /**
     * 初始化组件
     */
    private void initView() {
        item = View.inflate(getContext(), R.layout.item_settingcenter,null);
        addView(item);
        tv_title = (TextView) item.findViewById(R.id.tv_settingcenter_aotuupdate_title);
        tv_content = (TextView) item.findViewById(R.id.tv_settingcenter_aotuupdate_content);
        cb_check = (CheckBox) item.findViewById(R.id.cb_settingcenter_aotuupdate_check);
    }


    public SettingCenterItemView(Context context) {
        super(context);
        initView();
    }
}
