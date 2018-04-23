package com.lixh.view.refresh;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import java.util.List;

/**
 * Created by LIXH on 2018/4/12.
 * email lixhVip9@163.com
 * des
 */

public class StickNavLayout extends LinearLayout {
    private StickNavAdapter stickNavAdapter;
    private OverScroller mScroller;
    private List<MultiItem> items_views;
    private View stickNavView;
    int navId;
    float slideHeight = 0;

    public StickNavLayout(Context context) {
        this(context, null);
    }

    public StickNavLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickNavLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defstyleAttr) {
        if (isInEditMode()) {
            return;
        }

        if (getChildCount() > 3) {
            throw new RuntimeException("can only have three child widget");
        }

        mScroller = new OverScroller(context);

    }

    public void setAdapter(StickNavAdapter adapter) {
        this.stickNavAdapter = adapter;
        buildNavLayout();
    }

    public void buildNavLayout() {
        if (stickNavAdapter == null) {
            throw new RuntimeException("stickNavAdapter cannot be empty");
        }
        navId = stickNavAdapter.getStickType();
        items_views = stickNavAdapter.getItems_views();
        for (MultiItem item : items_views) {
            View.inflate(getContext(), item.getLayoutId(), this);
        }
        stickNavAdapter.initView(this);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (navId > 0) {
            stickNavView = findViewById(navId);
            stickNavView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            slideHeight = stickNavView.getY();
                        }
                    });

        }

    }


    boolean resetDispatchTouchEvent(MotionEvent ev) {
        ev.setAction(MotionEvent.ACTION_CANCEL);
        MotionEvent newEvent = MotionEvent.obtain(ev);
        dispatchTouchEvent(ev);
        newEvent.setAction(MotionEvent.ACTION_DOWN);
        return dispatchTouchEvent(newEvent);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        return isNeedMove(y);
    }

    private boolean isNeedMove(float y) {
        if (getScrollY() < slideHeight - 10) {

        } else if (getScrollY())
            return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            super.scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

    private float dy;
    private float dx;
    private float mLastX;

    /**
     * 判断是否需要由该控件来控制滑动事件
     */
    private boolean isNeedMyMove() {
        if (Math.abs(dy) < Math.abs(dx)) {
            return false;
        }
        if (dy > 0 && getScrollY() > slideHeight + 20 || getScrollY() < slideHeight - 20) {

            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float y = e.getY();
                dy = y - mLastY;
                mLastY = y;
                scrollBy(0, (int) (-dy));
                if (isNeedMyMove) {
                    needResetAnim = false;      //按下的时候关闭回弹
                    doMove();
                } else {
                    if (dy != 0 && getScrollY() > -30 && getScrollY() < 30) {
                        scrollBy(0, -getScrollY());
                        isChangeFocus = false;
                        e.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(e);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                break;
        }

        return true;

    }

    @Override
    public void scrollTo(int x, int y) {
        if (y <= 0) {
            y = 0;
        }
        if (y >= slideHeight) {
            y = (int) slideHeight;
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }

    }

}
