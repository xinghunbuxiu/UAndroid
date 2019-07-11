package com.lixh.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.IntDef;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.lixh.R;
import com.lixh.swipeback.SwipeBackActivityBase;
import com.lixh.swipeback.Utils;
import com.lixh.swipeback.app.SwipeBackActivityHelper;
import com.lixh.swipeback.app.SwipeBackLayout;
import com.lixh.utils.LoadingTip;
import com.lixh.utils.UIntent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Created by LIXH on 2016/11/5.
 * email lixhVip9@163.com
 * des
 */

public class LoadView implements SwipeBackActivityBase {
    public static final String TAG = "LoadView";
    private static final int UBLayout = 0x999999;
    public static Activity mContext;
    private RelativeLayout RootView;
    private LoadingTip tip;
    private final LoadView.Builder builder;
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
    public LoadView(LoadView.Builder builder, int windowType) {
        this.builder = builder;
        this.windowType = windowType;
        this.commonView(builder);

    }

    public UToolBar getToolbar() {
        return this.toolbar;
    }

    public UIntent getIntent() {
        return intent;
    }

    //返回布局View
    public RelativeLayout getRootView() {
        return this.RootView;
    }

    //通用布局
    public void commonView(LoadView.Builder builder) {
        this.RootView = (RelativeLayout) builder.inflate(builder.contentTop ? R.layout.status_toolbar_layout : R.layout.toolbar_layout);
        this.initToolBar();
        this.initBottomView();
        this.initSwipe(builder.swipeBack);
        this.initSlideMenu(builder.slideMenu);
        this.intent = new UIntent(LoadView.mContext);
    }


    /**
     * 底部导航条
     * 支持自定义
     *
     * @param hasBottomBar
     * @param root
     */
    private void initBottomBar(boolean hasBottomBar, RelativeLayout.LayoutParams root) {
        if (hasBottomBar) {
            this.bottomLayoutHelper = new BottomLayoutHelper(this.builder);
            this.RootView.addView(this.bottomLayoutHelper.getLayout(), root);
            this.bottomLayoutHelper.initBottomBarLayout();
        }
    }

