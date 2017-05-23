package com.common.dialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.common.dialog.ImpAlert.ChoiceModelItemClickListener;
import com.common.dialog.ImpAlert.DialogType;
import com.common.dialog.ImpAlert.IBindView;
import com.common.dialog.ImpAlert.ICreate;
import com.common.dialog.ImpAlert.OnOKDialogClickListener;
import com.common.dialog.ImpAlert.OnOptionsSelectListener;
import com.common.dialog.ImpAlert.OnSelectedItemListener;
import com.common.dialog.ImpAlert.Type;
import com.common.dialog.effects.BaseEffects;
import com.common.dialog.pickerview.BaseAdapter;
import com.common.dialog.pickerview.ViewHolder;
import com.common.dialog.pickerview.lib.ScreenInfo;
import com.common.dialog.pickerview.lib.WheelOptions;
import com.common.dialog.pickerview.lib.WheelTime;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static com.common.dialog.ImpAlert.AlertCancelListener;
import static com.common.dialog.ImpAlert.EffectsType;
import static com.common.dialog.ImpAlert.OnTimeSelectListener;
import static com.common.dialog.ImpAlert.TimeType;


/**
 * description:弹窗浮动加载进度条
 * Created by xsf
 * on 2016.07.17:22
 */
public class Alert {
    static Context mContext;

    /**
     * 加载数据对话框
     */
    private static Dialog dialog;
    private static PopupWindow mPopView;
    long mDuration = 0L;
    private Builder builder;
    WheelTime wheelTime;
    ScreenInfo screenInfo;

    public static void showCustomDialog(Activity context, @LayoutRes final int layoutId, final AlertCancelListener listener) {
        showDialog(context, new ICreate() {
            @Override
            public void bind(Builder mBuilder, int type) {
                mBuilder.layoutId = layoutId;
                mBuilder.alertCancelListener = listener;
            }
        }, DialogType.Custom);
    }

    /**
     * @param context
     * @param title
     * @param msg
     * @param onOkClickListener
     */
    public static void displayAlertDialog(Activity context, final String title, final String msg, final View.OnClickListener onOkClickListener) {
        displayAlertDialog(context, title, msg, null, null, onOkClickListener, null);
    }

    /**
     * @param context
     * @param title
     * @param msg
     * @param onOkClickListener
     */
    public static void displayEditDialog(Activity context, final String hint, final String title, final String msg, final OnOKDialogClickListener onOkClickListener) {
        displayEditDialog(context, hint, title, msg, null, null, onOkClickListener, null);
    }

    /**
     * @param context
     * @param title
     * @param msg
     * @param okStr
     * @param cancelStr
     * @param onOkClickListener
     * @param onCancelClickListener
     */
    public static void displayAlertDialog(Activity context, final String title, final String msg, final String okStr, final String cancelStr, final View.OnClickListener onOkClickListener, final View.OnClickListener onCancelClickListener) {
        showDialog(context, new ICreate() {
            @Override
            public void bind(Builder mBuilder, int type) {
                if (title != null) {
                    mBuilder.title = title;
                }
                if (okStr != null) {
                    mBuilder.okBtnStr = okStr;
                }
                if (cancelStr != null) {
                    mBuilder.cancelStr = cancelStr;
                }
                mBuilder.message = msg;
                mBuilder.onCancelClickLister = onCancelClickListener;
                mBuilder.onOkClickListener = onOkClickListener;
            }
        }, DialogType.Warn);
    }

    /**
     * @param context
     * @param title
     * @param msg
     * @param okStr
     * @param cancelStr
     * @param onOkClickListener
     * @param onCancelClickListener
     */
    public static void displayEditDialog(Activity context, final String hint, final String title, final String msg, final String okStr, final String cancelStr, final OnOKDialogClickListener onOkClickListener, final View.OnClickListener onCancelClickListener) {
        showDialog(context, new ICreate() {
            @Override
            public void bind(Builder mBuilder, int type) {
                if (title != null) {
                    mBuilder.title = title;
                }
                if (okStr != null) {
                    mBuilder.okBtnStr = okStr;
                }
                if (cancelStr != null) {
                    mBuilder.cancelStr = cancelStr;
                }
                if (hint != null) {
                    mBuilder.hint = hint;
                }
                mBuilder.message = msg;
                mBuilder.onCancelClickLister = onCancelClickListener;
                mBuilder.onOKDialogClickListener = onOkClickListener;
            }
        }, DialogType.Edit);
    }

    public static void displaySelectDialog(Activity context, final OnSelectedItemListener itemListener, final CharSequence char1, final CharSequence char2, final CharSequence char3) {

        showDialog(context, new ICreate() {
            @Override
            public void bind(Builder mBuilder, int type) {
                mBuilder.setLabels(char1, char2, char3);
                mBuilder.onSelectedItemListener = itemListener;
            }
        }, DialogType.Edit);
    }

