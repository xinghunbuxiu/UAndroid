package com.lixh.view.refresh;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LIXH on 2018/4/12.
 * email lixhVip9@163.com
 * des
 */

public abstract class StickNavAdapter {
    List<MultiItem> items_views = new ArrayList<>();
    int stickType = 0;

    public void addItemView(int type, @LayoutRes int layoutId) {
        MultiItem itemView = new MultiItem(type, layoutId);
        items_views.add(itemView);
    }

    public void setStickType(int type) {
        this.stickType = type;
    }

    public int getStickType() {
        return this.stickType;
    }

    protected <VT extends View> VT $(View v, @IdRes int id) {
        return (VT) v.findViewById(id);
    }


    public List<MultiItem> getItems_views() {
        return this.items_views;
    }

    public abstract void initView(View parent);
}
