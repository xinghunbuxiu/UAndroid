package com.lixh.uandroid.ui;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.lixh.base.BaseActivity;
import com.lixh.uandroid.R;
import com.lixh.view.UToolBar;

import java.util.ArrayList;

import butterknife.Bind;

public class PieChartActivity extends BaseActivity {
    @Bind(R.id.pieChart)
    PieChart mChart;

    protected String[] mParties = new String[]{
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };
    // add a lot of colors

    ArrayList<Integer> colors = new ArrayList<Integer>();
    ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

    @Override
    public void init(Bundle savedInstanceState) {
        initPieChart();
    }

    public void initPieChart() {
        mChart.setUsePercentValues(true);
        mChart.setDragDecelerationFrictionCoef(0.95f);//减速
        mChart.setExtraOffsets(10, 20, 10, 10);//内边距
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);
        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(58f);
        mChart.setDrawCenterText(false);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        setData(4, 100);
        mChart.getDescription().setEnabled(false);
        mChart.animateY(1400, Easing.EasingOption.EaseInCirc);
        Legend l = mChart.getLegend();
        l.setDrawInside(false);
        l.setEnabled(false);
        // entry label styling
        mChart.setDrawEntryLabels(false);//标签
    }

    private void setData(int count, float range) {

        float mult = range;


        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < count; i++) {
            entries.add(new PieEntry((float) ((Math.random() * mult) + mult / 5),
                    mParties[i % mParties.length]));
        }

        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(5f);


        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter() {
            // IValueFormatter
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                PieEntry pieEntry = null;
                if (entry instanceof PieEntry) {
                    pieEntry = (PieEntry) entry;
                }
                return pieEntry.getLabel() + mFormat.format(value) + " %";
            }
        });
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);
        // undo all highlights
        mChart.highlightValues(null);
        dataSet.setValueLinePart1OffsetPercentage(100f);
        dataSet.setValueLinePart1Length(0.5f);
        dataSet.setValueLinePart2Length(0.5f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);


        mChart.invalidate();
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_piechart;
    }

    @Override
    public void initTitle(UToolBar toolBar) {
        toolBar.setTitle("chart");
    }

}
