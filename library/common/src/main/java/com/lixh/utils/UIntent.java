package com.lixh.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by LIXH on 2016/11/8.
 * email lixhVip9@163.com
 * des
 */

public class UIntent {
    public Activity mContext;
    Intent mIntent;

    public UIntent(Activity mContext) {
        this.mContext = mContext;
    }


    public void toSetActivity() {
        Intent localIntent = new Intent();
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS")
                    .setData(Uri.fromParts("package", mContext.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW).
                    setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
                    .putExtra("com.android.settings.ApplicationPkgName", mContext.getPackageName());
        }
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(localIntent);
    }

    /**
     * 跳转到父Activity
     */
    public static void backToParent(Activity activity) {
        Intent upIntent = NavUtils.getParentActivityIntent(activity);
        if (upIntent == null) {
            activity.finish();
            return;
        }
        if (NavUtils.shouldUpRecreateTask(activity, upIntent)) {
            TaskStackBuilder.create(activity)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();
        } else {
            NavUtils.navigateUpTo(activity, upIntent);
        }
    }

    /**
     * 通过Class跳转界面
     **/
    public void toActivity(Class<?> cls) {
        mContext.startActivity(new Intent().setClass(mContext, cls));
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void toActivityForResult(Class<?> cls, int requestCode) {
        mContext.startActivityForResult(new Intent().setClass(mContext, cls), requestCode);
    }

}
