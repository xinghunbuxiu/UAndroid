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

    /**
     * 是否有标题栏
     *
     * @return
     */
    boolean hasToolBar();

    /**
     * 是否有返回按钮
     *
     * @return
     */
    public boolean isBack();

    /**
     * 是否允许左划结束
     *
     * @return
     */
    public boolean enableSwipeBack();

    /**
     * @return 是否双j击退出
     */
    public boolean isDoubleExit();

    /**
     * 标题栏是否置顶
     *
     * @return
     */
    public boolean isContentTop();

    /**
     * 侧滑
     */
    int getSlide();

    /**
     * 侧滑View
     * SlideView extends BaseSlideView
     *
     * @return
     */
    BaseSlideView getSlideView();

    
}
