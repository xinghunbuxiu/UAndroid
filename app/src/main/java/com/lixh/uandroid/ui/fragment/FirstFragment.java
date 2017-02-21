package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lixh.base.BaseRVFragment;
import com.lixh.base.adapter.BaseViewHolder;
import com.lixh.uandroid.R;

import java.util.ArrayList;
import java.util.List;


public class FirstFragment extends BaseRVFragment {


    public static FirstFragment newInstance(String param1) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean initTitle() {
        toolBar.setTitle("listView");
        return true;
    }

    CommonArrayAdapter arrayAdapter;
    @Override
    public RecyclerView.Adapter initAdapter() {
        arrayAdapter = new CommonArrayAdapter<Integer>(R.layout.item_ciew) {

            @Override
            protected void onBindData(BaseViewHolder viewHolder, int position, Integer item) {
                super.onBindData(viewHolder, position, item);
            }

            @Override
            public void onItemClick(View view, int position, Integer data) {

            }
        };
        return arrayAdapter;
    }

    @Override
    public void onLoadMore() {
        getInfo(page);
    }

    @Override
    public void onRefresh() {
        getInfo(page);
    }

    public void getInfo(int page) {
        finishRefreshAndLoadMore();
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
               List<Integer> list = new ArrayList<>();
                for (int i = 0; i < 15; i++) {
                    list.add(R.mipmap.ic_launcher);
                }
                arrayAdapter.addAll(list);
            }
        }, 100);


    }
    @Override
    public void reload() {

    }
}
