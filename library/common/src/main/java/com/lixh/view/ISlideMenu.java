package com.lixh.view;

import android.view.View;

/**
 * Created by LIXH on 2017/5/15.
 * email lixhVip9@163.com
 * des
 */

public interface ISlideMenu {
    int getLayoutId();

    View getView();
    void init();
    void initView(View slideView);

    int getMeasuredWidth();
}
