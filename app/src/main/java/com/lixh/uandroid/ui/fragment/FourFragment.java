package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;
import android.webkit.WebView;

import com.lixh.base.BaseFragment;
import com.lixh.uandroid.R;
import com.lixh.view.refresh.PullRefreshView;

import butterknife.Bind;


public class FourFragment extends BaseFragment {

    @Bind(R.id.web_view)
    WebView webView;
    @Bind(R.id.refresh)
    PullRefreshView refresh;

    public static FourFragment newInstance(String param1) {
        FourFragment fragment = new FourFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        webView.loadUrl("https://www.diycode.cc/topics/409");
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
                        refresh.finishLoadMore();
                    }
                }, 400);


            }
        });
    }

    @Override
    public boolean initTitle() {
        toolBar.setTitle("webView");
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.web_view;
    }

}
