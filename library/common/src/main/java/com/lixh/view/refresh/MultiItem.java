package com.lixh.view.refresh;

/**
 * Created by LIXH on 2018/4/12.
 * email lixhVip9@163.com
 * des
 */

public class MultiItem {

    int type;
    int layoutId;

    public MultiItem(int type, int layoutId) {
        this.type = type;
        this.layoutId = layoutId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLayoutId() {
        return this.layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }
}
