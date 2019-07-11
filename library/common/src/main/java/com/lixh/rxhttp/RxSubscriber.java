package com.lixh.rxhttp;

import android.content.Context;
import android.content.DialogInterface;

import com.lixh.BuildConfig;
import com.lixh.R;
import com.lixh.app.BaseApplication;
import com.lixh.rxhttp.exception.ApiException;
import com.lixh.utils.Alert;
import com.lixh.utils.UNetWork;
import com.lixh.utils.UToast;

import io.reactivex.observers.DisposableObserver;

public abstract class RxSubscriber<T> extends DisposableObserver<T> {

    private Context mContext;
    private String msg;
    private boolean showDialog = true;

    /**
     * 是否显示浮动dialog
     */
    public void showDialog() {
        this.showDialog = true;
    }

    public void hideDialog() {
        this.showDialog = true;
    }

    public RxSubscriber(Context context, String msg, boolean showDialog) {
        this.mContext = context;
        this.msg = msg;
        this.showDialog = showDialog;
    }

    public RxSubscriber(Context context) {
        this(context, BaseApplication.getAppContext().getString(R.string.loading), true);
    }

    public RxSubscriber(Context context, boolean showDialog) {
        this(context, BaseApplication.getAppContext().getString(R.string.loading), showDialog);
    }

    @Override
    public void onComplete() {
        if (showDialog)
            Alert.dismiss();

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
                Alert.displayLoading(mContext, R.layout.alert_proress, dialog -> cancelProgress());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onNext(T t) {
        try {
            _onNext(t);
        } catch (Exception e) {
            _onError(e.getMessage());
        }
    }

    @Override
    public void onError(Throwable e) {
        if (showDialog)
            Alert.dismiss();
        if (BuildConfig.LOG_DEBUG) {
            e.printStackTrace();
        }
        //网络
        if (!UNetWork.isNetworkAvailable(BaseApplication.getAppContext())) {
            _onError(BaseApplication.getAppContext().getString(R.string.no_net));
        }
        //服务器
        else if (e instanceof ApiException) {
            ApiException e1 = (ApiException) e;
            _onError(e1.getCode(), e1.getMessage());
        }
        //其它
        else {
            _onError(BaseApplication.getAppContext().getString(R.string.net_error));
        }
    }

    protected abstract void _onNext(T t);

    protected void _onError(String message) {
    }

    protected void _onError(int code, String message) {
        _onError(message);
        UToast.showShort(message);
    }

    public void cancelProgress() {
        if (!this.isDisposed()) {
            this.dispose();
        }
    }
}
