package com.lixh.base;

import android.app.Activity;
import androidx.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;


/**
 * Created by LIXH on 2017/5/8.
 * email lixhVip9@163.com
 * des
 */

public abstract class BasePageView implements IPageView {
    public View baseView;
    public Activity activity;
    public ViewGroup root;

    public <T> T getActivity() {
        return (T) activity;
    }

    public BasePageView(Activity activity, ViewGroup root) {
        this.root = root;
        this.activity = activity;
        initView();
        init();
    }

    @Override
    public void initView() {
        if (baseView == null) {
            baseView = View.inflate(activity, getLayoutId(), root);
            ButterKnife.bind(this,baseView);
        }
    }

    @Override
    public View getView() {
        return baseView;
    }

    protected <VT extends View> VT $(@IdRes int id) {
        return (VT) baseView.findViewById(id);
    }

}
