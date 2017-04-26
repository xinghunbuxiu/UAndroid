package com.lixh.uandroid.ui;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.lixh.base.BaseActivity;
import com.lixh.uandroid.R;
import com.lixh.uandroid.view.DayAxisValueFormatter;
import com.lixh.uandroid.view.MyMarkerView;
import com.lixh.view.UToolBar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class LineChartActivity extends BaseActivity implements OnChartValueSelectedListener {
    @Bind(R.id.line_chart)
    SelectedLineChart mChart;


    @Override
    protected void init(Bundle savedInstanceState) {
        initLineChart();
    }

    public void initLineChart() {
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mChart.getDescription().setEnabled(false);
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setScaleYEnabled(false);
        mChart.setExtraBottomOffset(18);
        mChart.setPinchZoom(false);
        mChart.setBackgroundColor(-1);
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);//放大的时候X值不增多
        xAxis.setLabelCount(7);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setValueFormatter(new DayAxisValueFormatter(mChart));
        YAxis leftAxis = mChart.getAxisLeft();
        setDrawBottomYLabelEntry(leftAxis, false);
        leftAxis.removeAllLimitLines();
        leftAxis.setAxisMinimum(0);
        leftAxis.setLabelCount(7, true);
        leftAxis.setDrawLimitLinesBehindData(true);
        mChart.getAxisRight().setEnabled(false);
        setData(15, 100.0F);
        mChart.setVisibleXRangeMinimum(7);
        mChart.setVisibleXRangeMaximum(7);

        this.mChart.animateX(2500);
        this.mChart.getLegend().setEnabled(false);
    }

    public void setDrawBottomYLabelEntry(YAxis yAxis, boolean enabled) {
        try {
            Field field = yAxis.getClass().getDeclaredField("mDrawBottomYLabelEntry");
            field.setAccessible(true);
            field.setBoolean(yAxis, enabled);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    private void setData(int count, float range) {

        List<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {

            float val = (float) (Math.random() * range) + 3;
            values.add(new LineEntry(i, val, false));
        }
        values.add(new LineEntry(15, 200, false));
        values.add(new LineEntry(16, 400, false));
        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, null);
            set1.setColor(Color.RED);
            set1.setLineWidth(2.5f);
            set1.setDrawCircles(true);
            set1.setCircleColor(Color.RED);
            set1.setCircleRadius(4f);
            set1.setFillColor(Color.BLUE);
            set1.setDrawValues(true);
            set1.setDrawIcons(false);
            set1.setValueTextSize(10f);
            set1.setDrawFilled(true);
            set1.setHighlightEnabled(true);
            set1.setFormLineWidth(1f);
            set1.setDrawValues(false);
            set1.setHighLightColor(Color.TRANSPARENT); // 设置点击某个点时，横竖两条线的颜色
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);
            // set data
            mChart.setData(data);
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_linechart;
    }

    @Override
    public void initTitle(UToolBar toolBar) {
        toolBar.setTitle("chart");
        toolBar.setNavigationIcon(R.mipmap.back_normal, "dddd");
        toolBar.setElevation(5);
    }


    float oldFloat;

    @Override
    public void onValueSelected(final Entry e, Highlight h) {
        LineDataSet set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
        List<Entry> values = set1.getValues();
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) instanceof LineEntry) {
                LineEntry entry = (LineEntry) values.get(i);
                if (e.getX() == entry.getX()) {
                    if (oldFloat != e.getX()) {
                        oldFloat = e.getX();
                    }
                    entry.setDrawCircle(true);
                } else if (e.getX() == oldFloat) {
                    entry.setDrawCircle(false);
                } else {
                    entry.setDrawCircle(false);
                }
            }
        }
        mChart.invalidate();
    }

    @Override
    public void onNothingSelected() {
        LineDataSet set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
        List<Entry> values = set1.getValues();
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) instanceof LineEntry) {
                LineEntry entry = (LineEntry) values.get(i);
                if (oldFloat == entry.getX()) {
                    entry.setDrawCircle(false);
                }
            }
        }
        mChart.invalidate();
    }

}
