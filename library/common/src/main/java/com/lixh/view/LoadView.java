package com.lixh.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.lixh.R;
import com.lixh.rxhttp.Observable;
import com.lixh.swipeback.app.SwipeBackLayout;
import com.lixh.utils.LoadingTip;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


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
    int windowType;
    protected SwipeBackLayout layout;
    //定义通用的布局 根布局为Liearlayout +ToolBar + LinearLayout
    public LoadView(Builder builder, int windowType) {
        this.builder = builder;
        this.windowType = windowType;
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
        if (windowType == WindowType.ACTIVITY) {
            createActivity();
        }
    }

    public LoadView createActivity() {
        mContext.findViewById(Window.ID_ANDROID_CONTENT);
        Window window = mContext.getWindow();
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        decorView.setId(Window.ID_ANDROID_CONTENT);
        decorView.removeAllViews();
        decorView.addView(RootView);
        return this;
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
        setContentView(view, true);
    }

    public void setContentView(View view, boolean belowTitle) {
        LayoutParams root = getLayoutParams();
        if (belowTitle) {
            root.addRule(RelativeLayout.BELOW, R.id.toolbar);
        }
        RootView.addView(view, root);
        toolbar.bringToFront();
    }

    public void setLayoutTop() {
        Window window = mContext.getWindow();
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        if (decorView.getChildAt(0) != null) {
            View view = decorView.getChildAt(0);
            view.setPadding(0, 0, 0, 0);
            view.invalidate();
        }
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

    @IntDef({
            WindowType.ACTIVITY,
            WindowType.FRAGMENT,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface WindowType {
        int FRAGMENT = 1;
        int ACTIVITY = 2;
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

        public LoadView createActivity() {
            return new LoadView(this, WindowType.ACTIVITY);
        }

        public LoadView createFragment() {
            return new LoadView(this, WindowType.FRAGMENT);
        }

        public Builder setToolBar(boolean hasToolbar) {
            this.hasToolbar = hasToolbar;
            return this;
        }

    }

    ;
}
