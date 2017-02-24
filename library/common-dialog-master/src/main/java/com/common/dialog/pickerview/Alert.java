package com.common.dialog.pickerview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.common.dialog.R;

import java.util.Arrays;


/**
 * description:弹窗浮动加载进度条
 * Created by xsf
 * on 2016.07.17:22
 */
public class Alert {
    static Context mContext;
    Builder builder;
    public interface IShow {
        View convert(ViewHolder holder);
    }
    public Alert(Builder builder) {

        this.builder = builder;
    }

    public enum ListType {
        SINGLE, MULTIPLE
    }

    public static class Builder {
        String message;
        String okBtnStr;
        String cancelStr;

        public String getCancelStr() {
            return cancelStr;
        }

        public void setCancelStr(String cancelStr) {
            this.cancelStr = cancelStr;
        }

        public ChoiceModelItemClickListener getChoiceModelItemClickListener() {
            return choiceModelItemClickListener;
        }

        public void setChoiceModelItemClickListener(ChoiceModelItemClickListener choiceModelItemClickListener) {
            this.choiceModelItemClickListener = choiceModelItemClickListener;
        }

        public int getLayoutId() {
            return layoutId;
        }

        public void setLayoutId(int layoutId) {
            this.layoutId = layoutId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getOkBtnStr() {
            return okBtnStr;
        }

        public void setOkBtnStr(String okBtnStr) {
            this.okBtnStr = okBtnStr;
        }

        public View.OnClickListener getOnClickListener() {
            return onClickListener;
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        View.OnClickListener onClickListener;
        ChoiceModelItemClickListener choiceModelItemClickListener;
        int layoutId;

        public Builder(Context context) {
            mContext = context;
        }

        public Alert Build() {
            return new Alert(this);
        }
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
        initToast(mContext.getResources().getText(strResId), Toast.LENGTH_SHORT).show();
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
        initToast(mContext.getResources().getText(strResId), Toast.LENGTH_LONG).show();
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
        View view = LayoutInflater.from(mContext).inflate(layoutId, null);
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

    public static Dialog showDialog(int layoutId, AlertCancelListener alertCancelListener) {
        return showDialog(layoutId, null, alertCancelListener, 0);
    }

    /**
     * 显示提示对话框（填充Message、设置ok按钮点击事件、隐藏cancel按钮）
     *
     * @param title
     * @param msg
     * @param okBtnStr
     * @param cancelBtnStr
     * @param onClickListener
     * @param cancelBtnOnClickListener null 默认取消
     */
    public static void displayAlertDialog(final String title, final String msg, final String okBtnStr, final String cancelBtnStr, final View.OnClickListener onClickListener, final View.OnClickListener cancelBtnOnClickListener) {
        showDialog(R.layout.dialog_interactive, new IShow() {
            @Override
            public View convert(ViewHolder holder) {
                holder.setText(R.id.titleView, title);
                holder.setText(R.id.messageView, msg);
                holder.setText(R.id.okBtn, okBtnStr);
                holder.setText(R.id.okBtn, cancelBtnStr);
                holder.setOnClickListener(R.id.okBtn, onClickListener);
                if (cancelBtnOnClickListener == null) {
                    holder.setOnClickListener(R.id.cancelBtn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelDialog();
                        }
                    });
                } else {
                    holder.setOnClickListener(R.id.cancelBtn, onClickListener);
                }

                return holder.getView();
            }
        }, null, 0);
    }

