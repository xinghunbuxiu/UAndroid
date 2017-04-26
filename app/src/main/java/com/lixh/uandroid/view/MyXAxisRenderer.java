package com.lixh.uandroid.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.lixh.uandroid.ui.LineEntry;
import com.lixh.utils.ULog;

/**
 * author：ajiang
 * mail：1025065158@qq.com
 * blog：http://blog.csdn.net/qqyanjiang
 */
public class MyXAxisRenderer extends XAxisRenderer {
    private final BarLineChartBase mChart;


    public MyXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans, BarLineChartBase chart) {
        super(viewPortHandler, xAxis, trans);
        mChart = chart;
    }

    private static Paint.FontMetrics mFontMetricsBuffer = new Paint.FontMetrics();
    private static Rect mDrawTextRectBuffer = new Rect();
    int color;
    private float lineHeight;

    protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
        Entry entry = mChart.getEntryByTouchPoint(x, y);
        lineHeight = mAxisLabelPaint.getFontMetrics(mFontMetricsBuffer);
        float lastY = y + (mChart.getMeasuredHeight() - y - lineHeight-5) / 2;
        if (entry instanceof LineEntry) {
            LineEntry lineEntry = (LineEntry) entry;
            if (lineEntry.isDrawCircle()) {
                LineDataSet set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                mAxisLabelPaint.setColor(set1.getColor());
                drawBackGroud(c, formattedLabel, x, lastY);
            } else {
                mAxisLabelPaint.setColor(mXAxis.getTextColor());
            }
        }
        Utils.drawXAxisValue(c, formattedLabel, x, lastY, mAxisLabelPaint, anchor, angleDegrees);
    }

    private void drawBackGroud(Canvas c, String label, float x, float y) {
        final float textLength = mAxisLabelPaint.measureText(label);
        if (!TextUtils.isEmpty(label)) {
            mDrawTextRectBuffer.left = (int) (x - textLength / 2 - 10);
            mDrawTextRectBuffer.top = (int) y - 10;
            mDrawTextRectBuffer.right = (int) (x + textLength / 2 + 10);
            mDrawTextRectBuffer.bottom = (int) (y + lineHeight) + 10;
            ULog.e(mDrawTextRectBuffer.toString());
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setAntiAlias(true);                       //设置画笔为无锯齿
            paint.setColor(mAxisLabelPaint.getColor());                    //设置画笔颜色
            paint.setStrokeWidth((float) 3.0);              //线宽
            paint.setStyle(Paint.Style.STROKE);
            c.drawRoundRect(new RectF(mDrawTextRectBuffer), 20, 20, paint);
        }
    }
}