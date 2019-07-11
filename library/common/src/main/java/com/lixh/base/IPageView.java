package com.lixh.base;

import android.view.View;

/**
 * Created by LIXH on 2018/11/19.
 * email lixhVip9@163.com
 * des
 */

public interface IPageView {
    int getLayoutId();

    View getView();

    void init();

    void initView();
}