    /**
     * @param title
     * @param msg
     * @param okBtnStr
     * @param cancelBtnStr
     * @param onClickListener
     * @param cancelBtnOnClickListener
     */
    public static void displayEditDialog(final String title, final String msg, final String okBtnStr, final String cancelBtnStr, final View.OnClickListener onClickListener, final View.OnClickListener cancelBtnOnClickListener) {
        showDialog(R.layout.dialog_edittext, new IShow() {
            @Override
            public View convert(ViewHolder holder) {
                holder.setText(R.id.titleView, title);
                holder.setEditText(R.id.messageView, msg);
                holder.setText(R.id.okBtn, okBtnStr);
                holder.setText(R.id.okBtn, cancelBtnStr);
                holder.setOnClickListener(R.id.okBtn, onClickListener);
                if (cancelBtnOnClickListener == null) {
                    holder.setOnClickListener(R.id.cancelBtn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelDialog();
                        }
                    });
                } else {
                    holder.setOnClickListener(R.id.cancelBtn, onClickListener);
                }

                return holder.getView();
            }
        }, null, 0);
    }


    /**
     * 显示列表对话框
     *
     * @param title   // 默认为“提示”
     * @param message []
     *                //返回点击ID操作
     */
    public static void displayListDialog(final String title, final String[] message, final String okBtnStr, final String cancelBtnStr, final int position[],
                                         final ChoiceModelItemClickListener modelItemClickListener, final ListType type) {// 点击
        final SparseArray<SparseArray<String>> item = new SparseArray<>();
        final SparseArray<String> array = new SparseArray(message.length);
        showDialog(R.layout.dialog_singlelist, new IShow() {
            @Override
            public View convert(ViewHolder holder) {
                ListView list = holder.$(R.id.dlist);
                holder.setText(R.id.titleView, title);
                holder.setText(R.id.okBtn, okBtnStr);
                holder.setText(R.id.cancelBtn, cancelBtnStr);
                if (type == ListType.SINGLE) {
                    holder.setVisible(R.id.btn_pane, View.GONE);
                }
                //多选时确认按钮
                holder.setOnClickListener(R.id.okBtn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modelItemClickListener.onItemClick(item);
                    }
                });
                holder.setOnClickListener(R.id.cancelBtn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelDialog();
                    }
                });
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int which, long id) {
                        if (item.get(which) == null) {
                            array.put(which, message[which]);
                            item.put(which, array);
                        } else {
                            item.removeAt(which);
                        }
                        if (type == ListType.SINGLE) {
                            cancelDialog();
                            modelItemClickListener.onItemClick(item);
                        }
                    }
                });
                list.setAdapter(new listItemAdapter(message, position));
                return holder.getView();
            }

        }, null, 0);

    }

    static class listItemAdapter extends BaseAdapter<String> {
        int index[];

        /**
         * @param list
         */
        public listItemAdapter(String list[], int position[]) {
            super(mContext, Arrays.asList(list));
            this.index = position;
        }

        //
        @Override
        public void onBindViewHolder(BaseAdapter.ViewHolder viewHolder, int position, String t) {
            TextView tv = viewHolder.getView(R.id.tv_content);
            TextView box = viewHolder.getView(R.id.cb_box);
            for (int p : index) {
                box.setSelected(p == position);
            }
            tv.setText(t);
        }

        //
        @Override
        public View onCreateView(ViewGroup parent) {
            return inflate(R.layout.dialog_singlelist_item);
        }

    }

    public interface ChoiceModelItemClickListener {
        /**
         * @param item
         */
        void onItemClick(SparseArray<SparseArray<String>> item);
    }

    ;

    public static Dialog showDialog(int layoutId) {
        return showDialog(layoutId, null, null, 0);
    }

    public static Dialog showDialog(int layoutId, IShow show, final AlertCancelListener alertCancelListener, int anim) {
        if (dialog == null) {
            dialog = new Dialog(mContext, R.style.CustomProgressDialog);
        }
        View view = LayoutInflater.from(mContext).inflate(layoutId, null);
        ViewHolder holder = new ViewHolder(view);
        dialog.setContentView(show == null ? view : show.convert(holder), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (alertCancelListener != null) {
                    alertCancelListener.onCancelProgress();
                }
            }
        });
        dialog.show();
        return dialog;
    }

    private static Toast initToast(CharSequence message, int duration) {
        if (toast == null) {
            toast = Toast.makeText(mContext, message, duration);
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
            toast = new Toast(mContext);
        }
        View view = LayoutInflater.from(mContext).inflate(layoutId, null);
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
