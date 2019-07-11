package com.lixh.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.flyco.dialog.widget.NormalListDialog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by LIXH on 2017/6/5.
 * email lixhVip9@163.com
 * des
 */

public class Alert {

    private static BaseAnimatorSet bas_in = new BounceTopEnter();
    private static BaseAnimatorSet bas_out = new SlideBottomExit();
    private static Queue<Dialog> dialogQueue = new LinkedList<>();
    private static Dialog currentDialog = null;

    private static void showDialog(Dialog dialog) {
        if (dialog != null) {
            dialogQueue.offer(dialog);
        }
        if (currentDialog == null) {
            currentDialog = dialogQueue.poll();
            if (currentDialog != null) {
                currentDialog.show();
                currentDialog.setOnDismissListener(dialog1 -> {
                    currentDialog = null;
                    showDialog(null);
                });
            }
        }
    }

    /**
     * 普通对话框
     *
     * @param context
     * @param message
     */
    public static void displayAlertDialog(Context context, String message) {
        displayAlertDialog(context, "温馨提示", message, (OnBtnClickL) null);
    }

    /**
     * 普通对话框
     *
     * @param context
     * @param title
     * @param message
     */
    public static void displayAlertDialog(Context context, String title, String message) {
        displayAlertDialog(context, title, message, (OnBtnClickL) null);
    }

    /**
     * 普通对话框
     *
     * @param context
     * @param title
     * @param message
     */
    public static void displayAlertDialog(Context context, String title, String message, OnBtnClickL... onBtnClickL) {
        NormalDialog dialog = new NormalDialog(context);
        dialog.content(message)//
                .style(NormalDialog.STYLE_ONE)//
                .title(title)
                .btnNum(1)
                .showAnim(bas_in)//
                .dismissAnim(bas_out);//
        if (onBtnClickL != null) {
            dialog.setOnBtnClickL(onBtnClickL);
        } else {
            dialog.setOnBtnClickL((OnBtnClickL) () -> dismiss());
        }
        showDialog(dialog);
    }

    public static void displayAlertDialog(Context context, String warn, String message, String okStr, String cancelStr, OnBtnClickL okOnClickListener, OnBtnClickL cancelOnClickListener) {
        NormalDialog dialog = new NormalDialog(context);
        dialog.content(message)//
                .style(NormalDialog.STYLE_TWO)//
                .titleTextSize(23)//
                .btnNum(3)
                .btnText(okStr, cancelStr)
                .showAnim(bas_in)//
                .dismissAnim(bas_out);//

        dialog.setOnBtnClickL(okOnClickListener,
                cancelOnClickListener);
        showDialog(dialog);
    }

    public static void displayAlertSelectedDialog(Context context, String[] stringItems, final OnOperItemClickL onOperItemClickL) {
        ActionSheetDialog dialog = new ActionSheetDialog(context, stringItems, null);
        dialog.isTitleShow(false)
                .setOnOperItemClickL((parent, view, position, id) -> {
                    dismiss();
                    onOperItemClickL.onOperItemClick(parent, view, position, id);
                });
        showDialog(dialog);
    }

    public static void displayAlertSingledDialog(Context context, String[] stringItems, final OnOperItemClickL onOperItemClickL) {
        ArrayList<DialogMenuItem> testItems = new ArrayList<>();

        final NormalListDialog dialog = new NormalListDialog(context, stringItems);
        dialog.title("请选择")//
                .layoutAnimation(null);
        dialog.setOnOperItemClickL((parent, view, position, id) -> {
            dialog.dismiss();
            if (onOperItemClickL != null)
                onOperItemClickL.onOperItemClick(parent, view, position, id);
        });
        showDialog(dialog);
    }

    public static void displayLoading(Context context, int layoutId, DialogInterface.OnDismissListener dismissListener) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(layoutId, false)
                .dismissListener(dismissListener).build();
        showDialog(dialog);
    }

    public static void dismiss() {
        if (currentDialog != null) {
            currentDialog.dismiss();
        }
    }
}
