package com.lixh.utils;

import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    SparseArray<View> views = new SparseArray<>();
    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T $(int viewId) {
        View view = null;
        if (views.get(viewId) == null) {
            views.put(viewId, mViews.findViewById(viewId));
        }
        view = views.get(viewId);

        return (T)view;
    }

    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, String text) {
        TextView tv = $(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param text
     * @param onClickListener
     * @return
     */
    public ViewHolder setText(int viewId, String text, View.OnClickListener onClickListener) {
        TextView tv = $(viewId);
        tv.setText(text);
        tv.setOnClickListener(onClickListener);
        return this;
    }
    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param text
     * @param onClickListener
     * @return
     */
    public ViewHolder setBtnText(int viewId, CharSequence text, View.OnClickListener onClickListener) {
        Button tv = $(viewId);
        tv.setText(text);
        tv.setOnClickListener(onClickListener);
        return this;
    }

    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param text
     * @param onClickListener
     * @return
     */
    public ViewHolder setBtnText(int viewId, int tag, CharSequence text, View.OnClickListener onClickListener) {
        Button tv = $(viewId);
        tv.setText(text);
        tv.setTag(tag);
        tv.setOnClickListener(onClickListener);
        return this;
    }
    public ViewHolder setOnClickListener(int viewId, View.OnClickListener onClickListener) {
        View view = $(viewId);
        view.setOnClickListener(onClickListener);
        return this;
    }

    public ViewHolder setEditText(int viewId, String msg) {
        EditText edit = $(viewId);
        edit.setText(msg);
        return this;
    }

    public ViewHolder setVisible(int viewId, int gone) {
        View view = $(viewId);
        view.setVisibility(gone);
        return this;
    }
}
