package com.lixh.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.lixh.R;
import com.lixh.rxhttp.Observable;
import com.lixh.swipeback.SwipeBackActivityBase;
import com.lixh.swipeback.Utils;
import com.lixh.swipeback.app.SwipeBackActivityHelper;
import com.lixh.swipeback.app.SwipeBackLayout;
import com.lixh.utils.LoadingTip;
import com.lixh.utils.UIntent;
import com.lixh.view.SlideMenu.Slide;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Created by LIXH on 2016/11/5.
 * email lixhVip9@163.com
 * des
 */

public class LoadView extends Observable implements SwipeBackActivityBase {
    public static final String TAG = "LoadView";
    private static final int UBLayout = 0x999999;
    public static Activity mContext;
    private RelativeLayout RootView;
    private LoadingTip tip;
    private Builder builder;
    private UToolBar toolbar;
    private ViewStub view_Stub;
    int windowType;
    private SwipeBackLayout mSwipeBackLayout;
    private SwipeBackActivityHelper mHelper;
    private SwipeBackLayout layout;
    SlideMenu slideMenu1;
    BottomLayoutHelper bottomLayoutHelper;
    private UIntent intent;

    //定义通用的布局 根布局为Liearlayout +ToolBar + LinearLayout
    public LoadView(Builder builder, int windowType) {
        this.builder = builder;
        this.windowType = windowType;
        commonView(builder);

    }

    public UToolBar getToolbar() {
        return toolbar;
    }

    public UIntent getIntent() {
        return this.intent;
    }

    //返回布局View
    public View getRootView() {
        return RootView;
    }

    //通用布局
    public void commonView(Builder builder) {
        RootView = (RelativeLayout) builder.inflate(builder.contentTop ? R.layout.status_toolbar_layout : R.layout.toolbar_layout);
        initToolBar();
        initBottomView();
        initSwipe(builder.swipeBack);
        initSlideMenu(builder.slideMenu);
        intent = new UIntent(mContext);
    }

    /**
     * 底部导航条
     * 支持自定义
     *
     * @param hasBottomBar
     * @param root
     */
    private void initBottomBar(boolean hasBottomBar, LayoutParams root) {
        if (hasBottomBar) {
            bottomLayoutHelper = new BottomLayoutHelper(builder);
            RootView.addView(bottomLayoutHelper.getLayout(), root);
            bottomLayoutHelper.initBottomBarLayout();
        }
    }

    private void initSlideMenu(boolean slideMenu) {
        if (slideMenu) {
            slideMenu1 = new SlideMenu(mContext);
            slideMenu1.setId(R.id.slideMenu);
            slideMenu1.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    private void initBottomView() {
        LayoutParams root = getLayoutParams();
        root.addRule(RelativeLayout.BELOW, R.id.toolbar);
        tip = getEmptyView();
        RootView.addView(tip, root);
        if (builder.mBottomLayout > 0) {
            RootView.addView(builder.inflate(builder.mBottomLayout), root);
        } else {
            initBottomBar(builder.hasBottomBar, root);
        }
        if (windowType == WindowType.ACTIVITY) {
            createActivity();
        }
        if (toolbar != null) {
            toolbar.bringToFront();
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
            if (builder.contentTop) {
                toolbar.setHasBar();
            }
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
        if (toolbar != null) {
            toolbar.bringToFront();
        }
    }

    public void initSwipe(boolean enable) {
        if (enable) {
            mHelper = new SwipeBackActivityHelper(mContext);
            mHelper.onActivityCreate();
            //侧滑
            mSwipeBackLayout = getSwipeBackLayout();
            mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
            setSwipeBackEnable(enable);
        }
    }
    public LoadingTip getEmptyView() {
        if (tip == null) {
            tip = new LoadingTip(mContext);
        }
        return tip;
    }




    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        if (mHelper != null) {
            return mHelper.getSwipeBackLayout();
        } else return null;
    }


    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(mContext);
        SwipeBackLayout swipeBackLayout = getSwipeBackLayout();
        if (swipeBackLayout != null) {
            swipeBackLayout.scrollToFinishActivity();
        }
    }

    public void onPostCreate() {
        if (mHelper != null) {
            mHelper.onPostCreate();
        }
        if (slideMenu1 != null) {
            slideMenu1.attachToActivity(mContext);
            int slide = builder.slide;
            BaseSlideView view = builder.slideView;
            if (view != null && slide != Slide.NONE) {
                slideMenu1.addSlideView(view, slide);
            }

        }
    }

    /**
     * 是否滑动结束
     */
    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
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

        public int mBottomLayout;
        public boolean hasToolbar;
        public boolean contentTop;
        public boolean swipeBack;
        public boolean slideMenu;
        public boolean hasBottomBar;
        public int bottomBarLayout;
        @Slide
        public int slide;
        public BaseSlideView slideView;
        public LayoutInflater layoutInflater;
        public FragmentManager supportFragmentManager;
        public BottomNavigationItem items[];
        public Fragment fragments[];
        public BottomNavigationBar.OnTabSelectedListener tabSelectedListener;

        public Builder(FragmentActivity context) {
            mContext = context;
            supportFragmentManager = context.getSupportFragmentManager();
            fragments = null;
            items = null;
            mBottomLayout = -1;
            bottomBarLayout = -1;
            hasToolbar=true;
            slideView = null;
            contentTop = true;
            slideMenu = false;
            swipeBack = true;
            hasBottomBar = false;
            tabSelectedListener = null;
            slide = Slide.NONE;
        }

        /**
         * 添加底部
         *
         * @param items
         * @return
         */
        public Builder addItem(BottomNavigationItem... items) {
            this.items = items;
            return this;
        }

        /**
         * 添加底部
         *
         * @param fragments
         * @return
         */
        public Builder addFragment(Fragment... fragments) {
            this.fragments = fragments;
            return this;
        }

        public View inflate(int layoutResID) {
            if (layoutInflater == null) {
                layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            return layoutInflater.inflate(layoutResID, null);
        }

        public void setOnTabSelectedListener(BottomNavigationBar.OnTabSelectedListener listener) {
            this.tabSelectedListener = listener;
        }
        public Builder setSlideMenu(@Slide int slide, BaseSlideView slideView) {
            this.slide = slide;
            if (slide != Slide.NONE) {
                slideMenu = true;
            }
            this.slideView = slideView;
            return this;
        }
        public LoadView createActivity() {
            return new LoadView(this, WindowType.ACTIVITY);
        }


        public LoadView createFragment() {
            return new LoadView(this, WindowType.FRAGMENT);
        }

        public Builder requestWindowFeature(int featureNoTitle) {
            mContext.requestWindowFeature(featureNoTitle);
            return this;
        }
        public Builder setRequestedOrientation(int requestedOrientation) {
            mContext.requestWindowFeature(requestedOrientation);
            return this;
        }

        public Builder initBottomBarLayout(View view) {
            return this;
        }
    }
    ;
}
