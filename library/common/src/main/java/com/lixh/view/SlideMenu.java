package com.lixh.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import com.lixh.utils.ULog;
import com.nineoldandroids.view.ViewHelper;

import java.util.Arrays;

/**
 * Created by LIXH on 2017/5/8.
 * email lixhVip9@163.com
 * des
 */

public class SlideMenu extends FrameLayout {
    private static final int EDGE_SIZE = 20; // dp
    /**
     * default foreground color
     */
    private static String DEFAULT_COLOR = "#1f1f1f";
    /**
     * 处理多点触控的情况，准确地计算Y坐标和移动距离dy
     * 同时兼容单点触控的情况
     */
    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;
    /**
     * Edge flag indicating that the left edge should be affected.
     */
    public static final int EDGE_LEFT = 1 << 0;
    /**
     * Edge flag indicating that the right edge should be affected.
     */
    public static final int EDGE_RIGHT = 1 << 1;
    private VelocityTracker mVelocityTracker;
    private int collapseOffset = 200;
    private int line = 5;//将横屏分为5份
    float scale;//滑动时的收缩比例
    // Current drag state; idle, dragging or settling
    private int mDragState;
    /**
     * A view is not currently being dragged or animating as a result of a fling/snap.
     */
    public static final int STATE_IDLE = 0;

    /**
     * A view is currently being dragged. The position is currently changing as a result
     * of user input or simulated user input.
     */
    public static final int STATE_DRAGGING = 1;

    /**
     * A view is currently settling into place as a result of a fling or
     * predefined non-interactive motion.
     */
    public static final int STATE_SETTLING = 2;
    @Slide
    int slide = Slide.LEFT;
    @State
    int slideState = State.CLOSE;
    View contentView;
    View slideView;
    int slideWidth;
    boolean isFollowing;
    boolean isAnim;
    OverScroller scroller;
    private int mEdgeSize;
    private int mPointersDown;
    BaseSlideView mSlideView;
    private boolean mEnable = true;//是否需要边缘触控    跟随移动时为false
    boolean canDrag;//边缘触控未唤醒时是否允许拖拽
    private int[] mInitialEdgesTouched;
    private float dx;
    private float dy;

    public void attachToActivity(Activity activity) {
        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decor.removeView(decorChild);
        addView(decorChild);
        setContentView(decorChild);
        decor.addView(this);
    }

    public void setEdgeEnable(boolean enable) {
        mEnable = enable;
    }

    private onSlideListener slideListener;

    public int getSlideWidth() {
        return slideWidth;
    }

    public interface onSlideListener {
        void SlideState(@State int state);
    }

    public void setonSlideListener(onSlideListener slideListener) {
        this.slideListener = slideListener;
    }

    float mLastX;
    float mLastY;

    public int getSlideState() {
        return slideState;
    }

    public void setSlideState(int slideState) {
        this.slideState = slideState;
        if (slideListener != null) {
            slideListener.SlideState(slideState);
        }
    }

    @IntDef({
            Slide.NONE,
            Slide.LEFT,
            Slide.RIGHT
    }
    )
    public @interface Slide {
        int NONE = 0;
        int LEFT = 1;
        int RIGHT = 2;
    }

    @IntDef({
            State.OPEN,
            State.CLOSE,
    }
    )
    public @interface State {
        int OPEN = 0;
        int CLOSE = 1;
    }

