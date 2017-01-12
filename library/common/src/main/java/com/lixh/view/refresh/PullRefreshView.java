package com.lixh.view.refresh;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.lixh.utils.ULog;
import com.lixh.view.refresh.ImplPull.Direction;
import com.lixh.view.refresh.ImplPull.ScrollState;
import com.lixh.view.refresh.ImplPull.StateType;

/**
 * Created by LIXH on 2017/1/3.
 * email lixhVip9@163.com
 * des
 */

public class PullRefreshView extends ViewGroup {
    protected FooterView mFooter;
    protected HeaderView mHeader;
    protected View mChildView;
    private OnRefreshListener onRefreshListener;
    private OnLoadListener onLoadListener;
    protected boolean isRefreshing;
    protected boolean isLoadMore = true;
    private boolean isLoadMoreing;
    private float springBack = 2.0f;
    private int mMaxHeight = 0;
    private ImplPull implPull;
    CustomHeadView headView;
    CustomFootView footView;
    float mTouchSlop;
    float mCurrentY;
    float mLastY;
    boolean mDragging = false;
    float mLastTouchY;
    ScrollState scrollState = ScrollState.NONE;
    StateType stateType = StateType.NONE;
    Direction direction = Direction.NONE;

    public void setImplPull(ImplPull implPull) {
        this.implPull = implPull;
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
        implPull.onScrollChange(stateType);
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public PullRefreshView(Context context) {
        this(context, null, 0);
    }

    public PullRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Context context = getContext();
        if (headView == null) {
            headView = new CustomHeadView(context);
            addHeaderView(headView);
        }
        if (mChildView == null) {
            mChildView = getChildAt(1);
        }
        if (footView == null) {
            footView = new CustomFootView(context);
            addFootView(footView);
        }

    }

    private void init(Context context, AttributeSet attrs, int defstyleAttr) {
        if (isInEditMode()) {
            return;
        }

        if (getChildCount() > 1) {
            throw new RuntimeException("can only have one child widget");
        }
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();//判断是否点击还是拖拽

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float currentTouchY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchY = currentTouchY;
                direction = Direction.NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = currentTouchY - mLastTouchY;
                mLastTouchY = currentTouchY;
                if (scrollState == ScrollState.TOP || scrollState == ScrollState.BOTTOM) {
                    if (isFinish) {
                        isFinish = false;
                        return resetDispatchTouchEvent(ev);
                    }
                }
                direction = dy > 0 ? Direction.DOWN : Direction.UP;
                break;
        }
        return super.

