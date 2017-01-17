package com.lixh.rxhttp;

/**
 * Created by LIXH on 2016/11/3.
 * email lixhVip9@163.com
 * des
 */

public interface ICallBackResult<E> {
    void onLoad();

    void onSuccess(E t);

    void onFail(String msg);

    void onLoadFinish();
}
