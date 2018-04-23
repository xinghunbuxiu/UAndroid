package com.lixh.uandroid.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lixh.base.adapter.recycleview.EasyRVAdapter;
import com.lixh.base.adapter.recycleview.EasyRVHolder;
import com.lixh.uandroid.R;
import com.lixh.view.refresh.StickNavAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LIXH on 2018/4/12.
 * email lixhVip9@163.com
 * des
 */

public class CustomStickNavAdapter extends StickNavAdapter {
    Context mContext;
    List<Integer> list = new ArrayList<Integer>();
    EasyRVAdapter adapter;

    public CustomStickNavAdapter(Context mContext) {
        this.mContext = mContext;
        addItemView(R.id.stick_top, R.layout.item_stick_top);
        addItemView(R.id.stick_nav, R.layout.item_stick_nav);
        addItemView(R.id.stick_recycle, R.layout.item_stick_bottom);
        setStickType(R.id.stick_nav);
    }

    @Override
    public void initView(View parent) {
        TextView tvTop = $(parent, R.id.tv_top);
        tvTop.setText("dddddddddd");
        RecyclerView recyclerView = $(parent, R.id.stick_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new EasyRVAdapter<Integer>(mContext, list, R.layout.item_ciew) {

            @Override
            protected void onBindData(EasyRVHolder viewHolder, int position, Integer item) {

            }
        };
        recyclerView.setAdapter(adapter);
        initList();
    }

    protected void initList() {
        for (int i = 0; i < 10; i++) {
            list.add(R.mipmap.ic_launcher);
        }
        adapter.addAll(list);

    }
}
