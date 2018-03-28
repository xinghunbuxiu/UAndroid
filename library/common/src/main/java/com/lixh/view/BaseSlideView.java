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
    public SlideMenu slideMenu;
    Activity activity;
    public boolean following;
    public boolean isAnim;
    public boolean isFullScreen;
    public boolean isEnabledEdge = true;

    public <T> T getActivity() {
        return (T) activity;
    }

    public BaseSlideView(Activity activity) {
        this.activity = activity;
        init();
    }

    public void setSlideMenu(SlideMenu slideMenu) {
        this.slideMenu = slideMenu;
    }
    @Override
    public View getView() {
        if (slideView == null) {
            slideView = View.inflate(activity, getLayoutId(), null);
            initView(slideView);
        }
        return slideView;
    }

    protected <VT extends View> VT $(@IdRes int id) {
        return (VT) slideView.findViewById(id);
    }

    @Override
    public int getMeasuredWidth() {
        return slideMenu.getSlideWidth();
    }

    public boolean isFollowing() {
        return following;
    }

    public boolean isAnim() {
        return isAnim;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    //是否允许 边缘触控
    public boolean isEnabledEdge() {
        return isEnabledEdge;
    }



}
