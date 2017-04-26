package com.lixh.uandroid.ui;

import android.graphics.drawable.Drawable;
import android.os.Parcel;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by LIXH on 2017/4/7.
 * email lixhVip9@163.com
 * des
 */
public class LineEntry extends Entry {
    boolean drawCircle;

    public LineEntry(boolean drawCircle) {
        this.drawCircle = drawCircle;
    }

    public LineEntry(Parcel in, boolean drawCircle) {
        super(in);
        this.drawCircle = drawCircle;
    }

    public LineEntry(float x, float y, Object data, boolean drawCircle) {
        super(x, y, data);
        this.drawCircle = drawCircle;
    }

    public LineEntry(float x, float y, Drawable icon, boolean drawCircle) {
        super(x, y, icon);
        this.drawCircle = drawCircle;
    }

    public LineEntry(float x, float y, Drawable icon, Object data, boolean drawCircle) {
        super(x, y, icon, data);
        this.drawCircle = drawCircle;
    }

    /**
     * A Entry represents one single entry in the chart.
     *
     * @param x the x value
     * @param y the y value (the actual value of the entry)
     */
    public LineEntry(float x, float y, boolean drawCircle) {
        super(x, y);
        this.drawCircle = drawCircle;
    }

    /**
     * A Entry represents one single entry in the chart.
     *
     * @param x    the x value
     * @param y    the y value (the actual value of the entry)
     * @param data Spot for additional data this Entry represents.
     */
    public LineEntry(float x, float y, boolean drawCircle, Object data) {
        super(x, y, data);
        this.drawCircle = drawCircle;
    }

    public boolean isDrawCircle() {
        return drawCircle;
    }

    public void setDrawCircle(boolean drawCircle) {
        this.drawCircle = drawCircle;
    }

    /**
     * returns an exact copy of the entry
     *
     * @return
     */
    public LineEntry copy() {
        LineEntry e = new LineEntry(getX(), getY(),getIcon(), getData(), drawCircle);
        return e;
    }
}
