package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lixh.base.BaseFragment;
import com.lixh.base.adapter.ViewHolder;
import com.lixh.base.adapter.recyclerview.CommonAdapter;
import com.lixh.uandroid.R;
import com.lixh.uandroid.ui.LinearLayoutDecoration;
import com.lixh.uandroid.view.ScrollNavLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class HomeFragment extends BaseFragment {
    @Bind(R.id.recycle)
    RecyclerView recycle;
    @Bind(R.id.recycle1)
    RecyclerView recycle1;
    List<Integer> list = new ArrayList<>();
    @Bind(R.id.navLayout)
    ScrollNavLayout navLayout;

    public static HomeFragment newInstance(String param1) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    //五行: 收缩比例是：0.25，0.5，0.75，1

    @Override
    protected void init(Bundle savedInstanceState) {

        for (int i = 0; i < 15; i++) {
            list.add(R.mipmap.ic_launcher);
        }
        GridLayoutManager manager = new GridLayoutManager(getContext(), 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recycle1.setLayoutManager(manager);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        recycle.setLayoutManager(gridLayoutManager);
        LinearLayoutDecoration layoutDecoration = new LinearLayoutDecoration(activity, LinearLayoutManager.VERTICAL, 10, R.color.color_sys_bg);
        recycle.addItemDecoration(layoutDecoration);
        CommonAdapter adapter = new CommonAdapter<Integer>(getActivity(), R.layout.item_ciew, list) {
            @Override
            public void convert(ViewHolder holder, Integer o) {
                holder.setImageResource(R.id.iv, o);
            }
        };
        CommonAdapter adapter1 = new CommonAdapter<Integer>(getActivity(), R.layout.item_ciew, list) {
            @Override
            public void convert(ViewHolder holder, Integer o) {
                holder.setImageResource(R.id.iv, o);
            }
        };
        recycle1.setAdapter(adapter1);
        recycle.setAdapter(adapter);

    }

    @Override
    public boolean initTitle() {
        toolBar.setTitle("home");
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }


}
