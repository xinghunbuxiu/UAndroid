package com.lixh.base.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by Moushao on 2017/8/23.
 */

public class VBaseHolder<T> extends RecyclerView.ViewHolder {
    public ItemListener mListener;
    public Context mContext;
    public View mView;
    public T mData;
    public int position;
    public ActionListener actionListener;

    public VBaseHolder(View itemView) {
        super(itemView);
        mView = itemView;
        ButterKnife.bind(this, itemView);
        init();
    }

    public void init() {
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void setListener(ItemListener listener) {
        mListener = listener;
    }

    public void setData(int ps, T mData) {
        this.mData = mData;
        position = ps;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }
}
