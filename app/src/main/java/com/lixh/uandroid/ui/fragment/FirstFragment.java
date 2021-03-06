package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.lixh.base.BaseFragment;
import com.lixh.base.Page;
import com.lixh.base.adapter.recycleview.BaseViewHolder;
import com.lixh.uandroid.R;
import com.lixh.uandroid.ui.StickNavActivity;
import com.lixh.utils.LoadingTip;
import com.lixh.utils.ULog;
import com.lixh.view.UToolBar;

import java.util.ArrayList;
import java.util.List;


public class FirstFragment extends BaseFragment {
    Page.Builder builder;


    public FirstFragment() {

    }

    int i = 0;
    @Override
    protected void init(Bundle savedInstanceState) {

        builder = new Page.Builder<Integer>(activity) {
            @Override
            public void onBindViewData(BaseViewHolder viewHolder, int position, Integer item) {
                i++;
                ULog.e(i + "");
            }

            @Override
            public void onItemClick(View view, int position, Integer data) {

                intent.go(StickNavActivity.class);
            }
        }.setAutoLoadMore(true).setRefresh(true).setDivideHeight(R.dimen.space_7).setLoadTip(tip);
        builder.setOnLoadingListener(onLoadingListener);
        builder.setArrayAdapter(R.layout.item_ciew);
        Page page=builder.Build(Page.PageType.List);
        layout.setContentView(page.getRootView());
    }

    Page.OnLoadingListener onLoadingListener = new Page.OnLoadingListener() {
        @Override
        public void load(int page, final Page.OnLoadFinish onLoadFinish) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    List<Integer> list = new ArrayList<>();
                    for (int i = 0; i < 15; i++) {
                        list.add(R.mipmap.ic_launcher);
                    }
                    onLoadFinish.finish(list, LoadingTip.LoadStatus.FINISH);
                }
            }, 100);

        }
    };
    @Override
    public void initTitle(UToolBar toolBar) {
        toolBar.setTitle("ddddddddd");
        toolBar.setLogo(R.mipmap.ic_launcher);
    }

    @Override
    public int getLayoutId() {
      return   0;
    }

}
