package com.lixh.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

public class LocalAppInfo {
    private final static String TAG = "LocalAppInfo";
    private String appName;
    private String packageName;
    private String versionName;
    private String imei;
    private int versionCode;
    private static LocalAppInfo localAppInfo;

    public static void init(Context mContext) {
        localAppInfo = new LocalAppInfo();
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                localAppInfo.setAppName(pm.getApplicationLabel(appInfo).toString());
                localAppInfo.setPackageName(info.packageName);
                localAppInfo.setVersionCode(info.versionCode);
                localAppInfo.setVersionName(info.versionName);
            }
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            localAppInfo.setIMei(tm.getDeviceId());
            ULog.d(TAG, "about: LocalAppInfo = " + localAppInfo.toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static LocalAppInfo getLocalAppInfo() {
        if (null == localAppInfo) {
            throw new RuntimeException("LocalAppInfo is not init.");
        }
        return localAppInfo;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getIMei() {
        return imei;
    }

    public void setIMei(String imei) {
        this.imei = imei;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    @Override
    public String toString() {
        return "LocalAppInfo{" +
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", imei='" + imei + '\'' +
                ", versionCode=" + versionCode +
                '}';
    }
}