package com.lixh.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * @author Administrator 更新应用版本API
 */
@SuppressWarnings("unused")
public enum UpdateFactory {
    INSTANCE;
    private NotificationManager mNotificationManager;
    private static final int NOTIFY_ID = 0;
    private volatile UpdateHelper updateHelper;
    private UpdateHelper.Builder builder;
    private UpdateService.DownloadBinder binder;
    boolean isBind = false;
    Context context;
    String apkUrl = UFile.getCacheDir();
    String downApk_url;

    public interface ICallbackResult {
        void OnBackResult(Object result);
    }

    public interface OnResultListener {
        String post();

        void parse(String msg);
    }

    OnResultListener resultListener = new OnResultListener() {

        @Override
        public String post() {
            return "";
        }

        @Override
        public void parse(String msg) {
            //解析
        }
    };


    private ICallbackResult callback = new ICallbackResult() {
        public void OnBackResult(Object result) {
            if ("finish".equals(result)) {
                return;
            }
        }

    };
    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (UpdateService.DownloadBinder) service;
            System.out.println("服务启动!!!");
            // 开始下载
            isBind = true;
            binder.addCallback(callback);
            binder.start(downApk_url, apkUrl);

        }
    };

    UpdateFactory() {

    }

    public void init(Activity context) {
        this.context = context;
        LocalAppInfo.init(context);
        apkUrl = LocalAppInfo.getLocalAppInfo().getPackageName() + ".apk";
        SharedPreferencesUtil.getInstance().init(context, "Update", Context.MODE_PRIVATE);
        mNotificationManager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        builder = new UpdateHelper.Builder(context);
        builder.setDelay(1000 * 60 * 60 * 24);
        builder.setServiceConnection(conn);
        builder.setOnResultListener(resultListener);
    }

    public UpdateHelper build() {
        updateHelper = builder.build();
        return updateHelper;
    }
}
