package com.lixh.view.refresh;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.OverScroller;

import com.lixh.view.refresh.ImplPull.ScrollState;
import com.lixh.view.refresh.ImplPull.StateType;

/**
 * Created by LIXH on 2017/1/3.
 * email lixhVip9@163.com
 * des
 */

public class SpringView extends ViewGroup {
    protected FooterView mFooter;
    protected HeaderView mHeader;
    protected View mChildView;
    //最大拉动距离，拉动距离越靠近这个值拉动就越缓慢
    private int MAX_HEADER_PULL_HEIGHT = 600;
    private int MAX_FOOTER_PULL_HEIGHT = 600;
    private OnRefreshListener onRefreshListener;
    private OnLoadListener onLoadListener;
    private ImplPull implPull;
    CustomHeadView headView;
    CustomFootView footView;
    float mLastY;
    boolean isChangeFocus = false;
    private final double MOVE_PARA = 2;
    boolean isNeedMyMove;
    boolean needResetAnim;
    ScrollState scrollState = ScrollState.NONE;
    StateType stateType = StateType.NONE;
    private int MOVE_TIME = 400;
    public void setImplPull(ImplPull implPull) {
        this.implPull = implPull;
    }

    public void setScrollState(ScrollState scrollState) {
        this.scrollState = scrollState;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadListener {
        void onLoad();
    }

    public void setOnLoadListener(OnLoadListener onRefreshListener) {
        this.onLoadListener = onRefreshListener;
    }

    public void setStateType(StateType stateType) {
        this.stateType = stateType;
        if (implPull != null) {
            implPull.onScrollChange(stateType);
        }
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public SpringView(Context context) {
        this(context, null, 0);
    }

    public SpringView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpringView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        if (mHeader != null) {
            int th = mHeader.getDragMaxHeight();
            MAX_HEADER_PULL_HEIGHT = th > 0 ? th : MAX_HEADER_PULL_HEIGHT;
        }
        if (mFooter != null) {
            int bh = mHeader.getDragMaxHeight();
            MAX_HEADER_PULL_HEIGHT = bh > 0 ? bh : MAX_HEADER_PULL_HEIGHT;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    boolean isFirstLoad = true;
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Context context = getContext();
        if (headView == null && onRefreshListener != null) {
            headView = new CustomHeadView(context);
            addHeaderView(headView);
        }
        if (mChildView == null) {
            mChildView = getChildAt(getChildCount() - 1);
        }
        if (footView == null && onLoadListener != null) {
            footView = new CustomFootView(context);
            addFootView(footView);
        }
        setImplPull(mHeader);
        if (autoRefresh && isFirstLoad) {
            needResetAnim = true;
            isFirstLoad = false;
            updating();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    private OverScroller mScroller;
    private void init(Context context, AttributeSet attrs, int defstyleAttr) {
        if (isInEditMode()) {
            return;
        }

        if (getChildCount() > 1) {
            throw new RuntimeException("can only have one child widget");
        }
        mScroller = new OverScroller(context);
    }

    public boolean isTop() {
        return !CanPullUtil.canChildScrollUp(mChildView);
    }

    public boolean isBottom() {
        return !CanPullUtil.canChildScrollDown(mChildView);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        dealMulTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                scrollState = ScrollState.NONE;
                isNeedMyMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                isNeedMyMove = isNeedMyMove();
                if (isNeedMyMove && !isChangeFocus) {
                    isChangeFocus = true;
                    return resetDispatchTouchEvent(ev);
                }
                break;
            case MotionEvent.ACTION_UP:
                needResetAnim = true;
                break;
        }
        return super.

                dispatchTouchEvent(ev);
    }

    private boolean resetDispatchTouchEvent(MotionEvent ev) {
        ev.setAction(MotionEvent.ACTION_CANCEL);
        MotionEvent newEvent = MotionEvent.obtain(ev);
        dispatchTouchEvent(ev);
        newEvent.setAction(MotionEvent.ACTION_DOWN);
        return dispatchTouchEvent(newEvent);
    }

    /**
     * 判断是否需要由该控件来控制滑动事件
     */
    private boolean isNeedMyMove() {
        if (Math.abs(dy) < Math.abs(dx)) {
            return false;
        }
        if (mHeader != null) {
            if (dy > 0 && isTop() || getScrollY() < 0 - 20) {
                scrollState = ScrollState.TOP;
                setImplPull(mHeader);
                return true;
            }
        }
        if (mFooter != null) {
            if (dy < 0 && isBottom() || getScrollY() > 0 + 20) {
                scrollState = ScrollState.BOTTOM;
                setImplPull(mFooter);
                return true;
            }
        }
        return false;
    }


    private float dy;
    private float dx;
    private float mLastX;


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
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isNeedMyMove;
    }

    boolean autoRefresh = true;


    /**
     * 第一次加载时自动下拉刷新
     *
     * @param autoRefresh
     */
    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
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
                eventUp();
                break;
        }

        return true;

    }

