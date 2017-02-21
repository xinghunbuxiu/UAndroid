package com.lixh.view.refresh;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;

public abstract class HeaderView implements ImplPull {
    public View headerView;
    Context context;
    SpringView refreshView;
    public View getView() {
        return headerView;
    }
    @Override
    public void setPull(SpringView refreshView) {
       this.refreshView=refreshView;
    }
    public HeaderView(Context context) {
        this.context = context;
        headerView = LayoutInflater.from(context).inflate(getLayoutId(), null);
        initView();
    }

    /**
     * 这个方法用于设置下拉最大高度(max height)，无论怎么拉动都不会超过这个高度
     * 返回值大于0才有效，如果<=0 则默认600px
     * 默认返回0
     */
    public int getDragMaxHeight() {
        return 0;
    }

    public int getHeight() {
        headerView.measure(0, 0);
        return headerView.getMeasuredHeight();
    }

    protected <VT extends View> VT $(@IdRes int id) {
        return (VT) headerView.findViewById(id);
    }


}