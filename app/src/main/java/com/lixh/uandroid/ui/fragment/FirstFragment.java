package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lixh.base.BaseRVFragment;
import com.lixh.base.adapter.BaseViewHolder;
import com.lixh.base.adapter.RecyclerArrayAdapter;
import com.lixh.uandroid.R;

import java.util.ArrayList;
import java.util.List;


public class FirstFragment extends BaseRVFragment {

    List<Integer> list = new ArrayList<>();
    public static FirstFragment newInstance(String param1) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected void init(Bundle savedInstanceState) {
        for (int i = 0; i < 15; i++) {
            list.add(R.mipmap.ic_launcher);
        }
    }

    @Override
    public boolean initTitle() {
        toolBar.setTitle("listView");
        return true;
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        CommonArrayAdapter arrayAdapter = new CommonArrayAdapter<Integer>(list, R.layout.item_ciew) {

            @Override
            protected void onBindData(BaseViewHolder viewHolder, int position, Integer item) {
                super.onBindData(viewHolder, position, item);
            }

            @Override
            public void onItemClick(View view, int position, Integer data) {

            }
        };

        arrayAdapter.addHeader(new RecyclerArrayAdapter.HeaderItemView() {
            @Override
            public View OnCreateHeaderViewHolder(ViewGroup parent) {
                return null;
            }

            @Override
            public void onBindHeaderData(View headerView, int position) {

            }
        });
        arrayAdapter.
        return arrayAdapter;
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_view;
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void reload() {

    }

    @Override
    public void onItemClick(int position) {

    }

}
