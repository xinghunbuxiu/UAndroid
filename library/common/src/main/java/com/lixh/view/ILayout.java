package com.lixh.view;

import android.os.Bundle;

/**
 * Created by LIXH on 2017/5/17.
 * email lixhVip9@163.com
 * des
 */

public interface ILayout {
    void init(Bundle savedInstanceState);
    /**
     * 初始化标题
     *
     * @param toolBar
     */
    void initTitle(UToolBar toolBar);

    /**
     * 布局
     *
     * @return
     */
    int getLayoutId();

    boolean isShowBack();
    boolean isDoubleExit();
}
