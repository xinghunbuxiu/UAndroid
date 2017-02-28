package com.lixh.base;


import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by LIXH on 2017/2/28.
 * email lixhVip9@163.com
 * des
 */

public class Page {
    Builder builder;

    enum Type {
        List,
        Grid,
        DragGrid

    }

    static Context mContext;

    public Page(Builder builder) {
        this.builder = builder;

    }

    protected View inflate(int layoutResID) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layoutResID, null);
        return view;
    }

    public static class Builder {
        Type type;
        RecyclerView.Adapter adapter;
        boolean refresh;
        boolean loadMore;
        int divideHeight;
        int divideColor;

        public boolean isRefresh() {
            return refresh;
        }

        public void setRefresh(boolean refresh) {
            this.refresh = refresh;
        }

        public boolean isLoadMore() {
            return loadMore;
        }

        public void setLoadMore(boolean loadMore) {
            this.loadMore = loadMore;
        }

        public int getDivideHeight() {
            return divideHeight;
        }

        public void setDivideHeight(@DimenRes int divideHeight, @ColorRes int color) {
            this.divideHeight = divideHeight;
            this.divideColor = color;
        }

        public Builder(Context context) {
            mContext = context;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public void setAdapter(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        public Page Build() {
            return new Page(this);
        }
    }
}