    /**
     * 跟随移动时
     *
     * @param isFollowing
     */
    public void setFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
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
        setForeground(new ColorDrawable(Color.parseColor(DEFAULT_COLOR)));
        getForeground().setAlpha(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        collapseOffset = widthSize / line;
        scale = 1f / (line - 1);
        if (mSlideView != null && mSlideView.isFullScreen()) {
            collapseOffset = 0;
        }
        if (slideView != null) {
            slideWidth = widthSize - collapseOffset;
            slideView.getLayoutParams().width = slideWidth;

        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }


    public void addSlideView(BaseSlideView mSlideView, @Slide int slide) {
        this.mSlideView = mSlideView;
        mSlideView.setSlideMenu(this);
        slideView = mSlideView.getView();
        this.slide = slide;
        mTrackingEdges = slide == Slide.LEFT ? EDGE_LEFT : EDGE_RIGHT;
        isFollowing = mSlideView.isFollowing();
        mEnable = mSlideView.isEnabledEdge();
        isAnim = mSlideView.isAnim();
        addView(slideView);
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

    boolean isMove;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        boolean interceptForTap = false;
        initVelocityTracker(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                interceptForTap = false;
                isMove = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (Math.abs(dx) > Math.abs(dy)) {
                    removeCallbacks(mPeekRunnable);
                    if (isFollowing || slideState == State.OPEN) {
                        isMove = true;
                    }
                } else {
                    if (slideState == State.OPEN) {
                        cancelChildViewTouch();
                    }

                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (slideState == State.OPEN && ((slide == Slide.RIGHT && ev.getX() <= collapseOffset) || (slide == Slide.LEFT && ev.getX() >= slideWidth))) {
                    cancelChildViewTouch();
                    scrollBy((int) -dx, 0);
                    close();
                }
                mChildrenCanceledTouch = false;
            }
            break;
        }

        return interceptForTap || canDrag || isMove;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mChildrenCanceledTouch = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mEnable) {//是否允许边缘触控
                    if (canDrag || slideState == State.OPEN || isFollowing) {
                        scrollBy((int) -dx, 0);
                    }
                } else {
                    scrollBy((int) -dx, 0);
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                removeCallbacks(mPeekRunnable);
                mChildrenCanceledTouch = false;
                canDrag = false;
                eventUp();
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
            }
            break;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        initVelocityTracker(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mChildrenCanceledTouch = false;
                if (mVelocityTracker != null) {
                    // Add a user's movement to the tracker.
                    mVelocityTracker.addMovement(ev);
                }
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
                if ((edgesTouched & mTrackingEdges) != 0 && mEnable) {
                    onEdgeTouched();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mVelocityTracker != null) {
                    // Add a user's movement to the tracker.
                    mVelocityTracker.addMovement(ev);
                }
                final int index = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(index);
                final float y = ev.getY(index);
                dx = x - mLastX;
                dy = y - mLastY;
                mLastY = y;
                mLastX = x;

                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                cancel();
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
                if ((edgesTouched & mTrackingEdges) != 0 && mEnable) {
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

    private void initVelocityTracker(MotionEvent motionEvent) {
        if (mVelocityTracker == null) {
            // Retrieve a new VelocityTracker object to watch the velocity of a motion.
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private static final int SNAP_VELOCITY = 600;

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
        canDrag = true;
        postDelayed(mPeekRunnable, 160);
    }

    private final Runnable mPeekRunnable = new Runnable() {
        @Override
        public synchronized void run() {
            peekDrawer();
        }
    };

    void peekDrawer() {
        int childLeft = slide == Slide.LEFT ? -mEdgeSize : mEdgeSize;
        int scrollX = getScrollX();
        if (scrollX != 0 || childLeft == scrollX) {
            scroller.abortAnimation();
            return;
        }
        scroller.startScroll(0, 0, scrollX + childLeft, 0, 400);
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

    @SuppressWarnings("Range")
    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        ULog.e(x + "");
        if (slide == Slide.LEFT) {
            if (x >= 0) {
                x = 0;
                setSlideState(State.CLOSE);

            }
            if (x <= -slideWidth) {
                x = -slideWidth;
                setSlideState(State.OPEN);
            }
        } else {
            if (x <= 0) {
                x = 0;
                setSlideState(State.CLOSE);
            }
            if (x >= slideWidth) {
                x = slideWidth;
                setSlideState(State.OPEN);
            }
        }
        getForeground().setAlpha(Math.abs(x) / 10);
        if (!isFollowing) {
            ViewHelper.setTranslationX(contentView, x * 1f);
        } else {
            if (isAnim) {
                ViewHelper.setTranslationX(slideView, x * scale);
            }
        }
        if (getScrollX() != x) {
            super.scrollTo(x, y);
        }
    }

    private void eventUp() {
        int scrollX = getScrollX();
        final VelocityTracker velocityTracker = mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000);
        int velocityX = (int) velocityTracker.getXVelocity();
        if (slide == Slide.LEFT) {
            if (velocityX > SNAP_VELOCITY) {
                open();
            } else if (velocityX < -SNAP_VELOCITY) {
                close();
            } else {
                if (scrollX < -slideWidth / 2) {//打开
                    open();
                } else {
                    close();
                }
            }
        } else if (slide == Slide.RIGHT) {
            if (velocityX > SNAP_VELOCITY) {
                close();
            } else if (velocityX < -SNAP_VELOCITY) {
                open();
            } else {
                if (scrollX > slideWidth / 2) {//打开
                    open();
                } else {
                    close();
                }
            }

        }
    }

    public void open() {
        int scrollX = getScrollX();
        if (slide == Slide.LEFT) {
            scroller.startScroll(scrollX, 0, -(scrollX + slideWidth), 0, 400);
            invalidate();
        } else if (slide == Slide.RIGHT) {
            scroller.startScroll(scrollX, 0, slideWidth - scrollX, 0, 400);
            invalidate();
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

    //边缘检测
    private int getEdgesTouched(int x, int y) {
        int result = 0;
        if (x < getLeft() + mEdgeSize) result |= EDGE_LEFT;
        if (x > getRight() - mEdgeSize) result |= EDGE_RIGHT;
        return result;
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
        if (slideView != null) {
            if (slide == Slide.LEFT) {
                slideView.layout(isAnim && isFollowing ? -slideWidth + collapseOffset : -slideWidth, top, isAnim && isFollowing ? collapseOffset : 0, bottom);
            } else {
                slideView.layout(right, top, right + slideWidth, bottom);
            }
        }
        if (!isFollowing) {
            slideView.bringToFront();
        } else {
            contentView.bringToFront();
        }
        contentView.setClickable(true);
    }
}
