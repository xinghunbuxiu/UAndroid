package com.lixh.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.Window;

import com.lixh.R;
import com.lixh.app.AppManager;
import com.lixh.bean.Message;
import com.lixh.presenter.BasePresenter;
import com.lixh.rxhttp.Observable;
import com.lixh.rxhttp.Observer;
import com.lixh.rxlife.LifeEvent;
import com.lixh.setting.AppConfig;
import com.lixh.swipeback.SwipeBackActivityBase;
import com.lixh.swipeback.Utils;
import com.lixh.swipeback.app.SwipeBackActivityHelper;
import com.lixh.swipeback.app.SwipeBackLayout;
import com.lixh.utils.Exit;
import com.lixh.utils.LoadingTip;
import com.lixh.utils.SharedPreferencesUtil;
import com.lixh.utils.StatusBarCompat;
import com.lixh.utils.TUtil;
import com.lixh.utils.UIntent;
import com.lixh.utils.UToast;
import com.lixh.view.LoadView;
import com.lixh.view.UToolBar;

import butterknife.ButterKnife;
import rx.subjects.BehaviorSubject;

/**
 * 基类Activity
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements SwipeBackActivityBase, Observer<Message> {
    public T mPresenter; //当前类需要的操作类
    public LoadingTip tip;
    public LoadView layout;
    public final BehaviorSubject<LifeEvent> lifecycleSubject = BehaviorSubject.create();
    public Exit exit = new Exit();// 双击退出 封装
    private boolean mNowMode;
    private SwipeBackLayout mSwipeBackLayout;
    private SwipeBackActivityHelper mHelper;
    protected abstract void init(Bundle savedInstanceState);
    public BaseActivity() {
        mPresenter = TUtil.getT(this, 0);
        // 把actvity放到application栈中管理
        AppManager.getAppManager().addActivity(this);
    }

    public UToolBar toolBar;
    public UIntent intent;
    public abstract int getLayoutId();
    public abstract boolean initTitle(UToolBar toolBar);
    public <T> T getActivity() {
        return (T) this;
    }

    public T getPresenter() {
        return (T) mPresenter.getPresenter();
    }

    /**
     * 是否有标题栏
     *
     * @return
     */
    public boolean hasToolBar() {
        return true;
    }

    /**
     * 是否允许左划结束
     *
     * @return
     */
    public boolean enableSwipeBack() {
        return true;
    }
    /**
     * @return 是否双j击退出
     */
    public boolean isDoubleExit() {
        return false;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }



    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lifecycleSubject.onNext(LifeEvent.CREATE);
        super.onCreate(savedInstanceState);
        doBeforeSetContentView();
        layout = new LoadView.Builder(this).setBottomLayout(getLayoutId()).setToolBar(hasToolBar()).build();
        layout.addObserver(this);
        setContentView(layout.getRootView());
        ButterKnife.bind(this);
        intent = new UIntent(this);
        tip = layout.getEmptyView();
        initSwipe(enableSwipeBack());
        initTitleBar();
        init(savedInstanceState);
        if (mPresenter != null) {
            mPresenter.init(this, savedInstanceState, lifecycleSubject);
        }


    }

    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    /**
     * 是否滑动结束
     */
    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    public void initSwipe(boolean enable) {
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        //侧滑
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        setSwipeBackEnable(enable);
    }

    private void initTitleBar() {
        toolBar = layout.getToolbar();
        if (toolBar != null) {
            toolBar.setDisplayShowTitleEnabled(false);
            toolBar.setDisplayHomeAsUpEnabled(!initTitle(toolBar));
            if (mPresenter != null) {
                mPresenter.setToolBar(toolBar);
        }
        }
    }

    /**
     * 设置layout前配置
     */
    private void doBeforeSetContentView() {
        mNowMode = SharedPreferencesUtil.getInstance().getBoolean(AppConfig.ISNIGHT);
        // 无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 默认着色状态栏
        SetStatusBarColor();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SharedPreferencesUtil.getInstance().getBoolean(AppConfig.ISNIGHT, false) != mNowMode) {
            if (SharedPreferencesUtil.getInstance().getBoolean(AppConfig.ISNIGHT, false)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            recreate();
        }
    }

    /**
     * 着色状态栏（4.4以上系统有效）
     */
    protected void SetStatusBarColor() {
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.main_color));
    }

    /**
     * 着色状态栏（4.4以上系统有效）
     */
    protected void SetStatusBarColor(int color) {
        StatusBarCompat.setStatusBarColor(this, color);
    }

    /**
     * 沉浸状态栏（4.4以上系统有效）
     */
    protected void SetTranslanteBar() {
        StatusBarCompat.translucentStatusBar(this);
    }


    @Override
    protected void onPause() {
        lifecycleSubject.onNext(LifeEvent.PAUSE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        lifecycleSubject.onNext(LifeEvent.STOP);
        super.onStop();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mPresenter != null)
            mPresenter.onDestroy();
        lifecycleSubject.onNext(LifeEvent.DESTROY);
        AppManager.getAppManager().finishActivity(this);
    }


    @Override
    public void onBackPressed() {
        if (isDoubleExit()) {
            if (exit.isExit()) {
                AppManager.getAppManager().NotifyExitApp();
            } else {
                exit.doExitInOneSecond();
                UToast.showShort("再按一次退出");
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void update(Observable o, Message arg) {

    }
}
