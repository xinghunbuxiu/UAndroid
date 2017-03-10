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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.common.dialog.ImpAlert.ChoiceModelItemClickListener;
import com.common.dialog.ImpAlert.DialogType;
import com.common.dialog.ImpAlert.IBindView;
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

    public static void showCustomDialog(Context context, @LayoutRes int layoutId, AlertCancelListener listener) {
        Alert.Builder mBuilder = new Builder(context);
        mBuilder.setAlertCancelListener(listener);
        mBuilder.setLayoutId(layoutId).Build(DialogType.Custom);
    }

    public static void displayAlertDialog(Context context, String title, String msg, String okStr, String cancelStr, View.OnClickListener onOkClickListener, View.OnClickListener onCancelClickListener) {
        Alert.Builder mBuilder = new Builder(context);
        mBuilder.setTitle(title).setMessage(msg).setOkBtnStr(okStr).setCancelStr(cancelStr)
                .setOnOkClickListener(onOkClickListener).setOnCancelClickLister(onCancelClickListener)
                .Build(DialogType.Warn);
    }

    public static void displayWheelDialog(Context context, ArrayList<String> options1Items, ArrayList<ArrayList<String>> options2Items, ArrayList<ArrayList<ArrayList<String>>> options3Items, View.OnClickListener onOkClickListener, View.OnClickListener onCancelClickListener) {
        Alert.Builder mBuilder = new Builder(context);
        mBuilder.setOptions1Items(options1Items).setOptions2Items(options2Items).setOptions3Items(options3Items)
                .setOnOkClickListener(onOkClickListener).setOnCancelClickLister(onCancelClickListener)
                .Build(DialogType.City);
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
                    showDialog(builder.getLayoutId(), builder.getBindView(), builder.getAlertCancelListener(), builder.getAnimation());
                } else {
                    showPop(builder.getLayoutId(), builder.getBindView(), 0);
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
                wheelOptions.setPicker(builder.getOptions1Items(),
                        builder.getOptions2Items(), builder.getOptions3Items(),
                        builder.getLinkage());
                int options[] = builder.getOptions();
                wheelOptions.setCurrentItems(options[0], options[1], options[2]);
                String labels[] = builder.getLabels();
                wheelOptions.setLabels(labels[0], labels[1], labels[2]);
                holder.setOnClickListener(R.id.btnSubmit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (builder.getOptionsSelectListener() != null) {
                            int[] optionsCurrentItems = wheelOptions.getCurrentItems();
                            builder.getOptionsSelectListener().onOptionsSelect(optionsCurrentItems[0], optionsCurrentItems[1], optionsCurrentItems[2]);
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

                wheelTime = new WheelTime(timePickerView, builder.getTimeType());
                wheelTime.screenheight = screenInfo.getHeight();
                setTime(builder.getDate());
                wheelTime.setCyclic(builder.isCyclic());
                holder.setOnClickListener(R.id.btnSubmit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (builder.getOnTimeSelectListener() != null) {
                            try {
                                Date date = WheelTime.dateFormat.parse(wheelTime.getTime());
                                builder.getOnTimeSelectListener().onTimeSelect(date);
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
            showDialog(R.layout.pw_time, bindView, builder.getAlertCancelListener(), builder.getAnimation());
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
                for (int i = 0; i < builder.getOnSelectedItem().size(); i++) {
                    final MenuItem menu = builder.getOnSelectedItem().get(i);
                    holder.setBtnText(ids[i], menu.getText(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            menu.getOnSelectedItemListener().onItemClick(menu.getPosition());
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
            showDialog(R.layout.dialog_get_model, bindView, builder.getAlertCancelListener(), EffectsType.SlideBottom);
        } else {
            showPop(R.layout.dialog_get_model, bindView, 0);
        }
    }


    public static class Builder {
        private View.OnClickListener onOkClickListener;
        private int[] selectedPosition;
        private View.OnClickListener onCancelClickLister;
        private ChoiceModelItemClickListener choiceModelItemClickListener;
        private AlertCancelListener alertCancelListener;
        private ArrayList<MenuItem> onSelectedItem;
        private OnSelectedItemListener onSelectedItemListener;
        private OnOptionsSelectListener optionsSelectListener;
        private IBindView bindView;
        @DialogType
        private int dialogType = DialogType.Warn;
        @TimeType
        private int timeType = TimeType.Year_Month_Day;
        @EffectsType
        private int animation = EffectsType.SlideBottom;
        public OnTimeSelectListener onTimeSelectListener;
        private int layoutId;
        private String title;
        private String message;
        private String okBtnStr;
        private String cancelStr;
        boolean cyclic = false;//时间dialog 是否滚动
        private Date date;
        private ArrayList<String> options1Items;
        private ArrayList<ArrayList<String>> options2Items;
        private ArrayList<ArrayList<ArrayList<String>>> options3Items;
        private boolean linkage;
        int options[] = {0, 0, 0};
        String labels[] = {null, null, null};

        public String[] getLabels() {
            return labels;
        }

        public void setLabels(String label1, String label2, String label3) {
            labels[0] = label1;
            labels[1] = label2;
            labels[2] = label3;
        }


        public int[] getOptions() {
            return options;
        }

        public Builder setOptions(int[] options) {
            this.options = options;
            return this;
        }

        //是否联动
        public void setLinkage(boolean linkage) {
            this.linkage = linkage;
        }


        public Builder setOptions1Items(ArrayList<String> options1Items) {
            this.options1Items = options1Items;
            return this;
        }

        public Builder setOptions2Items(ArrayList<ArrayList<String>> options2Items) {
            this.options2Items = options2Items;
            return this;
        }

        public Builder setOptions3Items(ArrayList<ArrayList<ArrayList<String>>> options3Items) {
            this.options3Items = options3Items;
            return this;
        }

        public ArrayList<String> getOptions1Items() {
            return options1Items;
        }

        public ArrayList<ArrayList<String>> getOptions2Items() {
            return options2Items;
        }

        public ArrayList<ArrayList<ArrayList<String>>> getOptions3Items() {
            return options3Items;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Date getDate() {
            return date;
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

        public OnTimeSelectListener getOnTimeSelectListener() {
            return onTimeSelectListener;
        }

        public void setOnTimeSelectListener(OnTimeSelectListener onTimeSelectListener) {
            this.onTimeSelectListener = onTimeSelectListener;
        }

        public boolean isCyclic() {
            return cyclic;
        }

        public void setCyclic(boolean cyclic) {
            this.cyclic = cyclic;
        }

        @TimeType
        public int getTimeType() {
            return timeType;
        }

        public void setTimeType(@TimeType int timeType) {
            this.timeType = timeType;
        }

        public Builder setOnOptionsSelectListener(
                OnOptionsSelectListener optionsSelectListener) {
            this.optionsSelectListener = optionsSelectListener;
            return this;
        }

        public OnOptionsSelectListener getOptionsSelectListener() {
            return optionsSelectListener;
        }


        public Builder setAnimation(@EffectsType int animation) {
            this.animation = animation;
            return this;
        }

        public ArrayList<MenuItem> getOnSelectedItem() {
            return onSelectedItem;
        }

        public OnSelectedItemListener getOnSelectedItemListener() {
            return onSelectedItemListener;
        }

        public Builder setOnSelectedItem(MenuItem onSelectedItem) {
            this.onSelectedItem.add(onSelectedItem);
            return this;
        }

        /**
         * 选择性弹出框的监听
         *
         * @param onSelectedItemListener
         */
        public Builder setOnSelectedItemListener(OnSelectedItemListener onSelectedItemListener) {
            this.onSelectedItemListener = onSelectedItemListener;
            return this;
        }


        public View.OnClickListener getOnCancelClickLister() {
            return onCancelClickLister;
        }

        /**
         * 取消按钮的监听
         *
         * @param onCancelClickLister
         * @return
         */
        public Builder setOnCancelClickLister(View.OnClickListener onCancelClickLister) {
            this.onCancelClickLister = onCancelClickLister;
            return this;
        }

        public View.OnClickListener getOnOkClickListener() {
            return onOkClickListener;
        }

        /**
         * 确认按钮的监听
         *
         * @param onOkClickListener
         * @return
         */
        public Builder setOnOkClickListener(View.OnClickListener onOkClickListener) {
            this.onOkClickListener = onOkClickListener;
            return this;
        }


        public IBindView getBindView() {
            return bindView;
        }

        /**
         * 自定义的dialog绑定数据
         *
         * @param bindView
         */
        public Builder setBindView(IBindView bindView) {
            this.bindView = bindView;
            return this;
        }

        public int[] getSelectedPosition() {
            return selectedPosition;
        }

        public AlertCancelListener getAlertCancelListener() {
            return alertCancelListener;
        }

        /**
         * dialog消失的监听
         *
         * @param alertCancelListener
         * @return
         */
        public Builder setAlertCancelListener(AlertCancelListener alertCancelListener) {
            this.alertCancelListener = alertCancelListener;
            return this;
        }

        public String[] getMessAges() {
            return messAges;
        }

        /**
         * 多选的id
         *
         * @param selectedPosition
         * @return
         */
        public Builder setSelectedPosition(int[] selectedPosition) {
            this.selectedPosition = selectedPosition;
            return this;
        }

        /**
         * 多选框的监听
         * i
         *
         * @param messAges
         * @return
         */
        public Builder setMessAges(String[] messAges) {
            this.messAges = messAges;
            return this;
        }

        String messAges[];


        /**
         * 标题
         *
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public String getCancelStr() {
            return cancelStr;
        }

        /**
         * 取消按钮
         *
         * @param cancelStr
         * @return
         */
        public Builder setCancelStr(String cancelStr) {
            this.cancelStr = cancelStr;
            return this;
        }

        public ChoiceModelItemClickListener getChoiceModelItemClickListener() {
            return choiceModelItemClickListener;
        }

        /**
         * 多选或者单选列表的弹框
         *
         * @param choiceModelItemClickListener
         * @return
         */
        public Builder setChoiceModelItemClickListener(ChoiceModelItemClickListener choiceModelItemClickListener) {
            this.choiceModelItemClickListener = choiceModelItemClickListener;
            return this;
        }

        public int getLayoutId() {
            return layoutId;
        }

        @EffectsType
        public int getAnimation() {
            return animation;
        }

        /**
         * 自定义布局id
         *
         * @param layoutId
         * @return
         */
        public Builder setLayoutId(int layoutId) {
            this.layoutId = layoutId;
            return this;
        }

        public String getMessage() {
            return message;
        }

        /**
         * 提示弹出框的信息
         *
         * @param message
         * @return
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public String getOkBtnStr() {
            return okBtnStr;
        }

        /**
         * 确认按钮的文字
         *
         * @param okBtnStr
         * @return
         */
        public Builder setOkBtnStr(String okBtnStr) {
            this.okBtnStr = okBtnStr;
            return this;
        }


        public Builder(Context context) {
            mContext = context;
        }

        public Alert Build(@DialogType int dialogType) {
            return new Alert(this, Type.Dialog, dialogType);
        }

        public Alert BuildPopWindow(@DialogType int dialogType) {
            return new Alert(this, Type.PopWindow, dialogType);
        }


        public boolean getLinkage() {
            return linkage;
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
                holder.setText(R.id.titleView, builder.getTitle());
                holder.setText(R.id.messageView, builder.getMessage());
                holder.setText(R.id.okBtn, builder.getOkBtnStr());
                holder.setText(R.id.cancelBtn, builder.getCancelStr());
                holder.setOnClickListener(R.id.okBtn, builder.getOnOkClickListener());
                if (builder.getOnCancelClickLister() == null) {
                    holder.setOnClickListener(R.id.cancelBtn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
                } else {
                    holder.setOnClickListener(R.id.cancelBtn, builder.getOnCancelClickLister());
                }

                return holder.getView();
            }
        };
        if (Type.Dialog == type) {
            showDialog(R.layout.dialog_interactive, bindView, builder.getAlertCancelListener(), EffectsType.SlideBottom);
        } else {
            showPop(R.layout.dialog_interactive, bindView, 0);
        }
    }

    public void createEditDialog(final Builder builder, @Type int type) {
        IBindView bindView = new IBindView() {
            @Override
            public View convert(ViewHolder holder) {
                holder.setText(R.id.titleView, builder.getTitle());
                holder.setEditText(R.id.messageView, builder.getMessage());
                holder.setText(R.id.okBtn, builder.getOkBtnStr());
                holder.setText(R.id.cancelBtn, builder.getCancelStr());
                holder.setOnClickListener(R.id.okBtn, builder.getOnOkClickListener());
                if (builder.getOnCancelClickLister() == null) {
                    holder.setOnClickListener(R.id.cancelBtn, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
                } else {
                    holder.setOnClickListener(R.id.cancelBtn, builder.getOnOkClickListener());
                }

                return holder.getView();
            }
        };
        if (Type.Dialog == type) {
            showDialog(R.layout.dialog_edittext, bindView, builder.getAlertCancelListener(), builder.getAnimation());
        } else {
            showPop(R.layout.dialog_edittext, bindView, 0);
        }

    }

    public void createListDialog(final Builder builder, @Type int type, @DialogType final int dialogType) {// 点击
        final SparseArray<SparseArray<String>> item = new SparseArray<>();
        final SparseArray<String> array = new SparseArray(builder.getMessAges().length);
        IBindView bindView = new IBindView() {
            @Override
            public View convert(ViewHolder holder) {
                ListView list = holder.$(R.id.dlist);
                holder.setText(R.id.titleView, builder.getTitle());
                holder.setText(R.id.okBtn, builder.getOkBtnStr());
                holder.setText(R.id.cancelBtn, builder.getCancelStr());
                if (dialogType == DialogType.SingleList) {
                    holder.setVisible(R.id.btn_pane, View.GONE);
                }
                //多选时确认按钮
                holder.setOnClickListener(R.id.okBtn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.getChoiceModelItemClickListener().onItemClick(item);
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
                            array.put(which, builder.getMessAges()[which]);
                            item.put(which, array);
                        } else {
                            item.removeAt(which);
                        }
                        if (dialogType == DialogType.SingleList) {
                            dismiss();
                            builder.getChoiceModelItemClickListener().onItemClick(item);
                        }
                    }
                });
                list.setAdapter(new listItemAdapter(builder.getMessAges(), builder.getSelectedPosition()));
                return holder.getView();
            }

        };
        if (Type.Dialog == type) {
            showDialog(R.layout.dialog_singlelist, bindView, builder.getAlertCancelListener(), builder.getAnimation());
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
        dialog.setContentView(bindView == null ? view : bindView.convert(holder), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (alertCancelListener != null) {
                    alertCancelListener.onCancelProgress();
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