    public static void showDialog(Activity context, final ICreate create, final @DialogType int Type) {
        Alert.Builder mBuilder = new Builder(context) {
            {
                if (create != null) {
                    create.bind(this, Type);
                    Build(Type);
                }
            }
        };
    }


    /**
     * 关闭加载对话框
     */
    public static void dismiss() {
        if (mPopView != null) {
            mPopView.dismiss();
        }
        if (dialog != null) {
            dialog.cancel();
        }
    }
    private class MenuItem {
        private OnSelectedItemListener onSelectedItemListener;
        private int position;
        private String text;

        public OnSelectedItemListener getOnSelectedItemListener() {
            return onSelectedItemListener;
        }


        public int getPosition() {
            return position;
        }

        public String getText() {
            return text;
        }

        public void MenuItem(int position, String text, OnSelectedItemListener onSelectedItemListener) {
            this.position = position;
            this.text = text;
            this.onSelectedItemListener = onSelectedItemListener;

        }
    }




    public BaseEffects getAnimator(int key) {
        BaseEffects bEffects = null;
        try {
            bEffects = ImpAlert.animClass.get(key).newInstance();
        } catch (ClassCastException e) {
            throw new Error("Can not init animatorClazz instance");
        } catch (InstantiationException e) {
            throw new Error("Can not init animatorClazz instance");
        } catch (IllegalAccessException e) {
            throw new Error("Can not init animatorClazz instance");
        }
        return bEffects;
    }

