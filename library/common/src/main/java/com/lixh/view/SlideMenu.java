package com.lixh.view;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import java.util.Arrays;

/**
 * Created by LIXH on 2017/5/8.
 * email lixhVip9@163.com
 * des
 */

public class SlideMenu extends FrameLayout {
    BaseSlideView mSlideView;
    View contentView;
    OverScroller scroller;
    private boolean isNeedMyMove;
    private float dy;
    private float dx;
    private float mLastX;
    private float mLastY;
    Slide slide = Slide.LEFT;
    private int mEdgeSize;
    private static final int EDGE_SIZE = 20; // dp
    private int mPointersDown;

    /**
     * Edge flag indicating that the left edge should be affected.
     */
    public static final int EDGE_LEFT = 1 << 0;
    private int[] mInitialEdgesTouched;
    /**
     * 处理多点触控的情况，准确地计算Y坐标和移动距离dy
     * 同时兼容单点触控的情况
     */
    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;
    /**
     * Edge flag indicating that the right edge should be affected.
     */
    public static final int EDGE_RIGHT = 1 << 1;

    public void attachToActivity(Activity activity) {
        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decor.removeView(decorChild);
        addView(decorChild);
        setContentView(decorChild);
        decor.addView(this);

    }

    public enum Slide {
        NONE, LEFT, RIGHT
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
        final float density = context.getResources().getDisplayMetrics().density;
        mEdgeSize = (int) (EDGE_SIZE * density + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void addSlideView(BaseSlideView mSlideView, Slide slide) {
        this.mSlideView = mSlideView;
        this.slide = slide;
        addView(mSlideView.getView());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isNeedMyMove;
    }


    public SlideMenu setContentView(View view) {
        contentView = view;
        return this;
    }

    private int mTrackingEdges = EDGE_LEFT;

    /**
     * Enable edge tracking for the selected edges of the parent view.
     * The callback's {@link ViewDragHelper.Callback#onEdgeTouched(int, int)} and
     * {@link ViewDragHelper.Callback#onEdgeDragStarted(int, int)} methods will only be invoked
     * for edges for which edge tracking has been enabled.
     *
     * @param edgeFlags Combination of edge flags describing the edges to watch
     * @see #EDGE_LEFT
     * @see #EDGE_RIGHT
     */
    public void setEdgeTrackingEnabled(int edgeFlags) {
        mTrackingEdges = edgeFlags;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mChildrenCanceledTouch = false;
                final int pointerId = ev.getPointerId(0);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);
                mLastX = x;
                mLastY = y;
                saveInitialMotion(mLastX, mLastY, pointerId);
                if (mActivePointerId != pointerId) {
                    mActivePointerId = pointerId;
                }
                final int edgesTouched = mInitialEdgesTouched[pointerId];
                if ((edgesTouched & mTrackingEdges) != 0) {
                    onEdgeTouched();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int index = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(index);
                final float y = ev.getY(index);
                dx = x - mLastX;
                dy = y - mLastY;
                mLastY = y;
                mLastX = x;
                if (Math.abs(dx) > Math.abs(dy)) {
                    isNeedMyMove = true;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                cancel();
                eventUp();
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int pointerId = ev.getPointerId(pointerIndex);
                mLastX = ev.getX(pointerIndex);
                mLastY = ev.getY(pointerIndex);
                saveInitialMotion(mLastX, mLastY, pointerId);
                if (mActivePointerId != pointerId) {
                    mActivePointerId = pointerId;
                }
                final int edgesTouched = mInitialEdgesTouched[pointerId];
                if ((edgesTouched & mTrackingEdges) != 0) {
                    onEdgeTouched();
                }

                break;
            }
            case MotionEventCompat.ACTION_POINTER_UP: {
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastX = ev.getX(newPointerIndex);
                    mLastY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                clearMotionHistory(pointerId);
                break;
            }
        }
        return super.

                dispatchTouchEvent(ev);
    }

    private void clearMotionHistory(int pointerId) {
        if (mInitialEdgesTouched == null || !isPointerDown(pointerId)) {
            return;
        }
        mInitialEdgesTouched[pointerId] = 0;
        mPointersDown &= ~(1 << pointerId);
    }

    public boolean isPointerDown(int pointerId) {
        return (mPointersDown & 1 << pointerId) != 0;
    }

