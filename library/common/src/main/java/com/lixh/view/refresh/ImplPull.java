package com.lixh.view.refresh;

public interface ImplPull {


    enum StateType {
        NONE,
        PULL,
        RELEASE,
        LOADING,
        LOAD_CLOSE,
        ONCLICK
    }

    enum ScrollState {
        TOP, NONE, BOTTOM
    }
     int getLayoutId();
     void setPull(SpringView refreshView);
     void initView();
    /**
     * @param maxY 滑动的 最大 y
     * @param y
     */
    void Scroll(int maxY, int y);

    void onScrollChange(StateType state);

    int getHeight();
}