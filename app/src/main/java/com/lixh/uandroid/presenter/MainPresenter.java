package com.lixh.uandroid.presenter;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.lixh.presenter.BasePresenter;
import com.lixh.uandroid.R;
import com.lixh.utils.Alert;


/**
 * Created by LIXH on 2016/11/14.
 * email lixhVip9@163.com
 * des
 */
public class MainPresenter extends BasePresenter  {




    @Override
    public void onCreate(Bundle savedInstanceState) {
        toolbar.setGravity(Gravity.LEFT);
        toolbar.setTitle("dddddd");
        toolbar.setSubtitle("ddddddsssss");
        toolbar.setNavigationIcon(R.mipmap.back_normal, "返回");
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setRightImage(R.mipmap.back_normal);
        toolbar.setRightText("ddddd");
        toolbar.setOnMenuItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alert.showShort(v.getId() + "");
            }
        });

    }

}