                dispatchTouchEvent(ev);
    }

    private boolean resetDispatchTouchEvent(MotionEvent ev) {
        MotionEvent newEvent = MotionEvent.obtain(ev);
        ev.setAction(MotionEvent.ACTION_CANCEL);
        dispatchTouchEvent(ev);
        newEvent.setAction(MotionEvent.ACTION_DOWN);
        return dispatchTouchEvent(newEvent);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ULog.e("onInterceptTouchEvent");
        if (isRefreshing || isLoadMoreing) return true;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCurrentY = ev.getY();
                mLastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getY();
                float dy = currentY - mLastY;
                if (!mDragging && Math.abs(dy) > mTouchSlop) {
                    mDragging = true;
                }
                if (mDragging) {
                    if (dy > 0 && !CanPullUtil.canChildScrollUp(mChildView)) {
                        if (mHeader != null) {
                            scrollState = ScrollState.TOP;
                            setImplPull(mHeader);

                        }
                        return true;
                    } else if (dy < 0 && !CanPullUtil.canChildScrollDown(mChildView) && isLoadMore) {
                        if (mFooter != null) {
                            scrollState = ScrollState.BOTTOM;
                            setImplPull(mFooter);
                        }
                        return true;
                    }
                }
                break;

        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        ULog.e("onTouchEvent");
        mMaxHeight = (int) (implPull.getHeight() * springBack);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCurrentY = e.getY();
                mLastY = mCurrentY;
                return true;
            case MotionEvent.ACTION_MOVE:
                mCurrentY = e.getY();
                float dy = mCurrentY - mLastY;
                if (scrollState == ScrollState.TOP || scrollState == ScrollState.BOTTOM) {
                    setStateType(StateType.PULL);
                    scrollBy(0, (int) -dy);
                } else if (isRefreshing||isLoadMoreing){
                    setStateType(StateType.PULL);
                    scrollBy(0, (int) -dy);
                }
                mLastY = mCurrentY;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mDragging = false;
                eventUp();
                break;
        }

        return true;

    }

    public void eventUp() {
        int tempHeight = 0;
        if (stateType == StateType.RELEASE) {
            if (scrollState == ScrollState.TOP) {
                tempHeight = -implPull.getHeight();
                updateListener();
            } else if (scrollState == ScrollState.BOTTOM) {
                tempHeight = implPull.getHeight();
                updateLoadMoreing();
            }

            super.scrollTo(0, tempHeight);
        } else {
            if (scrollState == ScrollState.BOTTOM) {
                if (mChildView instanceof AbsListView) {
                    ((ListView) mChildView).smoothScrollBy(getScrollY(),0);
                }else if (mChildView instanceof RecyclerView){
                    ((RecyclerView) mChildView).scrollBy(0,getScrollY());
                }
            }
            super.scrollTo(0, 0);

        }


    }

    private void updateLoadMoreing() {
        isLoadMoreing = true;
        if (onLoadListener != null) {
            setStateType(StateType.LOADING);
            onLoadListener.onLoad();
        }
    }

    boolean isFinish = false;

    public void setLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
    }

    @Override
    public void scrollTo(int x, int y) {
        implPull.Scroll(mMaxHeight, y);
        if (scrollState == ScrollState.TOP & y < -implPull.getHeight() || scrollState == ScrollState.BOTTOM && y > implPull.getHeight()) {
            setStateType(StateType.RELEASE);
        }
        if (scrollState == ScrollState.TOP) {
            if (y < -mMaxHeight) {
                y = -mMaxHeight;
            }
            if (y >= 0) {
                y = 0;
                isFinish = direction == Direction.UP ? true : false;
            }
        } else if (scrollState == ScrollState.BOTTOM) {
            if (y > mMaxHeight) {
                y = mMaxHeight;
            }
            if (y <= 0) {
                y = 0;
                isFinish = direction == Direction.DOWN ? true : false;
            }
        }

        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }
    }


    public void updateListener() {
        isRefreshing = true;
        if (onRefreshListener != null) {
            setStateType(StateType.LOADING);
            onRefreshListener.onRefresh();
        }

    }

    private void upLoadMoreListener() {
        isLoadMoreing = true;
        if (onLoadListener != null) {
            setStateType(StateType.LOADING);
            onLoadListener.onLoad();
        }
    }

    public void finishRefresh() {
        setStateType(StateType.LOAD_CLOSE);
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                isRefreshing = false;
                if (mHeader != null) {
                    eventUp();
                    setStateType(StateType.NONE);
                }
            }
        }, 400);
    }

    public void finishLoadMore() {
        setStateType(StateType.LOAD_CLOSE);
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mFooter != null && isLoadMoreing) {
                    isLoadMoreing = false;
                    eventUp();
                    setStateType(StateType.NONE);
                }
            }
        }, 400);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mHeader != null) {
            mHeader.getView().layout(0, 0 - mHeader.getHeight(), mChildView.getRight(), 0);
        }
        if (mChildView != null) {
            mChildView.layout(0, 0, r, mChildView.getMeasuredHeight());
        }
        if (mFooter != null) {
            mFooter.getView().layout(0, b, r, mFooter.getHeight() + b);
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


    public static abstract class HeaderView implements ImplPull {
        public View headerView;
        Context context;

        public View getView() {
            return headerView;
        }

        public HeaderView(Context context) {
            this.context = context;
            headerView = LayoutInflater.from(context).inflate(getLayoutId(), null);
            setLayoutParams();
            initView();
        }

        public int getHeight() {
            headerView.measure(0, 0);
            return headerView.getMeasuredHeight();
        }

        protected <VT extends View> VT $(@IdRes int id) {
            return (VT) headerView.findViewById(id);
        }

        public void setLayoutParams() {
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            headerView.setLayoutParams(lp);
        }


    }

    public static abstract class FooterView implements ImplPull {
        public View footerView;
        Context context;

        public View getView() {
            return footerView;
        }

        public FooterView(Context context) {
            this.context = context;
            footerView = LayoutInflater.from(context).inflate(getLayoutId(), null);
            setLayoutParams();
            initView();
        }

        protected <VT extends View> VT $(@IdRes int id) {
            return (VT) footerView.findViewById(id);
        }

        public int getHeight() {
            footerView.measure(0, 0);
            return footerView.getMeasuredHeight();
        }


        public void setLayoutParams() {
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            footerView.setLayoutParams(layoutParams);
        }
    }
}
