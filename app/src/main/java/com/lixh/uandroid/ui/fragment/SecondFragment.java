package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.lixh.base.BaseFragment;
import com.lixh.base.Page;
import com.lixh.base.adapter.recycleview.BaseViewHolder;
import com.lixh.uandroid.R;
import com.lixh.uandroid.ui.PieChartActivity;
import com.lixh.utils.LoadingTip;
import com.lixh.view.UToolBar;

import java.util.ArrayList;
import java.util.List;


public class SecondFragment extends BaseFragment {
    Page.Builder builder;
    List<Integer> list = new ArrayList<>();
    public SecondFragment() {

    }

    @Override
    public void initTitle(UToolBar toolBar) {
        toolBar.setTitle("recycle_view");
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        builder = new Page.Builder<Integer>(activity) {
            @Override
            public void onBindViewData(BaseViewHolder viewHolder, int position, Integer item) {
                super.onBindViewData(viewHolder, position, item);
            }

            @Override
            public void onItemClick(View view, int position, Integer data) {
                intent.go(PieChartActivity.class);
            }
        }.setAutoLoadMore(true).setRefresh(true).setDivideHeight(R.dimen.space_7);
        builder.setOnLoadingListener(onLoadingListener);
        builder.setRVAdapter(R.layout.item_ciew, list);
        layout.setContentView(builder.Build(Page.PageType.List).getRootView());
    }

    Page.OnLoadingListener onLoadingListener = new Page.OnLoadingListener() {
        @Override
        public void load(int page, final Page.OnLoadFinish onLoadFinish) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    for (int i = 0; i < 8; i++) {
                        list.add(R.mipmap.ic_launcher);
                    }
                    onLoadFinish.finish(list, LoadingTip.LoadStatus.FINISH);
                }
            }, 100);
        }
    };

    @Override
    public int getLayoutId() {
        return 0;
    }


}
