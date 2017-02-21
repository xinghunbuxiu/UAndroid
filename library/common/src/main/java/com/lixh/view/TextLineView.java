package com.lixh.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.lixh.R;


/**
 * TODO: document your custom view class.
 */
public class TextLineView extends TextView {
    public float lineHeight = 4;
    float mTextWidth;
    float mTextHeight;

    public TextLineView(Context context) {
        super(context);
        init(null, 0);
    }

    public TextLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TextLineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TextLineView, defStyle, 0);
        lineHeight = a.getDimension(
                R.styleable.TextLineView_lineHeight,
                lineHeight);
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        mTextWidth = getPaint().measureText(getText().toString());

        Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
        mTextHeight = fontMetrics.bottom;
        canvas.drawLine(0, (contentHeight - lineHeight) / 2, (contentWidth - mTextWidth) / 2, (contentHeight + lineHeight) / 2, getPaint());
        canvas.drawLine((contentWidth + mTextWidth) / 2, (contentHeight - lineHeight) / 2, contentWidth, (contentHeight + lineHeight) / 2, getPaint());

    }


}
