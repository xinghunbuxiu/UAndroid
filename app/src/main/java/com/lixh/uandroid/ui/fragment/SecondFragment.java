package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lixh.base.BaseFragment;
import com.lixh.base.adapter.ViewHolder;
import com.lixh.base.adapter.recyclerview.CommonAdapter;
import com.lixh.uandroid.R;
import com.lixh.uandroid.ui.LinearLayoutDecoration;
import com.lixh.view.refresh.PullRefreshView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;


public class SecondFragment extends BaseFragment {

    @Bind(R.id.recycle)
    RecyclerView recycle;
    @Bind(R.id.refresh)
    PullRefreshView refresh;
    List<Integer> list = new ArrayList<>();
    public static SecondFragment newInstance(String param1) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        for (int i = 0; i < 15; i++) {
            list.add(R.mipmap.ic_launcher);
        }
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        recycle.setLayoutManager(gridLayoutManager);
        LinearLayoutDecoration layoutDecoration = new LinearLayoutDecoration(activity, LinearLayoutManager.VERTICAL, 10, R.color.color_sys_bg);
        recycle.addItemDecoration(layoutDecoration);
        final CommonAdapter adapter = new CommonAdapter<Integer>(getActivity(), R.layout.item_ciew, list) {
            @Override
            public void convert(ViewHolder holder, Integer o) {
                holder.setImageResource(R.id.iv, o);
            }
        };
        refresh.setOnRefreshListener(new PullRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        refresh.finishRefresh();
                    }
                }, 400);

            }
        });
        refresh.setOnLoadListener(new PullRefreshView.OnLoadListener() {
            @Override
            public void onLoad() {
                refresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 15; i++) {
                            list.add(R.mipmap.ic_launcher);
                        }
                        adapter.notifyDataSetChanged();
                        refresh.finishLoadMore();
                    }
                }, 400);


            }
        });
        recycle.setAdapter(adapter);
    }

    @Override
    public boolean initTitle() {
        toolBar.setTitle("recycle_view");
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.recycle_view;
    }


}
