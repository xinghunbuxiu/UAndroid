package com.lixh.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * APPLICATION
 */
public abstract class BaseApplication extends Application {

    private static BaseApplication baseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
        init();
    }

    public static BaseApplication getAppContext() {
        return baseApplication;
    }

    public static Resources getAppResources() {
        return baseApplication.getResources();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    public abstract void init();

    /**
     * 数据库初始化
     * https://realm.io/docs/java/latest/
     */
    public void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.deleteRealm(config);
        Realm.setDefaultConfiguration(config);
    }

    /**
     * 分包
     *
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

}
