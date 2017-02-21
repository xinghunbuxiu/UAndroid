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

    protected void initList() {
        for (int i = 0; i < 15; i++) {
            list.add(R.mipmap.ic_launcher);
        }
    }
    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        initList();
    }

    @Override
    public boolean initTitle() {
        toolBar.setTitle("home");
        return true;
    }

    @Override
    public RecyclerView.Adapter initAdapter() {
        CommonAdapter adapter = new CommonAdapter<Integer>(R.layout.item_ciew, list) {
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

    }

    @Override
    public void reload() {

    }

}
