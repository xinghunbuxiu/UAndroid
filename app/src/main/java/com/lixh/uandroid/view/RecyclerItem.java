package com.lixh.uandroid.view;

import com.lixh.view.multi.MultiItemEntity;

/**
 * Created by LIXH on 2017/7/21.
 * email lixhVip9@163.com
 * des
 */

public class RecyclerItem implements MultiItemEntity {
    public static final int ITEM_POSTER = 1;
    public static final int ITEM_HEADER = 2;
    private int type;

    public RecyclerItem(int type) {
        this.type = type;
    }

    @Override
    public int getItemType() {
        return type;
    }
}