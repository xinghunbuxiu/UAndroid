package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lixh.base.BaseRVFragment;
import com.lixh.base.adapter.EasyRVHolder;
import com.lixh.uandroid.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseRVFragment {

    List<Integer> list = new ArrayList<>();
    public static HomeFragment newInstance(String param1) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void init(Bundle savedInstanceState) {
        getInfo(page);
    }

    @Override
    public boolean initTitle() {
        toolBar.setTitle("home");
        return true;
    }

    CommonAdapter adapter;
    @Override
    public RecyclerView.Adapter getAdapter() {
        adapter = new CommonAdapter<Integer>(R.layout.item_ciew, list) {
            @Override
            protected void onBindData(EasyRVHolder holder, int position, Integer item) {
                super.onBindData(holder, position, item);
            }

            @Override
            public void onItemClick(View view, int position, Integer data) {

            }
        };
        return adapter;
    }

    @Override
    public void onLoadMore() {
        getInfo(page);
    }

    @Override
    public void onRefresh() {
        getInfo(page);
    }

    @Override
    public void onItemClick(int position) {

    }

    public void getInfo(int page) {
        for (int i = 0; i < 15; i++) {
            list.add(R.mipmap.ic_launcher);
        }

    }

    @Override
    public void reload() {

    }
}
