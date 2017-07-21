package com.lixh.view.multi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.VisibleForTesting;
import android.support.v4.os.TraceCompat;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LIXH on 2017/7/21.
 * email lixhVip9@163.com
 * des
 */

public class MultiLayoutView extends ViewGroup {
    public static final int INVALID_TYPE = -1;
    private static final String TAG = "MultiLayoutView";
    static final String TRACE_CREATE_VIEW_TAG = "RV CreateView";
    @VisibleForTesting
    final LongSparseArray<RecyclerView.ViewHolder> mOldChangedHolders = new LongSparseArray<>();
    final ArrayList<RecyclerView.ItemDecoration> mItemDecorations = new ArrayList<>();

    public MultiLayoutView(Context context) {
        super(context);
    }

    public MultiLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        dispatchLayout();
    }

    Adapter mAdapter;

    void dispatchLayout() {
        if (mAdapter == null) {
            Log.e(TAG, "No adapter attached; skipping layout");
            // leave the state in START
            return;
        }
        for (int i = getChildCount() - 1; i >= 0; i--) {
            ViewHolder holder = getChildViewHolderInt(getChildAt(i));
        }

    }

    static ViewHolder getChildViewHolderInt(View child) {
        if (child == null) {
            return null;
        }
        return ((LayoutParams) child.getLayoutParams()).mViewHolder;
    }

    public void bindViewToPosition(View view, int position) {
        ViewHolder holder = getChildViewHolderInt(view);
        if (holder == null) {
            throw new IllegalArgumentException("The view does not have a ViewHolder. You cannot"
                    + " pass arbitrary views to this method, they should be created by the "
                    + "Adapter");
        }
        final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        final LayoutParams rvLayoutParams;
        if (lp == null) {
            rvLayoutParams = (LayoutParams) generateDefaultLayoutParams();
            holder.itemView.setLayoutParams(rvLayoutParams);
        } else if (!checkLayoutParams(lp)) {
            rvLayoutParams = (LayoutParams) generateLayoutParams(lp);
            holder.itemView.setLayoutParams(rvLayoutParams);
        } else {
            rvLayoutParams = (LayoutParams) lp;
        }
        rvLayoutParams.mViewHolder = holder;

    }

    final Rect mTempRect = new Rect();

    /**
     * {@link android.view.ViewGroup.MarginLayoutParams LayoutParams} subclass for children of
     * {@link RecyclerView}. Custom {@link RecyclerView.LayoutManager layout managers} are encouraged
     * to create their own subclass of this <code>LayoutParams</code> class
     * to store any additional required per-child view metadata about the layout.
     */
    public static class LayoutParams extends android.view.ViewGroup.MarginLayoutParams {
        ViewHolder mViewHolder;
        final Rect mDecorInsets = new Rect();
        boolean mInsetsDirty = true;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super((ViewGroup.LayoutParams) source);
        }

    }

    public void measureChild(View child, int widthUsed, int heightUsed) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        final Rect insets = getItemDecorInsetsForChild(child);
        widthUsed += insets.left + insets.right;
        heightUsed += insets.top + insets.bottom;
        final int widthSpec = getChildMeasureSpec(getWidth(), getWidthMode(),
                getPaddingLeft() + getPaddingRight() + widthUsed, lp.width,
                canScrollHorizontally());
        final int heightSpec = getChildMeasureSpec(getHeight(), getHeightMode(),
                getPaddingTop() + getPaddingBottom() + heightUsed, lp.height,
                canScrollVertically());
        if (shouldMeasureChild(child, widthSpec, heightSpec, lp)) {
            child.measure(widthSpec, heightSpec);
        }
    }
    /**
     * Query if vertical scrolling is currently supported. The default implementation
     * returns false.
     *
     * @return True if this LayoutManager can scroll the current contents vertically
     */
    public boolean canScrollVertically() {
        return false;
    }

    boolean shouldMeasureChild(View child, int widthSpec, int heightSpec, LayoutParams lp) {
        return child.isLayoutRequested()
                || !isMeasurementUpToDate(child.getWidth(), widthSpec, lp.width)
                || !isMeasurementUpToDate(child.getHeight(), heightSpec, lp.height);
    }

    private static boolean isMeasurementUpToDate(int childSize, int spec, int dimension) {
        final int specMode = MeasureSpec.getMode(spec);
        final int specSize = MeasureSpec.getSize(spec);
        if (dimension > 0 && childSize != dimension) {
            return false;
        }
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                return true;
            case MeasureSpec.AT_MOST:
                return specSize >= childSize;
            case MeasureSpec.EXACTLY:
                return specSize == childSize;
        }
        return false;
    }

    private int mWidthMode = MeasureSpec.EXACTLY;
    private int mHeightMode = MeasureSpec.EXACTLY;

    public int getWidthMode() {
        return mWidthMode;
    }

    public int getHeightMode() {
        return mHeightMode;
    }

    public static abstract class ItemDecoration {


        public void onDraw(Canvas c, MultiLayoutView parent) {
        }


        public void onDrawOver(Canvas c, MultiLayoutView parent) {
        }

        @Deprecated
        public void getItemOffsets(Rect outRect, int itemPosition, MultiLayoutView parent) {
            outRect.set(0, 0, 0, 0);
        }
    }

    Rect getItemDecorInsetsForChild(View child) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (!lp.mInsetsDirty) {
            return lp.mDecorInsets;
        }

        final Rect insets = lp.mDecorInsets;
        insets.set(0, 0, 0, 0);
        final int decorCount = mItemDecorations.size();
        for (int i = 0; i < decorCount; i++) {
            mTempRect.set(0, 0, 0, 0);
            mItemDecorations.get(i).getItemOffsets(mTempRect, child, this, mState);
            insets.left += mTempRect.left;
            insets.top += mTempRect.top;
            insets.right += mTempRect.right;
            insets.bottom += mTempRect.bottom;
        }
        lp.mInsetsDirty = false;
        return insets;
    }

    public static int getChildMeasureSpec(int parentSize, int parentMode, int padding,
                                          int childDimension, boolean canScroll) {
        int size = Math.max(0, parentSize - padding);
        int resultSize = 0;
        int resultMode = 0;
        if (canScroll) {
            if (childDimension >= 0) {
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == RecyclerView.LayoutParams.MATCH_PARENT) {
                switch (parentMode) {
                    case MeasureSpec.AT_MOST:
                    case MeasureSpec.EXACTLY:
                        resultSize = size;
                        resultMode = parentMode;
                        break;
                    case MeasureSpec.UNSPECIFIED:
                        resultSize = 0;
                        resultMode = MeasureSpec.UNSPECIFIED;
                        break;
                }
            } else if (childDimension == RecyclerView.LayoutParams.WRAP_CONTENT) {
                resultSize = 0;
                resultMode = MeasureSpec.UNSPECIFIED;
            }
        } else {
            if (childDimension >= 0) {
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == RecyclerView.LayoutParams.MATCH_PARENT) {
                resultSize = size;
                resultMode = parentMode;
            } else if (childDimension == RecyclerView.LayoutParams.WRAP_CONTENT) {
                resultSize = size;
                if (parentMode == MeasureSpec.AT_MOST || parentMode == MeasureSpec.EXACTLY) {
                    resultMode = MeasureSpec.AT_MOST;
                } else {
                    resultMode = MeasureSpec.UNSPECIFIED;
                }

            }
        }
        //noinspection WrongConstant
        return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }

    public void layoutDecorated(View child, int left, int top, int right, int bottom) {
        final Rect insets = ((LayoutParams) child.getLayoutParams()).mDecorInsets;
        child.layout(left + insets.left, top + insets.top, right - insets.right,
                bottom - insets.bottom);
    }

    public void layoutDecoratedWithMargins(View child, int left, int top, int right,
                                           int bottom) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        final Rect insets = lp.mDecorInsets;
        child.layout(left + insets.left + lp.leftMargin, top + insets.top + lp.topMargin,
                right - insets.right - lp.rightMargin,
                bottom - insets.bottom - lp.bottomMargin);
    }

    public static abstract class ViewHolder {
        public final View itemView;
        public int mItemViewType = INVALID_TYPE;

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = itemView;
        }

        public int getItemViewType() {

            return mItemViewType;
        }
    }

    /**
     * Base class for an Adapter
     * <p>
     * <p>Adapters provide a binding from an app-specific data set to views that are displayed
     * within a {@link RecyclerView}.</p>
     */
    public static abstract class Adapter<VH extends MultiLayoutView.ViewHolder> {
        public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

        public abstract void onBindViewHolder(VH holder, int position);

        public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
            onBindViewHolder(holder, position);
        }

        /**
         * This method calls {@link #onCreateViewHolder(ViewGroup, int)} to create a new
         * {@link MultiLayoutView.ViewHolder} and initializes some private fields to be used by RecyclerView.
         *
         * @see #onCreateViewHolder(ViewGroup, int)
         */
        public final VH createViewHolder(ViewGroup parent, int viewType) {
            TraceCompat.beginSection(TRACE_CREATE_VIEW_TAG);
            final VH holder = onCreateViewHolder(parent, viewType);
            holder.mItemViewType = viewType;
            TraceCompat.endSection();
            return holder;
        }
    }
}
