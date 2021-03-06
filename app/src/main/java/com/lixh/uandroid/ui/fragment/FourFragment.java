package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;
import android.webkit.WebView;

import com.lixh.base.BaseFragment;
import com.lixh.uandroid.R;
import com.lixh.view.UToolBar;
import com.lixh.view.refresh.SpringView;

import butterknife.Bind;


public class FourFragment extends BaseFragment {

    @Bind(R.id.web_view)
    WebView webView;
    @Bind(R.id.refresh)
    SpringView refresh;
    public FourFragment() {

    }

    @Override
    protected void init(Bundle savedInstanceState) {
        webView.loadUrl("https://www.diycode.cc/topics/409");
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
//        refresh.setOnLoadListener(new PullRefreshView.OnLoadListener() {
//            @Override
//            public void onLoad() {
//                refresh.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        refresh.finishRefreshAndLoadMore();
//                    }
//                }, 400);
//
//
//            }
//        });
    }

    @Override
    public void initTitle(UToolBar toolBar) {
        toolBar.setTitle("webView");
    }

    @Override
    public int getLayoutId() {
        return R.layout.web_view;
    }

}