    private void doMove() {

        //根据下拉高度计算位移距离，（越拉越慢）
        int moveX = 0;
        if (dy > 0) {
            moveX = (int) ((float) ((MAX_HEADER_PULL_HEIGHT + getScrollY()) / (float) MAX_HEADER_PULL_HEIGHT) * dy / MOVE_PARA);
        } else {
            moveX = (int) ((float) ((MAX_FOOTER_PULL_HEIGHT - getScrollY()) / (float) MAX_FOOTER_PULL_HEIGHT) * dy / MOVE_PARA);
        }
        scrollBy(0, (int) (-moveX));
    }

    public void eventUp() {
        if (stateType == StateType.RELEASE) {
            if (scrollState == ScrollState.TOP) {
                updating();
            } else if (scrollState == ScrollState.BOTTOM) {
                upLoading();
            }
        } else {
            dy = 0;
            if (scrollState == ScrollState.BOTTOM) {

                if (mChildView instanceof AbsListView) {
                    ((ListView) mChildView).smoothScrollBy(getScrollY(), 0);
                } else if (mChildView instanceof RecyclerView) {
                    ((RecyclerView) mChildView).scrollBy(0, getScrollY());
                }
            }
            mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), MOVE_TIME);
            invalidate();

        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            super.scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }
    @Override
    public void scrollTo(int x, int y) {
        if (implPull == null) {
            return;
        }
        implPull.Scroll(MAX_HEADER_PULL_HEIGHT, y);
        if (scrollState == ScrollState.TOP) {
            if (y > -implPull.getHeight()) {
                setStateType(StateType.PULL);
            } else {
                setStateType(StateType.RELEASE);
            }
        } else if (scrollState == ScrollState.BOTTOM) {
            if (y < implPull.getHeight()) {
                setStateType(StateType.PULL);
            } else {
                setStateType(StateType.RELEASE);
            }
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }
    }


    public void updating() {
        if (onRefreshListener != null) {
            setStateType(StateType.LOADING);
            scrollState = ScrollState.NONE;
            mScroller.startScroll(0, getScrollY(), 0, -getScrollY() - implPull.getHeight(), MOVE_TIME);
            onRefreshListener.onRefresh();
            invalidate();
        }

    }

    public void upLoading() {
        if (onLoadListener != null) {
            setStateType(StateType.LOADING);
            scrollState = ScrollState.NONE;
            mScroller.startScroll(0, getScrollY(), 0, -getScrollY() + implPull.getHeight(), MOVE_TIME);
            onLoadListener.onLoad();
            invalidate();
        }

    }

    public void finishRefreshAndLoadMore() {
        setStateType(StateType.LOAD_CLOSE);
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (needResetAnim) {
                    eventUp();
                    setStateType(StateType.NONE);
                }
            }
        }, 400);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mHeader != null && onRefreshListener != null) {
            mHeader.getView().layout(0, 0 - mHeader.getHeight(), r, 0);
        }
        if (mChildView != null) {
            mChildView.layout(0, 0, r, mChildView.getMeasuredHeight());
        }
        if (mFooter != null && onLoadListener != null) {
            mFooter.getView().layout(0, mChildView.getMeasuredHeight(), r, mFooter.getHeight() + mChildView.getMeasuredHeight());
        }
    }

    public void addFootView(FooterView mFooter) {
        this.mFooter = mFooter;
        addView(mFooter.getView(), getChildCount());
    }

    public void addHeaderView(HeaderView mHeader) {
        this.mHeader = mHeader;
        addView(mHeader.getView(), 0);
    }
}
