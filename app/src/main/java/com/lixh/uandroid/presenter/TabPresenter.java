package com.lixh.uandroid.presenter;


import android.os.Bundle;

import com.lixh.presenter.BasePresenter;
import com.lixh.uandroid.api.Api;
import com.lixh.uandroid.api.HostType;
import com.lixh.uandroid.model.MainModel;

;


/**
 * Created by LIXH on 2016/12/21.
 * email lixhVip9@163.com
 * des
 */
public class TabPresenter extends BasePresenter {
    Result<MainModel> m = new Result<MainModel>() {

        @Override
        public void onSuccess(MainModel t) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        rxHelper.createSubscriber(Api.getDefault(HostType.BASE_URL).login("us", "dd"), m);
    }


}
