package com.lixh.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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
    LinearLayout RootView;
    LoadingTip tip;
    Builder builder;
    UToolBar toolbar;
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
        RootView = (LinearLayout) inflate(R.layout.toolbar_layout);
        bottomView = new FrameLayout(mContext);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bottomView.setLayoutParams(lp);
        RootView.addView(getContentView(builder));
        initLayout();
    }

    private void initLayout() {
        toolbar = (UToolBar) RootView.findViewById(R.id.toolbar);
        if (!builder.hasToolbar) {
            RootView.removeView(toolbar);
        }
    }


    public static int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public void setContentView(View view) {
        bottomView.addView(view);
    }

    /**
     * 创建内容的布局 这里用的事RelativeLayout
     */
    public View getContentView(Builder builder) {
        tip = getEmptyView();
        bottomView.addView(tip);
        if (builder.getBottomLayout() > 0) {
            bottomView.addView(inflate(builder.getBottomLayout()));
        }
        return bottomView;
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
