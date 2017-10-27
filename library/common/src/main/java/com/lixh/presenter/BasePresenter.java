package com.lixh.presenter;

import android.os.Bundle;

import com.lixh.base.BaseActivity;
import com.lixh.base.BaseFragment;
import com.lixh.rxhttp.RxHelper;
import com.lixh.rxlife.LifeEvent;
import com.lixh.utils.LoadingTip;
import com.lixh.utils.UIntent;
import com.lixh.view.UToolBar;

import rx.subjects.BehaviorSubject;


/**
 * des:基类presenter
 * Created by xsf
 * on 2016.07.11:55
 */
public abstract class BasePresenter{
    public UToolBar toolbar;
    public LoadingTip tip;
    public UIntent intent;
    public RxHelper rxHelper;
    public BaseActivity activity;
    private BaseFragment fragment;
    public abstract void onCreate(Bundle savedInstanceState);

    /**
     * 对外部开放 以实现外面调用
     *
     * @param <T>
     * @return
     */
    public <T> T getPresenter() {
        return (T) this;
    }

    /**
     * 获取Activity 实例
     *
     * @param <T>
     * @return
     */
    public <T> T getActivity() {
        return (T) activity.getActivity();
    }

    /**
     * 获取fragment 实例
     *
     * @param <T>
     * @return
     */
    public <T> T getFragment() {
        return (T) fragment.getFragment();
    }


    public <T extends BaseActivity> void init(T activity, Bundle savedInstanceState, BehaviorSubject<LifeEvent> lifecycleSubject) {
        this.activity = activity;
        tip = activity.tip;
        intent = new UIntent(activity);
        rxHelper = RxHelper.build(activity).setCaChe(activity.getClass().getName()).bindLifeCycle(lifecycleSubject);
        onCreate(savedInstanceState);
    }

    public <T extends BaseFragment> void init(T fragment, Bundle savedInstanceState, BehaviorSubject<LifeEvent> lifecycleSubject) {
        this.fragment = fragment;
        tip = fragment.tip;
        intent = new UIntent(fragment.getActivity());
        rxHelper = RxHelper.build(fragment.getActivity()).setCaChe(fragment.getClass().getName()).bindLifeCycle(lifecycleSubject);
        onCreate(savedInstanceState);
    }

    public static enum Theme {
        DAY, NIGHT
    }

    /**
     * 设置主题
     */
    private void initTheme(Theme theme) {

    }


    public void setToolBar(UToolBar toolbar) {
        this.toolbar = toolbar;
    }

    public void onDestroy() {
        rxHelper.clearSubject();
    }


}
