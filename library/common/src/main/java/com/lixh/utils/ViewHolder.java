package com.lixh.utils;

import android.view.View;

/**
 * Created by LIXH on 2016/10/28.
 * email lixhVip9@163.com
 * des
 */

public class ViewHolder {

    View mViews;

    public ViewHolder(View view) {
        this.mViews = view;
    }

    public View getView() {
        return mViews;
    }
    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        return (T) mViews.findViewById(viewId);
    }

}
