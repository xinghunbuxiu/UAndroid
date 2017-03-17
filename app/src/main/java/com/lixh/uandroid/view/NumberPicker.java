package com.lixh.uandroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.lixh.uandroid.R;

public class NumberPicker extends LinearLayout {

    public NumberPicker(Context context) {
        this(context, null);
    }

    public NumberPicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.numberPickerStyle);
    }

    public NumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
