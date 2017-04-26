package com.lixh.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.lixh.utils.ULog;

/**
 * Created by LIXH on 2017/4/5.
 * email lixhVip9@163.com
 * des
 */

public class MyRelativeLayout extends RelativeLayout {

    public MyRelativeLayout(Context context) {
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getStatusBarHeight() {
        double statusBarHeight = Math.ceil(25 * getContext().getResources().getDisplayMetrics().density);
        return (int) statusBarHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setPadding(0, 0, 0, 0);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        ULog.e(t + "" + getPaddingTop());
        super.onLayout(changed, l, t, r, b);

    }
}
