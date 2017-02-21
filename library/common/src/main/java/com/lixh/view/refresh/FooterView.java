package com.lixh.view.refresh;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;

public abstract class FooterView implements ImplPull {
    public View footerView;
    Context context;
    public SpringView refreshView;
    public View getView() {
        return footerView;
    }

    @Override
    public void setPull(SpringView refreshView) {
        this.refreshView = refreshView;
    }
    public FooterView(Context context) {
        this.context = context;
        footerView = LayoutInflater.from(context).inflate(getLayoutId(), null);
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

    protected <VT extends View> VT $(@IdRes int id) {
        return (VT) footerView.findViewById(id);
    }

    public int getHeight() {
        footerView.measure(0, 0);
        return footerView.getMeasuredHeight();
    }

}