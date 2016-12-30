package com.lixh.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.lixh.R;
import com.lixh.app.BaseApplication;
import com.lixh.rxhttp.view.ProgressCancelListener;


/**
 * description:弹窗浮动加载进度条
 * Created by xsf
 * on 2016.07.17:22
 */
public class Alert {
    public interface IShow {
        View convert(ViewHolder holder);
    }

    private static Toast toast;
    /**
     * 加载数据对话框
     */
    private static Dialog dialog;
    private static PopupWindow mPopView;

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(CharSequence message) {
        initToast(message, Toast.LENGTH_SHORT).show();
    }


    /**
     * 短时间显示Toast
     *
     * @param strResId
     */
    public static void showShort(int strResId) {
        initToast(BaseApplication.getAppContext().getResources().getText(strResId), Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    public static void showLong(CharSequence message) {
        initToast(message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param strResId
     */
    public static void showLong(int strResId) {
        initToast(BaseApplication.getAppContext().getResources().getText(strResId), Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration
     */
    public static void show(CharSequence message, int duration) {
        initToast(message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param strResId
     * @param duration
     */
    public static void show(Context context, int strResId, int duration) {
        initToast(context.getResources().getText(strResId), duration).show();
    }


    /**
     * @param layoutId 布局文件
     * @param dialog
     * @return
     */
    private static PopupWindow showPop(int layoutId, IShow dialog, int anim) {
        if (mPopView == null) {
            mPopView = new PopupWindow();
        }
        View view = LayoutInflater.from(BaseApplication.getAppContext()).inflate(layoutId, null);
        mPopView.setContentView(view);
        mPopView.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopView.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        mPopView.setFocusable(true);
        // 点击外面的控件也可以使得PopUpWindow dimiss
        mPopView.setOutsideTouchable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        mPopView.setAnimationStyle(anim);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);// 0xb0000000
        // 设置SelectPicPopupWindow弹出窗体的背景
        mPopView.setBackgroundDrawable(dw);// 半透明颜色
        return mPopView;
    }

    public static Dialog showDialog(int layoutId, ProgressCancelListener progressCancelListener) {
        return showDialog(layoutId, null, progressCancelListener, 0);
    }

    public static Dialog showDialog(int layoutId) {
        return showDialog(layoutId, null, null, 0);
    }

    public static Dialog showDialog(int layoutId, IShow show, final ProgressCancelListener progressCancelListener, int anim) {
        if (dialog == null) {
            dialog = new Dialog(BaseApplication.getAppContext(), R.style.CustomProgressDialog);
        }
        View view = LayoutInflater.from(BaseApplication.getAppContext()).inflate(layoutId, null);
        ViewHolder holder = new ViewHolder(view);
        dialog.setContentView(show == null ? view : show.convert(holder), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (progressCancelListener != null) {
                    progressCancelListener.onCancelProgress();
                }
            }
        });
        dialog.show();
        return dialog;
    }

    private static Toast initToast(CharSequence message, int duration) {
        if (toast == null) {
            toast = Toast.makeText(BaseApplication.getAppContext(), message, duration);
        } else {
            toast.setText(message);
            toast.setDuration(duration);
        }
        return toast;
    }

    /**
     * 自定义的toast
     *
     * @param layoutId
     * @param t
     * @param duration
     * @return
     */
    private static Toast initToastWithImage(int layoutId, IShow t, int duration) {
        if (toast == null) {
            toast = new Toast(BaseApplication.getAppContext());
        }
        View view = LayoutInflater.from(BaseApplication.getAppContext()).inflate(layoutId, null);
        ViewHolder holder = new ViewHolder(view);
        toast.setView(t.convert(holder));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return toast;
    }

    /**
     * 关闭加载对话框
     */
    public static void dismissPop() {
        if (mPopView != null) {
            mPopView.dismiss();
        }
    }

    /**
     * 关闭加载对话框
     */
    public static void cancelDialog() {
        if (dialog != null) {
            dialog.cancel();
        }
    }


}
