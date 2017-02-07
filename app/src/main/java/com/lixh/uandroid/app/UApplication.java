package com.lixh.uandroid.app;

import com.lixh.app.BaseApplication;
import com.lixh.uandroid.BuildConfig;
import com.lixh.utils.ULog;

/**
 * APPLICATION
 */
public class UApplication extends BaseApplication {

    @Override
    public void init() {
        //初始化logger
        ULog.logInit(BuildConfig.DEBUG);
    }
}
