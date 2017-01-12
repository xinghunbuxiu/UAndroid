package com.lixh.uandroid.ui;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lixh.base.BaseActivity;
import com.lixh.base.adapter.ViewHolder;
import com.lixh.base.adapter.recyclerview.OnItemClickListener;
import com.lixh.uandroid.R;
import com.lixh.uandroid.presenter.MainPresenter;
import com.lixh.uandroid.view.ScrollLayout;
import com.lixh.view.UToolBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;


public class MainActivity extends BaseActivity<MainPresenter> {
    MainPresenter mainPresenter;
    @Bind(R.id.recycle)
    RecyclerView recycle;
    @Bind(R.id.recycle1)
    RecyclerView recycle1;
    List<Integer> list = new ArrayList<>();;
    @Bind(R.id.scrollView)
    ScrollLayout scrollLayout;
    @Override
    public int getLayoutId() {
        return R.layout.scrolllayout;
    }

    @Override
    public boolean initTitle(UToolBar toolBar) {
        toolBar.setTitle("scrolllayout");
        return false;
    }


    @Override
    protected void init(Bundle savedInstanceState) {
        mainPresenter = getPresenter();

        for (int i = 0; i < 15; i++) {
            list.add(R.mipmap.ic_launcher);
        }
        GridLayoutManager manager = new GridLayoutManager(this, 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recycle1.setLayoutManager(manager);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        recycle.setLayoutManager(gridLayoutManager);
        LinearLayoutDecoration layoutDecoration = new LinearLayoutDecoration(this, LinearLayoutManager.VERTICAL, 10, R.color.color_sys_bg);
        recycle.addItemDecoration(layoutDecoration);
        com.lixh.base.adapter.recyclerview.CommonAdapter adapter = new com.lixh.base.adapter.recyclerview.CommonAdapter<Integer>(this, R.layout.item_ciew, list) {
            @Override
            public void convert(ViewHolder holder, Integer o) {
                holder.setImageResource(R.id.iv, o);
            }
        };
        com.lixh.base.adapter.recyclerview.CommonAdapter adapter1 = new com.lixh.base.adapter.recyclerview.CommonAdapter<Integer>(this, R.layout.item_ciew, list) {
            @Override
            public void convert(ViewHolder holder, Integer o) {
                holder.setImageResource(R.id.iv, o);
            }
        };
        adapter1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, int position) {
//                intent.toActivity(MainActivity.class);
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, int position) {
                return false;
            }
        });
        recycle1.setAdapter(adapter1);
        recycle.setAdapter(adapter);
    }
}