    private void initSlideMenu(boolean slideMenu) {
        if (slideMenu) {
            this.slideMenu1 = new SlideMenu(LoadView.mContext);
            this.slideMenu1.setId(R.id.slideMenu);
            this.slideMenu1.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    private void initBottomView() {
        RelativeLayout.LayoutParams root = this.getLayoutParams();
        root.addRule(RelativeLayout.BELOW, R.id.toolbar);
        this.tip = this.getEmptyView();
        if (this.builder.mBottomLayout > 0) {
            this.RootView.addView(this.builder.inflate(this.builder.mBottomLayout), root);
        } else {
            this.initBottomBar(this.builder.hasBottomBar, root);
        }
        this.RootView.addView(this.tip, root);
        if (this.windowType == LoadView.WindowType.ACTIVITY) {
            this.createActivity();
        }
        if (this.toolbar != null) {
            this.toolbar.bringToFront();
        }
    }

    public LoadView createActivity() {
        LoadView.mContext.findViewById(Window.ID_ANDROID_CONTENT);
        Window window = LoadView.mContext.getWindow();
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        decorView.setId(Window.ID_ANDROID_CONTENT);
        decorView.removeAllViews();
        decorView.addView(this.RootView);
        return this;
    }

    public RelativeLayout.LayoutParams getLayoutParams() {
        return new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void initToolBar() {
        this.view_Stub = this.RootView.findViewById(R.id.title_stub);
        if (this.builder.hasToolbar) {
            this.toolbar = (UToolBar) this.view_Stub.inflate();
            if (this.builder.contentTop) {
                this.toolbar.setHasBar();
            }
        } else {
            this.RootView.removeView(this.view_Stub);
        }
    }

    public static int sp2px(float spValue) {
        float fontScale = LoadView.mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public void setContentView(View view) {
        this.setContentView(view, true);
    }

    public void setContentView(View view, boolean belowTitle) {
        RelativeLayout.LayoutParams root = this.getLayoutParams();
        if (belowTitle) {
            root.addRule(RelativeLayout.BELOW, R.id.toolbar);
        }
        this.RootView.addView(view, root);
        if (this.toolbar != null) {
            this.toolbar.bringToFront();
        }
    }

    public void addView(View view, RelativeLayout.LayoutParams lp, boolean belowTitle) {
        if (belowTitle) {
            lp.addRule(RelativeLayout.BELOW, R.id.toolbar);
        }
        this.RootView.addView(view, lp);
    }

    public void initSwipe(boolean enable) {
        if (enable) {
            this.mHelper = new SwipeBackActivityHelper(LoadView.mContext);
            this.mHelper.onActivityCreate();
            //侧滑
            this.mSwipeBackLayout = this.getSwipeBackLayout();
            this.mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
            this.setSwipeBackEnable(enable);
        }
    }

    public LoadingTip getEmptyView() {
        if (this.tip == null) {
            this.tip = new LoadingTip(LoadView.mContext);
        }
        return this.tip;
    }


    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        if (this.mHelper != null) {
            return this.mHelper.getSwipeBackLayout();
        } else return null;
    }


    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(LoadView.mContext);
        SwipeBackLayout swipeBackLayout = this.getSwipeBackLayout();
        if (swipeBackLayout != null) {
            swipeBackLayout.scrollToFinishActivity();
        }
    }

    public void onPostCreate() {
        if (this.mHelper != null) {
            this.mHelper.onPostCreate();
        }
        if (this.slideMenu1 != null) {
            this.slideMenu1.attachToActivity(LoadView.mContext);
            int slide = this.builder.slide;
            BaseSlideView view = this.builder.slideView;
            if (view != null && slide != SlideMenu.Slide.NONE) {
                this.slideMenu1.addSlideView(view, slide);
            }

        }
    }

    /**
     * 是否滑动结束
     */
    @Override
    public void setSwipeBackEnable(boolean enable) {
        this.getSwipeBackLayout().setEnableGesture(enable);
    }

    @IntDef({
            LoadView.WindowType.ACTIVITY,
            LoadView.WindowType.FRAGMENT,
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
        @SlideMenu.Slide
        public int slide;
        public BaseSlideView slideView;
        public LayoutInflater layoutInflater;
        public FragmentManager supportFragmentManager;
        public BottomNavigationItem items[];
        public Fragment fragments[];
        public BottomNavigationBar.OnTabSelectedListener tabSelectedListener;

        public Builder(FragmentActivity context) {
            LoadView.mContext = context;
            this.supportFragmentManager = context.getSupportFragmentManager();
            this.fragments = null;
            this.items = null;
            this.mBottomLayout = -1;
            this.bottomBarLayout = -1;
            this.hasToolbar = true;
            this.slideView = null;
            this.contentTop = true;
            this.slideMenu = false;
            this.swipeBack = true;
            this.hasBottomBar = false;
            this.tabSelectedListener = null;
            this.slide = SlideMenu.Slide.NONE;
        }

        /**
         * 添加底部
         *
         * @param items
         * @return
         */
        public LoadView.Builder addItem(BottomNavigationItem... items) {
            this.items = items;
            return this;
        }

        /**
         * 添加底部
         *
         * @param fragments
         * @return
         */
        public LoadView.Builder addFragment(Fragment... fragments) {
            this.fragments = fragments;
            return this;
        }

        public View inflate(int layoutResID) {
            if (this.layoutInflater == null) {
                this.layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            return this.layoutInflater.inflate(layoutResID, null);
        }

        public void setOnTabSelectedListener(BottomNavigationBar.OnTabSelectedListener listener) {
            tabSelectedListener = listener;
        }

        public LoadView.Builder setSlideMenu(@SlideMenu.Slide int slide, BaseSlideView slideView) {
            this.slide = slide;
            if (slide != SlideMenu.Slide.NONE) {
                this.slideMenu = true;
            }
            this.slideView = slideView;
            return this;
        }

        public LoadView createActivity() {
            return new LoadView(this, LoadView.WindowType.ACTIVITY);
        }


        public LoadView createFragment() {
            return new LoadView(this, LoadView.WindowType.FRAGMENT);
        }

        public LoadView.Builder requestWindowFeature(int featureNoTitle) {
            LoadView.mContext.requestWindowFeature(featureNoTitle);
            return this;
        }

        public LoadView.Builder setRequestedOrientation(int requestedOrientation) {
            LoadView.mContext.requestWindowFeature(requestedOrientation);
            return this;
        }

        public LoadView.Builder initBottomBarLayout(View view) {
            return this;
        }
    }

}