    public void showAsDropDown(View anchor) {
        if (mPopView != null)
            mPopView.showAsDropDown(anchor);
    }

    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (mPopView != null)
            mPopView.showAsDropDown(anchor, xoff, yoff);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        if (mPopView != null)
            mPopView.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        if (mPopView != null)
            mPopView.showAtLocation(parent, gravity, x, y);
    }

    public Alert(Builder builder, @Type int type, @DialogType int dialogType) {
        this.builder = builder;
        screenInfo = new ScreenInfo((Activity) mContext);
        switch (dialogType) {
            case DialogType.Edit:
                createEditDialog(builder, type);
                break;
            case DialogType.Warn:
                createAlertDialog(builder, type);
                break;
            case DialogType.Selected:
                createSelected(builder, type);
                break;
            case DialogType.SingleList:
            case DialogType.MultipleList:
                createListDialog(builder, type, dialogType);
                break;
            case DialogType.Time:
                createTime(builder, type);
                break;
            case DialogType.City:
                createCity(builder, type);
                break;
            case DialogType.Custom:
                if (Type.Dialog == type) {
                    showDialog(builder.layoutId, builder.bindView, builder.alertCancelListener, builder.animation);
                } else {
                    showPop(builder.layoutId, builder.bindView, 0);
                }
                break;
            default:
                break;
        }
    }

    WheelOptions wheelOptions;

    private void createCity(final Builder builder, int type) {
        IBindView bindView = new IBindView() {
            @Override
            public View convert(ViewHolder holder) {
                // ----转轮
                View optionsPicker = holder.$(R.id.optionspicker);
                wheelOptions = new WheelOptions(optionsPicker);
                wheelOptions.screenheight = screenInfo.getHeight();
                wheelOptions.setPicker(builder.options1Items,
                        builder.options2Items, builder.options3Items,
                        builder.linkage);
                int options[] = builder.options;
                wheelOptions.setCurrentItems(options[0], options[1], options[2]);
                CharSequence labels[] = builder.labels;
                wheelOptions.setLabels(labels[0], labels[1], labels[2]);
                holder.setOnClickListener(R.id.btnSubmit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (builder.optionsSelectListener != null) {
                            int[] optionsCurrentItems = wheelOptions.getCurrentItems();
                            builder.optionsSelectListener.onOptionsSelect(optionsCurrentItems[0], optionsCurrentItems[1], optionsCurrentItems[2]);
                        }
                        dismiss();
                    }
                });
                holder.setOnClickListener(R.id.btn_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                return holder.getView();
            }
        };
    }

    private void createTime(final Builder builder, int type) {

        IBindView bindView = new IBindView() {


            @Override
            public View convert(ViewHolder holder) {
                // ----时间转轮
                final View timePickerView = holder.$(R.id.timepicker);

                wheelTime = new WheelTime(timePickerView, builder.timeType);
                wheelTime.screenheight = screenInfo.getHeight();
                setTime(builder.date);
                wheelTime.setCyclic(builder.cyclic);
                holder.setOnClickListener(R.id.btnSubmit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (builder.onTimeSelectListener != null) {
                            try {
                                Date date = WheelTime.dateFormat.parse(wheelTime.getTime());
                                builder.onTimeSelectListener.onTimeSelect(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        dismiss();
                    }
                });
                holder.setOnClickListener(R.id.btn_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                return holder.getView();
            }

        };
        if (Type.Dialog == type) {
            showDialog(R.layout.pw_time, bindView, builder.alertCancelListener, builder.animation);
        } else {
            showPop(R.layout.pw_time, bindView, R.style.timepopwindow_anim_style);
        }
    }

    /**
     * 设置选中时间
     *
     * @param date
     */

    public void setTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date == null)
            calendar.setTimeInMillis(System.currentTimeMillis());
        else
            calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelTime.setPicker(year, month, day, hours, minute);
    }


    /**
     * 指定选中的时间，显示选择器
     *
     * @param parent
     * @param gravity
     * @param x
     * @param y
     * @param date
     */
    public void showAtLocation(View parent, int gravity, int x, int y, Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date == null)
            calendar.setTimeInMillis(System.currentTimeMillis());
        else
            calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelTime.setPicker(year, month, day, hours, minute);
        mPopView.update();
        showAtLocation(parent, gravity, x, y);
    }

    /**
     * 选择性的弹框
     *
     * @param builder
     * @param type
     */
    private void createSelected(final Builder builder, @Type int type) {
        final int ids[] = {
                R.id.btn_one,
                R.id.btn_two,
                R.id.btn_three
        };
        IBindView bindView = new IBindView() {

            @Override
            public View convert(final ViewHolder holder) {
                for (int i = 0; i < builder.labels.length; i++) {
                    holder.setBtnText(ids[i], builder.labels[i], new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.onSelectedItemListener.onItemClick(v.getId());
                        }
                    });
                    holder.setVisible(ids[i], View.VISIBLE);
                }
                ;
                holder.setOnClickListener(R.id.btn_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        ;
                    }
                });
                return holder.getView();
            }
        };
        if (Type.Dialog == type) {
            showDialog(R.layout.dialog_get_model, bindView, builder.alertCancelListener, EffectsType.SlideBottom);
        } else {
            showPop(R.layout.dialog_get_model, bindView, 0);
        }
    }


    public static class Builder {
        public View.OnClickListener onOkClickListener;
        public OnOKDialogClickListener onOKDialogClickListener;
        public int[] selectedPosition;
        public View.OnClickListener onCancelClickLister;
        public ChoiceModelItemClickListener choiceModelItemClickListener;
        public AlertCancelListener alertCancelListener;
        public ArrayList<MenuItem> onSelectedItem;
        public OnSelectedItemListener onSelectedItemListener;
        public OnOptionsSelectListener optionsSelectListener;
        public IBindView bindView;
        @DialogType
        public int dialogType = DialogType.Warn;
        @TimeType
        public int timeType = TimeType.Year_Month_Day;
        @EffectsType
        public int animation = EffectsType.SlideBottom;
        public OnTimeSelectListener onTimeSelectListener;
        public int layoutId;
        public String title;
        public String message;
        public String hint;
        public String okBtnStr = "ok";
        public String cancelStr = "cancel";
        public boolean cyclic = false;//时间dialog 是否滚动
        public Date date;
        public String messAges[];
        public ArrayList<String> options1Items;
        public ArrayList<ArrayList<String>> options2Items;
        public ArrayList<ArrayList<ArrayList<String>>> options3Items;
        /**
         * 是否联动
         */
        public boolean linkage;
        /**
         * 选择按钮
         */
        public int options[] = {0, 0, 0};
        /**
         * 选择按钮Lable
         */
        public CharSequence labels[] = {null, null, null};

        public void setLabels(CharSequence label1, CharSequence label2, CharSequence label3) {
            labels[0] = label1;
            labels[1] = label2;
            labels[2] = label3;
        }

        /**
         * 设置可以选择的时间范围
         *
         * @param START_YEAR
         * @param END_YEAR
         */
        public void setRange(int START_YEAR, int END_YEAR) {
            WheelTime.setSTART_YEAR(START_YEAR);
            WheelTime.setEND_YEAR(END_YEAR);
        }

        public Builder(Activity context) {
            mContext = context;
        }

        public Alert Build(@DialogType int dialogType) {
            return new Alert(this, Type.Dialog, dialogType);
        }

        public Alert BuildPopWindow(@DialogType int dialogType) {
            return new Alert(this, Type.PopWindow, dialogType);
        }

    }


    /**
     * @param layoutId 布局文件
     * @param bindView
     * @return
     */
    private PopupWindow showPop(int layoutId, IBindView bindView, int anim) {
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

    public void createAlertDialog(final Builder builder, @Type int type) {
        IBindView bindView = new IBindView() {
            @Override
            public View convert(ViewHolder holder) {
                holder.setText(R.id.titleView, builder.title);
                holder.setText(R.id.messageView, builder.message);
                holder.setText(R.id.okBtn, builder.okBtnStr);
                holder.setText(R.id.cancelBtn, builder.cancelStr);
                holder.setOnClickListener(R.id.okBtn, builder.onOkClickListener);
                if (builder.onCancelClickLister == null) {
                    holder.setOnClickListener(R.id.cancelBtn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
                } else {
                    holder.setOnClickListener(R.id.cancelBtn, builder.onCancelClickLister);
                }

                return holder.getView();
            }
        };
        if (Type.Dialog == type) {
            showDialog(R.layout.dialog_interactive, bindView, builder.alertCancelListener, EffectsType.SlideBottom);
        } else {
            showPop(R.layout.dialog_interactive, bindView, 0);
        }
    }

    public void createEditDialog(final Builder builder, @Type int type) {
        IBindView bindView = new IBindView() {
            @Override
            public View convert(ViewHolder holder) {
                final EditText message = holder.$(R.id.messageView);
                holder.setText(R.id.titleView, builder.title);
                message.setText(builder.message);
                message.setHint(builder.hint);
                holder.setText(R.id.okBtn, builder.okBtnStr);
                holder.setText(R.id.cancelBtn, builder.cancelStr);
                holder.setOnClickListener(R.id.okBtn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.onOKDialogClickListener.okOnClick(message.getText().toString());
                    }
                });
                if (builder.onCancelClickLister == null) {
                    holder.setOnClickListener(R.id.cancelBtn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
                } else {
                    holder.setOnClickListener(R.id.cancelBtn, builder.onOkClickListener);
                }

                return holder.getView();
            }
        };
        if (Type.Dialog == type) {
            showDialog(R.layout.dialog_edittext, bindView, builder.alertCancelListener, builder.animation);
        } else {
            showPop(R.layout.dialog_edittext, bindView, 0);
        }

    }

    public void createListDialog(final Builder builder, @Type int type, @DialogType final int dialogType) {// 点击
        final SparseArray<SparseArray<String>> item = new SparseArray<>();
        final SparseArray<String> array = new SparseArray(builder.messAges.length);
        IBindView bindView = new IBindView() {
            @Override
            public View convert(ViewHolder holder) {
                ListView list = holder.$(R.id.dlist);
                holder.setText(R.id.titleView, builder.title);
                holder.setText(R.id.okBtn, builder.okBtnStr);
                holder.setText(R.id.cancelBtn, builder.cancelStr);
                if (dialogType == DialogType.SingleList) {
                    holder.setVisible(R.id.btn_pane, View.GONE);
                }
                //多选时确认按钮
                holder.setOnClickListener(R.id.okBtn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.choiceModelItemClickListener.onItemClick(item);
                    }
                });
                holder.setOnClickListener(R.id.cancelBtn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int which, long id) {
                        if (item.get(which) == null) {
                            array.put(which, builder.messAges[which]);
                            item.put(which, array);
                        } else {
                            item.removeAt(which);
                        }
                        if (dialogType == DialogType.SingleList) {
                            dismiss();
                            builder.choiceModelItemClickListener.onItemClick(item);
                        }
                    }
                });
                list.setAdapter(new listItemAdapter(builder.messAges, builder.selectedPosition));
                return holder.getView();
            }

        };
        if (Type.Dialog == type) {
            showDialog(R.layout.dialog_singlelist, bindView, builder.alertCancelListener, builder.animation);
        } else {
            showPop(R.layout.dialog_singlelist, bindView, 0);
        }


    }

    class listItemAdapter extends BaseAdapter<String> {
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

    public Dialog showDialog(int layoutId, IBindView bindView, final AlertCancelListener alertCancelListener, @EffectsType final int anim) {
        if (dialog == null) {
            dialog = new Dialog(mContext, R.style.CustomProgressDialog);
        }
        final View view = LayoutInflater.from(mContext).inflate(layoutId, null);
        final ViewHolder holder = new ViewHolder(view);
        dialog.setContentView(bindView == null ? view : bindView.convert(holder));
        dialog.getWindow().setLayout(screenInfo.getWidth() * 8 / 10, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (alertCancelListener != null) {
                    alertCancelListener.OnDismissListener();
                }
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BaseEffects animator = getAnimator(anim);
                if (mDuration != -1) {
                    animator.setDuration(Math.abs(mDuration));
                }
                animator.start(view);
            }
        });
        dialog.show();
        return dialog;
    }

}
