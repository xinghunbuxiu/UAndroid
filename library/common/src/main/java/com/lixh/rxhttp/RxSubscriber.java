package com.lixh.rxhttp;

import android.content.Context;

import com.lixh.BuildConfig;
import com.lixh.R;
import com.lixh.app.BaseApplication;
import com.lixh.rxhttp.exception.ApiException;
import com.lixh.rxhttp.view.ProgressCancelListener;
import com.lixh.utils.Alert;
import com.lixh.utils.UNetWork;

import rx.Subscriber;

public abstract class RxSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {

    private Context mContext;
    private String msg;
    private boolean showDialog=true;
    /**
     * 是否显示浮动dialog
     */
    public void showDialog() {
        this.showDialog= true;
    }
    public void hideDialog() {
        this.showDialog= true;
    }

    public RxSubscriber(Context context, String msg,boolean showDialog) {
        this.mContext = context;
        this.msg = msg;
        this.showDialog=showDialog;
    }
    public RxSubscriber(Context context) {
        this(context, BaseApplication.getAppContext().getString(R.string.loading),true);
    }
    public RxSubscriber(Context context,boolean showDialog) {
        this(context, BaseApplication.getAppContext().getString(R.string.loading),showDialog);
    }

    @Override
    public void onCompleted() {
        _onFinish();
        if (showDialog)
            Alert.cancelDialog();


    }
    @Override
    public void onStart() {
        super.onStart();
        if (showDialog) {
            try {
                showProgressDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示Dialog
     */
    public void showProgressDialog() {
        if (showDialog) {
            try {
                Alert.showDialog(R.layout.alert_proress);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }


    @Override
    public void onNext(T t) {
        _onNext(t);
    }
    @Override
    public void onError(Throwable e) {
        if (showDialog)
            Alert.cancelDialog();
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
        //网络
        if (!UNetWork.isNetConnected(BaseApplication.getAppContext())) {
            _onError(BaseApplication.getAppContext().getString(R.string.no_net));
        }
        //服务器
        else if (e instanceof ApiException) {
            _onError(e.getMessage());
        }
        //其它
        else {
            _onError(BaseApplication.getAppContext().getString(R.string.net_error));
        }

        _onFinish();
    }
    protected abstract void _onNext(T t);

    protected abstract void _onError(String message);

    protected abstract void _onFinish();

    @Override
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }
}
