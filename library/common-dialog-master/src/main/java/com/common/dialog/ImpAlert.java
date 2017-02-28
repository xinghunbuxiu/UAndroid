package com.common.dialog;

import android.support.annotation.IntDef;
import android.util.SparseArray;
import android.view.View;

import com.common.dialog.pickerview.ViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * Created by LIXH on 2017/2/23.
 * email lixhVip9@163.com
 * des
 */

public class ImpAlert {
    @IntDef({
            TimeType.All,
            TimeType.Year_Month_Day,
            TimeType.Hours_Min,
            TimeType.Month_Day_Hour_Min
    })
    public @interface TimeType {
        int All = 0;
        int Year_Month_Day = 1;
        int Hours_Min = 2;
        int Month_Day_Hour_Min = 3;
    }// 四种选择模式，年月日时分，年月日，时分，月日时分

    @IntDef({
            Type.Dialog,
            Type.PopWindow
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int Dialog = 0;
        int PopWindow = 1;
    }

    @IntDef({
            DialogType.Edit, //带输入框
            DialogType.Warn,//关于提示
            DialogType.Selected,//选择的如（类似照片选择啥的）
            DialogType.SingleList,//单选列表
            DialogType.MultipleList,//多选列表
            DialogType.Time, //时间
            DialogType.City, //城市
            DialogType.Custom, //自定义
    })
    @Retention(RetentionPolicy.SOURCE)
    public  @interface DialogType {
        int Edit = 0; //带输入框
        int Warn = 1; //关于提示
        int Selected = 2;//选择的如（类似照片选择啥的）
        int SingleList = 3;//单选列表
        int MultipleList = 4;//多选列表
        int Time = 5;
        int City = 6;
        int Custom = 7;//自定义
    }

    @IntDef(value = {
            EffectsType.FadeIn,
            EffectsType.SlideLeft,
            EffectsType.SlideTop,
            EffectsType.SlideBottom,
            EffectsType.SlideRight,
            EffectsType.Fall,
            EffectsType.NewsPaper,
            EffectsType.FlipH,
            EffectsType.FlipV,
            EffectsType.RotateBottom,
            EffectsType.RotateLeft,
            EffectsType.Slit,
            EffectsType.Shake,
            EffectsType.SideFall,


    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface EffectsType {

        int FadeIn = 0;
        int SlideLeft = 1;
        int SlideTop = 2;
        int SlideBottom = 3;
        int SlideRight = 4;
        int Fall = 5;
        int NewsPaper = 6;
        int FlipH = 7;
        int FlipV = 8;
        int RotateBottom = 9;
        int RotateLeft = 10;
        int Slit = 11;
        int Shake = 12;
        int SideFall = 13;

    }

    public interface AlertCancelListener {
        void onCancelProgress();
    }

    public interface IBindView {
        View convert(ViewHolder holder);
    }

    public interface OnOptionsSelectListener {
        void onOptionsSelect(int options1, int option2, int options3);
    }

    public interface ChoiceModelItemClickListener {
        /**
         * @param item
         */
        void onItemClick(SparseArray<SparseArray<String>> item);
    }

    public interface OnSelectedItemListener {
        void onItemClick(int position);
    }

    public interface OnTimeSelectListener {
        void onTimeSelect(Date date);
    }
}