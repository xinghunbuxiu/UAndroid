package com.lixh.uandroid.ui;


import android.os.Bundle;

import com.lixh.base.BaseActivity;
import com.lixh.uandroid.R;
import com.lixh.uandroid.presenter.MainPresenter;


public class MainActivity extends BaseActivity<MainPresenter> {
    MainPresenter mainPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public boolean initTitle() {
        return false;
    }


    @Override
    protected void init(Bundle savedInstanceState) {
        mainPresenter = getPresenter();
    }
}
