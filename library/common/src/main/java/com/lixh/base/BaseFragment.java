package com.lixh.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.lixh.bean.Message;
import com.lixh.presenter.BasePresenter;
import com.lixh.rxhttp.Observable;
import com.lixh.rxhttp.Observer;
import com.lixh.rxlife.LifeEvent;
import com.lixh.utils.LoadingTip;
import com.lixh.utils.Global;
import com.lixh.utils.TUtil;
import com.lixh.utils.UIntent;
import com.lixh.view.IBase;
import com.lixh.view.LoadView;
import com.lixh.view.UToolBar;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.subjects.BehaviorSubject;

public abstract class BaseFragment<T extends BasePresenter> extends Fragment implements Observer<Message>, IBase {
    public T mPresenter; //当前类需要的操作类
    public FragmentActivity activity;
    public LoadingTip tip;
    public LoadView layout;
    public UToolBar toolBar;
    public View mContentView;
    public UIntent intent;
    Unbinder unbinder;

    protected void init(Bundle savedInstanceState) {

    }

    public BaseFragment() {
        mPresenter = TUtil.getT (this, 0);
        Global.get( ).addObserver (this);
    }

    protected final BehaviorSubject<LifeEvent> lifecycleSubject = BehaviorSubject.create ( );


    public void initLoad(LoadView.Builder builder) {

    }

    public boolean isContentTop() {
        return true;
    }

    @Override
    public void onAttach(Context context) {
        lifecycleSubject.onNext (LifeEvent.ATTACH);
        super.onAttach (context);
        activity = (FragmentActivity) context;
        layout = new LoadView.Builder (activity) {
            {
                mBottomLayout = getLayoutId ( );
                initLoad (this);
            }
        }.createFragment ( );
        intent = layout.getIntent ( );
        intent.with (getArguments ( ));
        tip = layout.getEmptyView ( );
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        lifecycleSubject.onNext (LifeEvent.CREATE);
        super.onCreate (savedInstanceState);

    }

    public boolean isBack() {
        return false;
    }

    public abstract void initTitle(UToolBar toolBar);

    public abstract int getLayoutId();

    private void initTitleBar() {
        toolBar = layout.getToolbar ( );
        if (toolBar != null) {
            toolBar.setDisplayShowTitleEnabled (false);
            toolBar.setDisplayHomeAsUpEnabled (isBack ( ));
            initTitle (toolBar);
            if (mPresenter != null) {
                mPresenter.setToolBar (layout.getToolbar ( ));
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        lifecycleSubject.onNext (LifeEvent.CREATE_VIEW);
        super.onViewCreated (view, savedInstanceState);


    }

    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility (View.GONE);
                }
            }
        }
    }

    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility (View.VISIBLE);
                }
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = layout.getRootView ( );
            unbinder = ButterKnife.bind (this, mContentView);
            initTitleBar ( );
            if (mPresenter != null) {
                mPresenter.bind (this).init (savedInstanceState, lifecycleSubject);
            }
            init (savedInstanceState);

        }
        return mContentView;
    }

    protected <VT extends View> VT $(@IdRes int id) {
        return (VT) mContentView.findViewById (id);
    }

    @Override
    public void onStart() {
        lifecycleSubject.onNext (LifeEvent.START);
        super.onStart ( );
    }

    @Override
    public void onResume() {
        lifecycleSubject.onNext (LifeEvent.RESUME);
        super.onResume ( );

    }


    @Override
    public void onPause() {
        lifecycleSubject.onNext (LifeEvent.PAUSE);
        super.onPause ( );

    }

    @Override
    public void onStop() {
        lifecycleSubject.onNext (LifeEvent.STOP);
        super.onStop ( );

    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext (LifeEvent.DESTORY);
        super.onDestroy ( );


    }

    @Override
    public void onDestroyView() {
        lifecycleSubject.onNext (LifeEvent.DESTORY_VIEW);
        super.onDestroyView ( );

    }

    @Override
    public void onDetach() {
        lifecycleSubject.onNext (LifeEvent.DETACH);
        super.onDetach ( );
        if (mPresenter != null)
            mPresenter.onDestroy ( );

    }

    @Override
    public void update(Observable o, Message arg) {

    }

    @Override
    public void setData(Object bean) {

    }
}