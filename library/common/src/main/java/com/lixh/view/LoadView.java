package com.lixh.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.lixh.R;
import com.lixh.rxhttp.Observable;
import com.lixh.swipeback.app.SwipeBackLayout;
import com.lixh.utils.LoadingTip;




/**
 * Created by LIXH on 2016/11/5.
 * email lixhVip9@163.com
 * des
 */

public class LoadView  extends Observable {
    public static final String TAG = "LoadView";
    public static Activity mContext;
    RelativeLayout RootView;
    LoadingTip tip;
    Builder builder;
    UToolBar toolbar;
    ViewStub view_Stub;
    FrameLayout bottomView;
    protected SwipeBackLayout layout;
    //定义通用的布局 根布局为Liearlayout +ToolBar + LinearLayout
    public LoadView(Builder builder) {
        this.builder = builder;
        commonView(builder);

    }

    public UToolBar getToolbar() {
        return toolbar;
    }



    //返回布局View
    public View getRootView() {
        return RootView;
    }

    //通用布局
    public void commonView(Builder builder) {
        RootView = (RelativeLayout) inflate(R.layout.toolbar_layout);
        initToolBar();
        initBottomView();
    }

    private void initBottomView() {
        LayoutParams root = getLayoutParams();
        root.addRule(RelativeLayout.BELOW, R.id.toolbar);
        tip = getEmptyView();
        RootView.addView(tip, root);
        if (builder.getBottomLayout() > 0) {
            RootView.addView(inflate(builder.getBottomLayout()), root);
        }
        ViewGroup view = (ViewGroup) mContext.getWindow().getDecorView();
        view.setId(android.R.id.content);
        view.removeAllViews();
        view.addView(RootView);
    }

    public LayoutParams getLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void initToolBar() {
        view_Stub = (ViewStub) RootView.findViewById(R.id.title_stub);
        if (builder.hasToolbar) {
            toolbar = (UToolBar) view_Stub.inflate();
        } else {
            RootView.removeView(view_Stub);
        }
    }


    public static int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public void setContentView(View view) {
        LayoutParams root = getLayoutParams();
        root.addRule(RelativeLayout.BELOW, R.id.toolbar);
        RootView.addView(view, root);
    }


    public LoadingTip getEmptyView() {
        if (tip == null) {
            tip = new LoadingTip(mContext);
        }
        return tip;
    }


    protected View inflate(int layoutResID) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layoutResID, null);
        return view;
    }

    public static class Builder {

        int mBottomLayout;
        boolean hasToolbar;
        View mBottomView;
        public Builder(Activity context) {
            mContext = context;
            mBottomLayout = -1;
            mBottomView = null;
            hasToolbar=true;
        }

        public int getBottomLayout() {
            return mBottomLayout;
        }

        public Builder setBottomLayout(int mBottomLayout) {
            this.mBottomLayout = mBottomLayout;
            return this;
        }

        public LoadView build() {
            return new LoadView(this);
        }


        public Builder setToolBar(boolean hasToolbar) {
            this.hasToolbar = hasToolbar;
            return this;
        }

    }

    ;
}
