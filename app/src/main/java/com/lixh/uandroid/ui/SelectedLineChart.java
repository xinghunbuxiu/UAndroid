package com.lixh.uandroid.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.lixh.uandroid.view.MyXAxisRenderer;


/**
 * Chart that draws lines, surfaces, circles, ...
 *
 * @author Philipp Jahoda
 */
public class SelectedLineChart extends BarLineChartBase<LineData> implements LineDataProvider {
    public SelectedLineChart(Context context) {
        super(context);
    }
    public SelectedLineChart(Context context,AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectedLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void init() {
        super.init();
        mRenderer = new SelectedLineChartRenderer(this, mAnimator, mViewPortHandler);
        mXAxisRenderer = new MyXAxisRenderer(mViewPortHandler, mXAxis, mLeftAxisTransformer, this);
    }


    @Override
    public LineData getLineData() {
        return mData;
    }

    @Override
    protected void onDetachedFromWindow() {
        // releases the bitmap in the renderer to avoid oom error
        if (mRenderer != null && mRenderer instanceof LineChartRenderer) {
            ((LineChartRenderer) mRenderer).releaseBitmap();
        }
        super.onDetachedFromWindow();
    }



    public void setHighlightValue(Highlight h) {
        if (mData == null)
            mIndicesToHighlight = null;
        else {
            mIndicesToHighlight = new Highlight[]{
                    h};
        }
        invalidate();
    }
}

