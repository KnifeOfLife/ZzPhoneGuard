package com.example.zzphoneguard.fragment;

import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zzphoneguard.R;

/**
 * Created by 狗蛋儿 on 2016/10/8.
 */
public class UnlockedFragment extends BaseLockOrUnlockFragment {

    @Override
    public boolean isMyData(String packName) {
        return !allLockedPacks.contains(packName);
    }
    @Override
    public void setImageViewEventAndBackground(ImageView iv_lock, final View convertView, final String packName) {
        iv_lock.setImageResource(R.drawable.iv_lock_selector);

        iv_lock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dao.add(packName);
                //加个动画
                TranslateAnimation ta = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF,0,
                        Animation.RELATIVE_TO_SELF,1,
                        Animation.RELATIVE_TO_SELF,0,
                        Animation.RELATIVE_TO_SELF,0);
                ta.setDuration(300);

                convertView.startAnimation(ta);

                new Thread(){
                    @Override
                    public void run() {
                        SystemClock.sleep(300);
                        initData();
                    }
                }.start();
            }
        });
    }

    @Override
    public void setTitleLabText(TextView tv_lab) {
        tv_lab.setText("未加锁的软件个数（"+(unlockedSystemDatas.size()+unlockedUserDatas.size())+"）");

    }
}
