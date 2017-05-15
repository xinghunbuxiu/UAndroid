package com.lixh.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.OverScroller;

/**
 * Created by LIXH on 2017/5/8.
 * email lixhVip9@163.com
 * des
 */

public class SlideMenu extends FrameLayout {
    View leftView;
    View rightView;
    View contentView;
    OverScroller scroller;
    private boolean isNeedMyMove;
    private float dy;
    private float dx;
    private float mLastX;
    private float mLastY;
    Slide slide = Slide.LEFT;

    public void attachToActivity(Activity activity) {
        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decor.removeView(decorChild);
        addView(decorChild);
        setContentView(decorChild);
        decor.addView(this);

    }

    public enum Slide {
        LEFT, RIGHT
    }

    public SlideMenu(Context context) {
        this(context, null);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new OverScroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isNeedMyMove;
    }

    public SlideMenu setView(View view, Slide slide) {
        if (slide == Slide.LEFT) {
            leftView = view;
            addView(leftView);
        } else {
            rightView = view;
            addView(rightView);
        }

        return this;
    }

    public SlideMenu setContentView(View view) {
        contentView = view;
        return this;
    }

    /**
     * 处理多点触控的情况，准确地计算Y坐标和移动距离dy
     * 同时兼容单点触控的情况
     */
    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;

    public void dealMulTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                mLastX = x;
                mLastY = y;
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                dx = x - mLastX;
                dy = y - mLastY;
                mLastY = y;
                mLastX = x;
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId != mActivePointerId) {
                    mLastX = MotionEventCompat.getX(ev, pointerIndex);
                    mLastY = MotionEventCompat.getY(ev, pointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastX = MotionEventCompat.getX(ev, newPointerIndex);
                    mLastY = MotionEventCompat.getY(ev, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                break;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        dealMulTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isNeedMyMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(dx) > Math.abs(dy)) {
                    isNeedMyMove = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.

                dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (isNeedMyMove) {
                    scrollBy((int) -dx, 0);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                eventUp();
                break;
        }

        return true;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        if (slide == Slide.LEFT) {
            if (x > 0) {
                x = 0;
            }
            if (x < -leftView.getMeasuredWidth()) {
                x = -leftView.getMeasuredWidth();
            }
        } else {
            if (x < 0) {
                x = 0;
            }
            if (x > leftView.getMeasuredWidth()) {
                x = leftView.getMeasuredWidth();
            }
        }
        if (getScrollX() != x) {
            super.scrollTo(x, y);
        }
    }

    private void eventUp() {
        int scrollX = getScrollX();
        if (slide == Slide.LEFT) {
            if (scrollX < -leftView.getMeasuredWidth() / 2) {
                scroller.startScroll(scrollX, 0, leftView.getMeasuredWidth(), 0, 400);
                invalidate();
            } else {
                scroller.startScroll(scrollX, 0, leftView.getMeasuredWidth() - scrollX, 0, 400);
                invalidate();
            }
        } else if (slide == Slide.RIGHT) {
            if (scrollX > leftView.getMeasuredWidth() / 2) {
                scroller.startScroll(scrollX, 0, leftView.getMeasuredWidth(), 0, 400);
                invalidate();
            } else {
                scroller.startScroll(scrollX, 0, -scrollX, 0, 400);
                invalidate();
            }
        }
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), 0);
            invalidate();
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        contentView.layout(left, top, right, bottom);
        if (slide == Slide.LEFT) {
            leftView.layout(-leftView.getMeasuredWidth(), top, 0, bottom);
        } else {
            rightView.layout(right, top, right + rightView.getMeasuredWidth(), bottom);
        }
    }
}
