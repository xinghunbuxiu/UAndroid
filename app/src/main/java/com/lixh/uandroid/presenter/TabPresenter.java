package com.lixh.uandroid.presenter;


import android.os.Bundle;

import com.lixh.presenter.BasePresenter;
import com.lixh.rxhttp.RxSubscriber;
import com.lixh.uandroid.api.Api;
import com.lixh.uandroid.api.HostType;
import com.lixh.uandroid.model.MainModel;
import com.lixh.uandroid.ui.TabsActivity;

;


/**
 * Created by LIXH on 2016/12/21.
 * email lixhVip9@163.com
 * des
 */
public class TabPresenter extends BasePresenter {
    TabsActivity tabsActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        tabsActivity = getActivity();
        rxHelper.createSubscriber(Api.getDefault(HostType.BASE_URL).login("us", "dd"), new RxSubscriber<MainModel>(activity, true) {
            @Override
            protected void _onNext(MainModel mainModel) {

            }

            @Override
            protected void _onError(String message) {

            }
        });

    }
}
