package com.lixh.uandroid.ui;

import android.os.Bundle;

import com.lixh.base.BaseActivity;
import com.lixh.uandroid.R;
import com.lixh.uandroid.view.CustomStickNavAdapter;
import com.lixh.view.UToolBar;
import com.lixh.view.refresh.StickNavLayout;

import butterknife.Bind;

/**
 * Created by LIXH on 2017/4/11.
 * email lixhVip9@163.com
 * des
 */

public class StickNavActivity extends BaseActivity {
    @Bind(R.id.stick_nav_layout)
    StickNavLayout stickNavLayout;
    CustomStickNavAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_stick_layout;
    }

    @Override
    public void initTitle(UToolBar toolBar) {

    }


    @Override
    public void init(Bundle savedInstanceState) {
        adapter = new CustomStickNavAdapter(this);
        stickNavLayout.setAdapter(adapter);
    }

}
