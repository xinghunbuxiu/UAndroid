package com.lixh.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;

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
                currentDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        currentDialog = null;
                        showDialog(null);
                    }
                });
            }
        }
    }

    public static void displayAlertDialog(Activity context, String warn, String message, String okStr, String cancelStr, OnBtnClickL okOnClickListener, OnBtnClickL cancelOnClickListener) {
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

    public static void displayAlertSelectedDialog(Activity context, String[] stringItems, final OnOperItemClickL onOperItemClickL) {
        ActionSheetDialog dialog = new ActionSheetDialog(context, stringItems, null);
        dialog.isTitleShow(false)
                .setOnOperItemClickL(new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dismiss();
                        onOperItemClickL.onOperItemClick(parent, view, position, id);
                    }
                });
        showDialog(dialog);
    }

    public static void displayAlertSingledDialog(Activity context, String[] stringItems, final OnOperItemClickL onOperItemClickL) {
        ArrayList<DialogMenuItem> testItems = new ArrayList<>();

        final NormalListDialog dialog = new NormalListDialog(context, stringItems);
        dialog.title("请选择")//
                .layoutAnimation(null);
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
            }
        });
        showDialog(dialog);
    }

    public static void displayLoading(Activity context, int layoutId, DialogInterface.OnDismissListener dismissListener) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(layoutId,false)
                .dismissListener(dismissListener)
                .show();
    }

    public static void dismiss() {
        if (currentDialog != null) {
            currentDialog.dismiss();
        }
    }
}
