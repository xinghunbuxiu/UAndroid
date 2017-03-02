package com.lixh.utils;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.FileProvider;

import com.lixh.BuildConfig;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;

import rx.functions.Action1;


public class UpdateService extends Service {
    DownloadManager dm;
    long enqueue;
    private UpdateFactory.ICallbackResult callback;
    private DownloadBinder binder;
    private Context mContext = this;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new DownloadBinder();
        stopForeground(true);// 这个不确定是否有作用
    }

    BroadcastReceiver receiver;

    public class DownloadBinder extends Binder {
        public void start(final String downloadUrl, final String saveFileName) {
            try {
                receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        install(saveFileName);
                        //销毁当前的Service
                        stopSelf();
                    }
                };
                registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                //下载需要写SD卡权限, targetSdkVersion>=23 需要动态申请权限
                RxPermissions.getInstance(mContext)
                        // 申请权限
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean granted) {
                                if (granted) {
                                    //请求成功
                                    startDownload(downloadUrl, saveFileName);
                                } else {
                                    // 请求失败回收当前服务
                                    UToast.showShort("没有SD卡储存权限,下载失败");
                                    intentWebDown(downloadUrl);
                                    stopSelf();
                                }
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void addCallback(UpdateFactory.ICallbackResult callback) {
            UpdateService.this.callback = callback;
        }
    }


    private void startDownload(String downloadUrl, String saveFileName) {
        //获得系统下载器
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        //设置下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        //设置下载文件的类型
        request.setMimeType("application/vnd.android.package-archive");
        //设置下载存放的文件夹和文件名字
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, saveFileName);
        //设置下载时或者下载完成时，通知栏是否显示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(LocalAppInfo.getLocalAppInfo().getAppName());
        //执行下载，并返回任务唯一id
        enqueue = dm.enqueue(request);
    }

    public void intentWebDown(String downloadUrl) {
        Uri uri = Uri.parse(downloadUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 通过隐式意图调用系统安装程序安装APK
     */
    public void install(String saveFileName) {
        try {
            File file = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    , saveFileName);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                Uri apkUri =
                        FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".udeskfileprovider", file);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
            }
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
