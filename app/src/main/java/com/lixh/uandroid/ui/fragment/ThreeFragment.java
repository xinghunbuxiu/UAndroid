package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;
import android.widget.ScrollView;

import com.lixh.base.BaseFragment;
import com.lixh.uandroid.R;
import com.lixh.view.refresh.SpringView;

import butterknife.Bind;


public class ThreeFragment extends BaseFragment {

    @Bind(R.id.list)
    ScrollView list;
    @Bind(R.id.refresh)
    SpringView refresh;

    public static ThreeFragment newInstance(String param1) {
        ThreeFragment fragment = new ThreeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
    public boolean initTitle() {
        toolBar.setTitle("ScrollView");
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.scroll_view;
    }

}
