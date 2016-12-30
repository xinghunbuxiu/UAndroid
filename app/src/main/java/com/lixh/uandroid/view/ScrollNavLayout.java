package com.lixh.uandroid.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import com.lixh.uandroid.R;
import com.lixh.utils.ULog;
import com.nineoldandroids.view.ViewHelper;

/**
 * 总系数为1f
 * 1f/（行数- 停留的行数) 例如GridView 4行 要想停留一行
 * 1f/(4-1)
 * 从下往上
 */

public class ScrollNavLayout extends LinearLayout implements NestedScrollingParent {
    private static final String TAG = "ScrollNavLayout";
    View target;
    Direction direction = Direction.NONE;
    int line = 4;//行数
    int location = 3;//当前行数
    float scale = 1f;  // 重点 收缩的比例
    boolean isEnabled = true;//是否允许滑动
    int collapseOffset = 0; //阻尼的高度
    int lineHeight = 0;//当前1行的高度

    public enum Direction {
        DOWN, UP, NONE
    }

    private PanelListener panelListener;
    private PanelState panelState = PanelState.EXPANDED;

    public enum PanelState {
        COLLAPSED(0),
        SILIDING(1),
        EXPANDED(2);
        private int asInt;

        PanelState(int i) {
            this.asInt = i;
        }

        static PanelState fromInt(int i) {
            switch (i) {
                case 0:
                    return COLLAPSED;
                case 1:
                default:
                    return SILIDING;
                case 2:
                    return EXPANDED;


            }
        }


        public int toInt() {
            return asInt;
        }
    }

    public interface PanelListener {
        void onPanelStateChanged(PanelState panelState);
    }

    /**
     * Setup the drag listener.
     *
     * @return SetupWizard
     */
    public ScrollNavLayout setPanelListener(PanelListener panelListener) {
        this.panelListener = panelListener;
        return this;
    }

    /**
     * 设置当前的行数
     *
     * @param line
     */
    public void setLine(int line) {
        scale = 1f / (line - 1);
    }

    /**
     * 第几行
     *
     * @param position
     */
    public void setLocation(int position) {
        location = position;
        scale = location * scale;
    }

    //关闭
    public void closeTopView() {
        panelState = PanelState.COLLAPSED;
        mScroller.startScroll(0, getScrollY(), 0, mTopViewHeight - collapseOffset, 400);
        invalidate();

    }

    //打开
    public void openTopView() {
        panelState = PanelState.EXPANDED;
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 400);
        invalidate();
        if (panelListener != null) {
            panelListener.onPanelStateChanged(panelState);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState state = new SavedState(superState);
        state.scrollY = getScrollY();
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (!(state instanceof SavedState)) {
            // FIX #10
            super.onRestoreInstanceState(BaseSavedState.EMPTY_STATE);
            return;
        }
        SavedState s = (SavedState) state;
        super.onRestoreInstanceState(s.getSuperState());
        scrollTo(0, s.scrollY);
    }

    /**
     * Save the instance state
     */
    @SuppressLint("ParcelCreator")
    private static class SavedState extends BaseSavedState {
        int scrollY = 0;

        SavedState(Parcelable superState) {
            super(superState);
        }

    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        this.target = target;
        direction = Direction.NONE;
        return true;

    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {

    }

    @Override
    public void onStopNestedScroll(View target) {
        stopScroll();
    }


    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        Log.e(TAG, "onNestedPreScroll" + dy);
        if (dy > 0) {
            //记录方向是向上滑动
            direction = Direction.UP;
            //记录方向是向上滑动
            if (getScrollY() >= mTopViewHeight - collapseOffset) {
                isEnabled = false;
            }
        } else {
            //记录方向是向下滑动
            direction = Direction.DOWN;
            if (!ViewCompat.canScrollVertically(target, -1)) {
                //记录方向是向下滑动
                isEnabled = true;
            }
        }

