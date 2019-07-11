package com.lixh.utils;

import android.Manifest.permission;
import android.app.AlertDialog.Builder;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build.VERSION;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.yanzhenjie.permission.AndPermission;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static android.content.Context.APP_OPS_SERVICE;


public class PermissionUtils {
    public static final String[] CAMERA = new String[]{permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE, permission.CAMERA};
    public static final String[] EXTERNAL_GROUP = new String[]{permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE};
    public static final String[] INSTALL = new String[]{"android.permission.REQUEST_INSTALL_PACKAGES"};
    public static final String[] PERMISSIONS = new String[]{permission.READ_PHONE_STATE, permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE};

    public static final String TAG = "Permission";


    public interface PermissionCallback {
        void onRequestSuccess();
    }


    public interface RequestPermission {
        void onRequestPermissionFailure(List<String> list);

        void onRequestPermissionSuccess();
    }

    public static void requestPermission(Context context, final RequestPermission requestPermission, String... strArr) {
        AndPermission.with(context).runtime().permission(strArr)
                .onGranted(data -> requestPermission.onRequestPermissionFailure(data))
                .onDenied(data -> requestPermission.onRequestPermissionSuccess())
                .start();
    }

    public static void launchCamera(Context context, RequestPermission requestPermission) {
        requestPermission(context, requestPermission, CAMERA);
    }

    public static void externalStorage(Context context, RequestPermission requestPermission) {
        requestPermission(context, requestPermission, EXTERNAL_GROUP);
    }

    public static void sendSms(Context context, RequestPermission requestPermission) {
        requestPermission(context, requestPermission, permission.SEND_SMS);
    }

    public static void callPhone(Context context, RequestPermission requestPermission) {
        requestPermission(context, requestPermission, permission.CALL_PHONE);
    }

    public static void readPhonestate(Context context, RequestPermission requestPermission) {
        requestPermission(context, requestPermission, permission.READ_PHONE_STATE);
    }

    public static boolean hasPermissions(Context context, String... strArr) {
        for (String str : strArr) {
            if (hasPermission(context, str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasPermission(Context context, String str) {
        return ContextCompat.checkSelfPermission(context, str) == 0;
    }

    @RequiresApi(api = 19)
    public static boolean isNotificationEnable(Context context) {
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);

            Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
            int value = (int) opPostNotificationValue.get(Integer.class);
            return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void checkNotificationPermission(Context context) {
        if (VERSION.SDK_INT >= 19 && !isNotificationEnable(context)) {
            new Builder(context).setTitle("提醒").setMessage("没有通知权限更新进度可能无法在状态栏显示。\n请前往\"设置\"-\"权限管理\"-打开通知权限。").setPositiveButton("前往设置", (dialog, which) -> UIntent.goSetActivity(context)).setNegativeButton("下次再说", (dialog, which) -> dialog.dismiss()).show();
        }
    }

    public static void showPmsExternalStorage(Context context) {
        new Builder(context).setTitle("提醒").setMessage("权限缺失可能导致此功能无法正常使用。\n请前往\"设置\"-\"权限管理\"-请求外部存储的权限。")
                .setPositiveButton("设置", (dialog, which) -> UIntent.goSetActivity(context)).setNegativeButton("退出", (dialog, which) -> dialog.dismiss()).show();
    }

    public static void requestPermission(final Context context, String[] strArr, final PermissionCallback permissionCallback) {
        requestPermission(context, new RequestPermission() {
            public void onRequestPermissionSuccess() {
                if (permissionCallback != null) {
                    permissionCallback.onRequestSuccess();
                }
            }

            public void onRequestPermissionFailure(List<String> list) {
                showPermissionSettingDialog(context);
            }
        }, strArr);
    }

    private static void showPermissionSettingDialog(final Context context) {
        new Builder(context).setTitle("提醒").setMessage("权限缺失可能导致某些功能无法正常使用。\n请前往设置-权限管理-打开所需权限。")
                .setNegativeButton("以后再说", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("立即设置", (dialog, which) -> {
                    UIntent.goSetActivity(context);
                    dialog.dismiss();
                }).create().show();
    }
}