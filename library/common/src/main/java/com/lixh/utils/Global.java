package com.lixh.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.lixh.rxhttp.Observable;

import androidx.core.app.ActivityCompat;

public class Global extends Observable {
    private final static String TAG = "LocalAppInfo";
    private String appName;
    private String packageName;
    private String versionName;
    private String imei;
    private int versionCode;
    String labelRes;
    private static Global global;

    public static void init(Context mContext) {
        global = new Global();
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                global.setAppName(pm.getApplicationLabel(appInfo).toString());
                global.setPackageName(info.packageName);
                global.setVersionCode(info.versionCode);
                global.setVersionName(info.versionName);
                global.setLabelRes(mContext.getResources().getString(info.applicationInfo.labelRes));
            }
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            global.setIMei(tm.getDeviceId());
            ULog.d(TAG, "about: LocalAppInfo = " + global.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Global get() {
        if (null == global) {
            throw new RuntimeException("LocalAppInfo is not init.");
        }
        return global;
    }

    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }


    public String getLabelRes() {
        return this.labelRes;
    }

    public void setLabelRes(String labelRes) {
        this.labelRes = labelRes;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getSdkINT(Context context) {
        try {
            return Build.VERSION.SDK_INT;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
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