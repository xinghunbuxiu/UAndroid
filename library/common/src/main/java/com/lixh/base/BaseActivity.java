package com.lixh.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.lixh.R;
import com.lixh.app.AppManager;
import com.lixh.daynightmodeutils.ChangeModeController;
import com.lixh.presenter.BasePresenter;
import com.lixh.rxlife.LifeEvent;
import com.lixh.utils.Alert;
import com.lixh.utils.Exit;
import com.lixh.utils.LoadingTip;
import com.lixh.utils.StatusBarCompat;
import com.lixh.utils.TUtil;
import com.lixh.utils.UIntent;
import com.lixh.view.LoadView;
import com.lixh.view.UToolBar;

import butterknife.ButterKnife;
import rx.subjects.BehaviorSubject;

/**
 * 基类Activity
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity {
    public T mPresenter; //当前类需要的操作类
    public LoadingTip tip;
    public LoadView layout;
    public final BehaviorSubject<LifeEvent> lifecycleSubject = BehaviorSubject.create();
    public Exit exit = new Exit();// 双击退出 封装

    protected abstract void init(Bundle savedInstanceState);
    public BaseActivity() {
        bind();
    }

    public UToolBar toolBar;
    public UIntent intent;
    public abstract int getLayoutId();
    public abstract boolean initTitle(UToolBar toolBar);
    public void bind() {
        mPresenter = TUtil.getT(this, 0);
        intent = new UIntent(this);
        // 把actvity放到application栈中管理
        AppManager.getAppManager().addActivity(this);
    }

    public <T> T getActivity() {
        return (T) this;
    }

    public T getPresenter() {
        return (T) mPresenter.getPresenter();
    }

    public boolean hasToolBar() {
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lifecycleSubject.onNext(LifeEvent.CREATE);
        super.onCreate(savedInstanceState);
        doBeforeSetContentView();
        layout = new LoadView.Builder(this).setBottomView(getLayoutId()).setToolBar(hasToolBar()).build();
        tip = layout.getEmptyView();
        setContentView(layout.getRootView());
        ButterKnife.bind(this);
        initTitleBar();
        init(savedInstanceState);
        if (mPresenter != null) {
            mPresenter.init(this, savedInstanceState, lifecycleSubject);
        }


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



    //自己新添加的
    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
    }
    /**
     * 设置layout前配置
     */
    private void doBeforeSetContentView() {
        ChangeModeController.setTheme(this, R.style.DayTheme, R.style.NightTheme);
        // 无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 默认着色状态栏
        SetStatusBarColor();

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

    /**
     * @return 是否双j击退出
     */
    public boolean isDoubleExit() {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (isDoubleExit()) {
            if (exit.isExit()) {
                AppManager.getAppManager().NotifyExitApp();
            } else {
                exit.doExitInOneSecond();
                Alert.showShort("再按一次退出");
            }
        } else {
            super.onBackPressed();
        }
    }
}
