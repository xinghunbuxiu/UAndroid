package com.lixh.view;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.view.View;


/**
 * Created by LIXH on 2017/5/8.
 * email lixhVip9@163.com
 * des
 */

public abstract class BaseSlideView implements ISlideMenu {
    public View slideView;
    Activity activity;
    public boolean following;

    public <T> T getActivity() {
        return (T) activity;
    }

    public BaseSlideView(Activity activity) {
        this.activity = activity;
        initView();
    }

    public void createView(View slideView) {
        this.slideView =slideView;

    }

    @Override
    public View getView() {
        return slideView;
    }

    protected <VT extends View> VT $(@IdRes int id) {
        return (VT) slideView.findViewById(id);
    }

    @Override
    public int getMeasuredWidth() {
        slideView.measure(0, 0);
        return slideView.getWidth();
    }

    public boolean isFollowing() {
        return following;
    }
}
