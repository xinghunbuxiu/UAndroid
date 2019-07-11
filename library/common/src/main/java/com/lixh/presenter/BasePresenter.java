package com.lixh.presenter;

import android.app.Activity;
import android.os.Bundle;

import com.lixh.base.BaseActivity;
import com.lixh.base.BaseFragment;
import com.lixh.rxhttp.RxHelper;
import com.lixh.rxlife.LifeEvent;
import com.lixh.utils.LoadingTip;
import com.lixh.utils.UIntent;
import com.lixh.view.IBase;
import com.lixh.view.UToolBar;

import io.reactivex.subjects.BehaviorSubject;


/**
 * des:基类presenter
 * Created by xsf
 * on 2016.07.11:55
 */
public abstract class BasePresenter<T extends IBase> {
    public UToolBar toolbar;
    public LoadingTip tip;
    public UIntent intent;
    public RxHelper rxHelper;
    public T view;
    public Activity activity;


    public abstract void onCreate(Bundle savedInstanceState);

    public BasePresenter bind(T layout) {
        this.view = layout;
        return this;
    }

    public void init(Bundle savedInstanceState, BehaviorSubject<LifeEvent> lifecycleSubject) {
        BaseFragment fragment;
        if (view instanceof BaseActivity) {
            activity = (BaseActivity) view;
            tip = ((BaseActivity) view).tip;
        } else {
            fragment = (BaseFragment) view;
            tip = fragment.tip;
            activity = fragment.getActivity();
        }
        intent = new UIntent(activity);
        rxHelper = RxHelper.build(activity).setCaChe(view.getClass().getName()).bindLifeCycle(lifecycleSubject);
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
