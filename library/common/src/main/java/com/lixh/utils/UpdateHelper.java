package com.lixh.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;

import com.flyco.dialog.listener.OnBtnClickL;
import com.lixh.R;

/**
 * @author Administrator 更新应用版本API
 */
@SuppressWarnings("unused")
public class UpdateHelper {
    static Activity context;
    BroadcastReceiver broadcastReceiver;
    boolean isRegisterBR;
    Builder builder;
    String versionName;
    Resources resources;

    private UpdateHelper(Builder builder) {
        this.builder = builder;
        resources = context.getResources();
    }


    public void check() {
        if (UNetWork.isNetworkAvailable(context)) {
            if (null != broadcastReceiver && isRegisterBR) {
                context.unregisterReceiver(broadcastReceiver);
                isRegisterBR = false;
            }
            checkUpdate();
        } else {
            if (!isRegisterBR) {
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        checkUpdate();
                    }
                };
                IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                context.registerReceiver(broadcastReceiver, intentFilter);
                isRegisterBR = true;
            }
        }
    }

    // 检查更新
    public void checkUpdate() {

        Boolean isFirst = SharedPreferencesUtil.getInstance().getBoolean("firstLogin", true);
        long lastUpdateTime = SharedPreferencesUtil.getInstance().getLong("lastUpdateTime",
                System.currentTimeMillis());
        if (!isFirst) {
            execCheckUpdate(lastUpdateTime);
        } else {
            SharedPreferencesUtil.getInstance().putLong("lastUpdateTime", lastUpdateTime);
            SharedPreferencesUtil.getInstance().putBoolean("firstLogin", false);
        }
    }

    /**
     * @param lastUpdateTime
     */
    private void execCheckUpdate(long lastUpdateTime) {
        /* 检查上次更新时间到现在是否满足一天 */
        if ((lastUpdateTime + builder.getDelay()) < System
                .currentTimeMillis()) {
            /* 保存当前时间给下次更新使用 */
            lastUpdateTime = System.currentTimeMillis();
            SharedPreferencesUtil.getInstance().putLong("lastUpdateTime", lastUpdateTime);
            /* 开始检查更新 */
            checkUpdateInfo();
        }
    }

    /**
     * 下载完成之后更新UI
     */
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Alert.dismiss();
            String msgStr = msg.obj.toString();
            switch (msg.what) {
                case 1:
                    builder.getOnResultListener().parse(msgStr);
                    break;
                case 2:
                    UToast.showShort(R.string.not_connect_networks);
                    break;
            }
        }
    };

    // 外部接口让主Activity调用
    public void checkUpdateInfo() {// 此线程负责在后台检查是否有新版本
        new Thread() {
            public void run() {
                Message msg = mHandler.obtainMessage();
                try {
                    if (UNetWork.isNetworkAvailable(context)) {// 判断网络是否可用
                        //请求
                        msg.obj = builder.getOnResultListener().post();
                        msg.what = 1;
                    } else {
                        // 网络异常时
                        msg.obj = resources.getString(R.string.not_connect_networks);
                        msg.what = 2;
                    }
                } catch (Exception e) {
                    msg.obj = resources.getString(R.string.not_connect_networks);
                    msg.what = 2;
                }
                msg.sendToTarget();
            }
        }.start();
    }

    public Runnable showUpdate = new Runnable() {
        public void run() {
            // 更新
            Alert.displayAlertDialog(context, "发现新版本", "最新版本:" + "\n"
                    + versionName, "立即更新", "以后再说", new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    if (UFile.isExistsSdcard()) {// 判断sdcard是否存在
                        Intent updateIntent = new Intent(context,
                                UpdateService.class);
                        context.startService(updateIntent);
                        context.bindService(updateIntent, builder.getServiceConnection(),
                                Context.BIND_AUTO_CREATE);

                        UToast.showLong(R.string.backstage_down);
                    } else {
                        UToast.showLong(R.string.plug_sdcard_tip);
                    }
                }
            }, null);
        }
    };

    public static class Builder {
        private static final String TAG = Builder.class.getSimpleName();
        private String upgradeUrl;                      //upgrade check remote-interface.
        private boolean isAutoStartInstall = true;
        private boolean isQuietDownload = false;        //whether quiet download when the update is detected.
        private boolean isCheckPackageName = true;      //whether check the package name.
        private boolean isAboutChecking = false;        //whether is "about" check upgrade.
        private long delay = 0;                         //millisecond. whether delay check upgrade.

        public ServiceConnection getServiceConnection() {
            return serviceConnection;
        }

        private ServiceConnection serviceConnection;

        public UpdateFactory.OnResultListener getOnResultListener() {
            return onResultListener;
        }

        private UpdateFactory.OnResultListener onResultListener;


        public long getDelay() {
            return delay;
        }

        public Builder(Activity mContext) {
            context = mContext;
        }

        private Builder setAutoStartInstall(boolean autoStartInstall) {
            this.isAutoStartInstall = autoStartInstall;
            return this;
        }

        /***
         * 更新时间
         *
         * @param delay
         * @return
         */
        public Builder setDelay(long delay) {
            this.delay = delay;
            return this;
        }

        public UpdateHelper build() {
            return new UpdateHelper(this);
        }

        public void setServiceConnection(ServiceConnection serviceConnection) {
            this.serviceConnection = serviceConnection;
        }

        public void setOnResultListener(UpdateFactory.OnResultListener onResultListener) {
            this.onResultListener = onResultListener;
        }
    }

}