        if (isEnabled) {
            scrollBy(0, dy);
            consumed[1] = dy;

        }

    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        //down - //up+
        if (getScrollY() >= mTopViewHeight - collapseOffset) return false;
        fling((int) velocityY);
        return true;
    }


    public void stopScroll() {
        ULog.e(getScrollY() + "111111" + panelState.toInt());
        ULog.e(lineHeight / 2 * location + "");
        if (direction == Direction.NONE || direction == Direction.UP && panelState == PanelState.COLLAPSED || direction == Direction.DOWN && panelState == PanelState.EXPANDED)
            return;
        if (getScrollY() < lineHeight / 2 * location) {
            openTopView();
        } else {
            closeTopView();
        }
        invalidate();
    }

    @Override
    public int getNestedScrollAxes() {
        return 0;
    }

    private View mTop;
    private View mBottom;
    private int mTopViewHeight;
    private OverScroller mScroller;
    private int topViewId = -1;
    private int bottomViewId = -1;

    public ScrollNavLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StickyNavLayout);
        topViewId = a.getResourceId(R.styleable.StickyNavLayout_topView, -1);
        bottomViewId = a.getResourceId(R.styleable.StickyNavLayout_bottomView, -1);
        mScroller = new OverScroller(context);//内置滚动
    }


    public void setCollapseOffset(int collapseOffset) {
        this.collapseOffset = collapseOffset;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() < 2) {
            throw new RuntimeException("Content view must contains two child views at least.");
        }

        if (topViewId != -1 || bottomViewId != -1) {
            throw new IllegalArgumentException("You have set \"topViewId\" , \"navViewId\"bottomViewId");
        }

        if (bottomViewId != -1 && topViewId != -1 && bottomViewId != -1) {
            bindId(this);
        } else {
            mTop = getChildAt(0);
            mBottom = getChildAt(1);
        }
        Log.e(TAG, "onFinishInflate");
    }

    private void bindId(View view) {
        mTop = view.findViewById(topViewId);
        mBottom = view.findViewById(bottomViewId);
        if (!(mBottom instanceof ViewPager)) {
            throw new RuntimeException(
                    "id_stickynavlayout_viewpager show used by ViewPager !");
        }

        if (mBottom == null) {
            throw new IllegalArgumentException("\"bottomViewId\" with id = \"@id/"
                    + getResources().getResourceEntryName(bottomViewId)
                    + "\" has NOT been found. Is a child with that id in this "
                    + getClass().getSimpleName()
                    + "?");
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //不限制顶部的高度
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isInEditMode()) {
            mTop.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            ViewGroup.LayoutParams params = mBottom.getLayoutParams();
            params.height = getMeasuredHeight()+200;
            setMeasuredDimension(getMeasuredWidth(), mTop.getMeasuredHeight() + mBottom.getMeasuredHeight()+200);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTopViewHeight = mTop.getMeasuredHeight();
        lineHeight = mTopViewHeight / line;
        collapseOffset = lineHeight;

    }

    public interface OnScrollListener {
        void onScroll(int currentX, int currentY);

    }

    private OnScrollListener onScrollListener;

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public void fling(int velocityY) {
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mTopViewHeight);
        if (direction == Direction.UP && panelState != PanelState.COLLAPSED) {
            closeTopView();
        } else if (direction == Direction.DOWN && panelState != PanelState.EXPANDED) {
            openTopView();
        }

    }


    @Override
    public void scrollTo(int x, int y) {
        panelState = PanelState.SILIDING;
        if (y < 0) {
            y = 0;
        }
        if (y >= mTopViewHeight - collapseOffset) {
            y = mTopViewHeight - collapseOffset;
        }

        if (getScrollY() == mTopViewHeight - collapseOffset) {
            panelState = PanelState.COLLAPSED;
        }
        if (getScrollY() <= 0) {
            panelState = PanelState.EXPANDED;

        }

        if (onScrollListener != null) {
            onScrollListener.onScroll(x, y);
        }
        ViewHelper.setTranslationY(mTop, y * scale);
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }

    }

}


