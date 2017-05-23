package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;
import android.widget.ScrollView;

import com.lixh.base.BaseFragment;
import com.lixh.uandroid.R;
import com.lixh.view.UToolBar;
import com.lixh.view.refresh.SpringView;

import butterknife.Bind;


public class ThreeFragment extends BaseFragment {

    @Bind(R.id.list)
    ScrollView list;
    @Bind(R.id.refresh)
    SpringView refresh;

    public ThreeFragment() {

    }

    @Override
    public void initTitle(UToolBar toolBar) {
        toolBar.setTitle("ScrollView");
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        refresh.setOnRefreshListener(new SpringView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh.finishRefreshAndLoadMore();
                    }
                }, 400);

            }
        });
        refresh.setOnLoadListener(new SpringView.OnLoadListener() {
            @Override
            public void onLoad() {
                refresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh.finishRefreshAndLoadMore();
                    }
                }, 400);


            }
        });
    }


    @Override
    public int getLayoutId() {
        return R.layout.scroll_view;
    }

}
