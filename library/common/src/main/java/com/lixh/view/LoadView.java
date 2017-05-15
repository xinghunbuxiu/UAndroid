package com.lixh.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.lixh.R;
import com.lixh.rxhttp.Observable;
import com.lixh.swipeback.SwipeBackActivityBase;
import com.lixh.swipeback.Utils;
import com.lixh.swipeback.app.SwipeBackActivityHelper;
import com.lixh.swipeback.app.SwipeBackLayout;
import com.lixh.utils.LoadingTip;
import com.lixh.view.SlideMenu.Slide;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.lixh.view.SlideMenu.Slide.LEFT;


/**
 * Created by LIXH on 2016/11/5.
 * email lixhVip9@163.com
 * des
 */

public class LoadView extends Observable implements SwipeBackActivityBase {
    public static final String TAG = "LoadView";
    public static Activity mContext;
    RelativeLayout RootView;
    LoadingTip tip;
    Builder builder;
    UToolBar toolbar;
    ViewStub view_Stub;
    int windowType;
    private SwipeBackLayout mSwipeBackLayout;
    private SwipeBackActivityHelper mHelper;
    protected SwipeBackLayout layout;
    SlideMenu slideMenu1;
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
        RootView = (RelativeLayout) inflate(builder.isContentTop() ? R.layout.status_toolbar_layout : R.layout.toolbar_layout);
        initToolBar();
        initBottomView();
        initSwipe(builder.isSwipeBack());
        initSlideMenu(builder.isSlideMenu());
    }

    private void initSlideMenu(boolean slideMenu) {
        if (slideMenu) {
            slideMenu1 = new SlideMenu(mContext);
            slideMenu1.setId(View.NO_ID);
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
        if (builder.getBottomLayout() > 0) {
            RootView.addView(inflate(builder.getBottomLayout()), root);
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
        RootView.setPadding(0,0,0,0);
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
            if (builder.isContentTop()) {
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


    protected View inflate(int layoutResID) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layoutResID, null);
        return view;
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
            Slide slide = builder.getSlide();
            View view = builder.getSlideView();
            if (view != null) {
                slideMenu1.setView(view, slide);

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

        private int mBottomLayout;
        private boolean hasToolbar;
        private View mBottomView;
        private boolean contentTop;
        private boolean swipeBack;
        private boolean slideMenu;
        private Slide slide;
        private View slideView;
        public boolean isSwipeBack() {
            return swipeBack;
        }

        public Builder(Activity context) {
            mContext = context;
            mBottomLayout = -1;
            mBottomView = null;
            hasToolbar=true;
            slideView = null;
            contentTop = false;
            slideMenu = false;
            slide = LEFT;
        }

        public View getSlideView() {
            if (slideView == null) {
                throw new RuntimeException("slideView is not null");
            }
            return slideView;
        }

        public Slide getSlide() {
            return slide;
        }

        public boolean isSlideMenu() {
            return slideMenu;
        }

        public Builder setSlideMenu(View slideView, Slide slide) {
            if (slideView != null) {
                slideMenu = true;
            }
            this.slide = slide;
            this.slideView = slideView;
            return this;
        }
        public int getBottomLayout() {
            return mBottomLayout;
        }

        public Builder setBottomLayout(int mBottomLayout, boolean contentTop) {
            this.mBottomLayout = mBottomLayout;
            this.contentTop = contentTop;
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

        public boolean isContentTop() {
            return contentTop;
        }

        public Builder setSwipeBack(boolean swipeBack) {
            this.swipeBack = swipeBack;
            return this;
        }


    }

    ;
}
