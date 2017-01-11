package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;
import android.widget.ListView;

import com.lixh.base.BaseFragment;
import com.lixh.base.adapter.ViewHolder;
import com.lixh.base.adapter.abslistview.CommonAdapter;
import com.lixh.uandroid.R;
import com.lixh.view.refresh.PullRefreshView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;


public class FirstFragment extends BaseFragment {

    @Bind(R.id.list)
    ListView list_view;
    @Bind(R.id.refresh)
    PullRefreshView refresh;
    List<Integer> list = new ArrayList<>();
    int position = 0;
    public static FirstFragment newInstance(String param1) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    CommonAdapter adapter;
    @Override
    protected void init(Bundle savedInstanceState) {
        for (int i = 0; i < 15; i++) {
            list.add(R.mipmap.ic_launcher);
        }

        adapter = new CommonAdapter<Integer>(getActivity(), R.layout.item_ciew, list) {
            @Override
            public void convert(ViewHolder holder, Integer o) {
                holder.setText(R.id.text,holder.getmPosition()+"");
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
        list_view.setAdapter(adapter);
    }

    @Override
    public boolean initTitle() {
        toolBar.setTitle("listView");
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_view;
    }
}