    public void cancel() {
        mActivePointerId = MotionEvent.INVALID_POINTER_ID;
        isNeedMyMove = false;
        if (mInitialEdgesTouched != null) {
            Arrays.fill(mInitialEdgesTouched, 0);
            mLastY = 0;
            mLastX = 0;
            dx = 0;
            dy = 0;
            mPointersDown = 0;
        }
    }

    private void onEdgeTouched() {
        postDelayed(mPeekRunnable, 160);
    }

    private final Runnable mPeekRunnable = new Runnable() {
        @Override
        public synchronized void run() {
            if (mActivePointerId != MotionEvent.INVALID_POINTER_ID) {
                peekDrawer();
            }
        }
    };

    void peekDrawer() {
        int childLeft = slide == Slide.LEFT ? -mEdgeSize : mEdgeSize;
        int scrollX = getScrollX();
        if (scrollX != 0 || childLeft == scrollX) {
            scroller.abortAnimation();
            return;
        }
        scroller.startScroll(0, 0, childLeft, 0, 400);
        invalidate();
        cancelChildViewTouch();
    }

    boolean mChildrenCanceledTouch = false;

    private void cancelChildViewTouch() {
        // Cancel child touches
        if (!mChildrenCanceledTouch) {
            final long now = SystemClock.uptimeMillis();
            final MotionEvent cancelEvent = MotionEvent.obtain(now, now,
                    MotionEvent.ACTION_CANCEL, 0.0f, 0.0f, 0);
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).dispatchTouchEvent(cancelEvent);
            }
            cancelEvent.recycle();
            mChildrenCanceledTouch = true;
        }
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
            if (x < -mSlideView.getMeasuredWidth()) {
                x = -mSlideView.getMeasuredWidth();
            }
        } else {
            if (x < 0) {
                x = 0;
            }
            if (x > mSlideView.getMeasuredWidth()) {
                x = mSlideView.getMeasuredWidth();
            }
        }
        if (getScrollX() != x) {
            super.scrollTo(x, y);
        }
    }

    private void eventUp() {
        int scrollX = getScrollX();
        if (slide == Slide.LEFT) {
            if (scrollX < -mSlideView.getMeasuredWidth() / 2) {//打开
                scroller.startScroll(scrollX, 0, -(scrollX + mSlideView.getMeasuredWidth()), 0, 400);
                invalidate();
            } else {
                scroller.startScroll(scrollX, 0, -scrollX, 0, 400);
                invalidate();
            }
        } else if (slide == Slide.RIGHT) {
            if (scrollX > mSlideView.getMeasuredWidth() / 2) {//打开
                scroller.startScroll(scrollX, 0, mSlideView.getMeasuredWidth() - scrollX, 0, 400);
                invalidate();
            } else {
                scroller.startScroll(scrollX, 0, -scrollX, 0, 400);
                invalidate();
            }
        }
    }

    public void close() {
        int scrollX = getScrollX();
        scroller.startScroll(scrollX, 0, -scrollX, 0, 400);
        invalidate();

    }

    private void saveInitialMotion(float x, float y, int pointerId) {
        if (mInitialEdgesTouched == null || mInitialEdgesTouched.length <= pointerId) {
            int[] iit = new int[pointerId + 1];
            if (mInitialEdgesTouched != null) {
                System.arraycopy(mInitialEdgesTouched, 0, iit, 0, mInitialEdgesTouched.length);
            }
            mInitialEdgesTouched = iit;
        }
        mInitialEdgesTouched[pointerId] = getEdgesTouched((int) x, (int) y);
    }

    private int getEdgesTouched(int x, int y) {
        int result = 0;
        if (x < getLeft() + mEdgeSize) result |= EDGE_LEFT;
        if (x > getRight() - mEdgeSize) result |= EDGE_RIGHT;
        return result;
    }

    public void open() {
        int scrollX = getScrollX();
        if (slide == Slide.LEFT) {
            scroller.startScroll(scrollX, 0, scrollX + mSlideView.getMeasuredWidth(), 0, 400);
            invalidate();
        } else if (slide == Slide.RIGHT) {
            scroller.startScroll(scrollX, 0, mSlideView.getMeasuredWidth() - scrollX, 0, 400);
            invalidate();
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
        View child = mSlideView.getView();
        if (child != null) {
            if (slide == Slide.LEFT) {
                child.layout(-mSlideView.getMeasuredWidth(), top, 0, bottom);
            } else {
                child.layout(right, top, right + mSlideView.getMeasuredWidth(), bottom);
            }
        }
    }
}
