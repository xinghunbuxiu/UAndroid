package com.lixh.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.Window;

import com.lixh.app.AppManager;
import com.lixh.bean.Message;
import com.lixh.presenter.BasePresenter;
import com.lixh.rxhttp.Observable;
import com.lixh.rxhttp.Observer;
import com.lixh.rxlife.LifeEvent;
import com.lixh.setting.AppConfig;
import com.lixh.utils.Exit;
import com.lixh.utils.LoadingTip;
import com.lixh.utils.SharedPreferencesUtil;
import com.lixh.utils.SystemBarTintManager;
import com.lixh.utils.TUtil;
import com.lixh.utils.UIntent;
import com.lixh.utils.UToast;
import com.lixh.view.ILayout;
import com.lixh.view.LoadView;
import com.lixh.view.LoadView.Builder;
import com.lixh.view.UToolBar;

import butterknife.ButterKnife;
import rx.subjects.BehaviorSubject;

/**
 * 基类Activity
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements Observer<Message>, ILayout {
    public T mPresenter; //当前类需要的操作类
    public LoadingTip tip;
    public LoadView layout;
    public final BehaviorSubject<LifeEvent> lifecycleSubject = BehaviorSubject.create();
    public Exit exit = new Exit();// 双击退出 封装
    private boolean mNowMode;
    public BaseActivity() {
        mPresenter = TUtil.getT(this, 0);
        mNowMode = SharedPreferencesUtil.getInstance().getBoolean(AppConfig.ISNIGHT);
        // 把actvity放到application栈中管理
        AppManager.getAppManager().addActivity(this);
    }
    public UToolBar toolBar;
    public UIntent intent;
    public <T> T getActivity() {
        return (T) this;
    }

    public T getPresenter() {
        return (T) mPresenter.getPresenter();
    }

    public void initLoad(Builder builder) {

    }
    @Override
    public void init(Bundle savedInstanceState) {
    }
    @Override
    public boolean isShowBack() {
        return true;
    }

    @Override
    public boolean isDoubleExit() {
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lifecycleSubject.onNext(LifeEvent.CREATE);
        super.onCreate(savedInstanceState);
        layout = new LoadView.Builder(this) {
            {
                mBottomLayout = getLayoutId();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置竖屏
                requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题
                initLoad(this);
            }
        }.createActivity();
        layout.addObserver(this);
        ButterKnife.bind(this);
        intent = layout.getIntent();
        tip = layout.getEmptyView();
        initTitleBar();
        init(savedInstanceState);
        SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
        systemBarTintManager.setTranslucentStatus(this, true);
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
        layout.onPostCreate();
    }

    private void initTitleBar() {
        toolBar = layout.getToolbar();
        if (toolBar != null) {
            toolBar.setDisplayShowTitleEnabled(false);
            toolBar.setDisplayHomeAsUpEnabled(isShowBack());
            initTitle(toolBar);
            if (mPresenter != null) {
                mPresenter.setToolBar(toolBar);
        }
        }
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
