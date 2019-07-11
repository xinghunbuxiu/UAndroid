package com.lixh.view.refresh;

import android.os.Build;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;


/**
 * Created by ybao on 16/3/7.
 */
public class CanPullUtil {
    /**
     * * @param 负数表示检测上滑，正数表示下滑
     *
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child com.angel.recycling.desewang.view is a custom com.angel.recycling.desewang.view.
     */
    public static boolean canChildScrollUp(View mChildView) {
        if (mChildView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mChildView.canScrollVertically(-1) || mChildView.getScrollY() > 0;
            }
        } else {
            return mChildView.canScrollVertically(-1);
        }
    }

    public static boolean canChildScrollDown(View mChildView) {
        if (mChildView == null) {
            return false;
        }
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                if (absListView.getChildCount() > 0) {
                    int lastChildBottom = absListView.getChildAt(absListView.getChildCount() - 1).getBottom();
                    return absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1 && lastChildBottom <= absListView.getMeasuredHeight();
                } else {
                    return false;
                }

            } else {
                return mChildView.canScrollVertically(1) || mChildView.getScrollY() > 0;
            }
        } else {
            return mChildView.canScrollVertically(1);
        }
    }

    public static void addScrollListener(final View mChildView, final SpringView pull) {

        if (mChildView instanceof RecyclerView) {
            final RecyclerView mRecyclerView = (RecyclerView) mChildView;

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                boolean isSlidingToLast = false;
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
//                    int totalItemCount = linearLayoutManager.getItemCount();
//                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//                    if ((newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_SETTLING)) {
//                        if (isSlidingToLast && totalItemCount <= (lastVisibleItem + 1) && recyclerView.canScrollVertically(-1)) {
//                            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
//                            if (adapter instanceof RecyclerArrayAdapter) {
//                                ((RecyclerArrayAdapter) adapter).resumeMore();
//                            }
//                        }
//                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    isSlidingToLast = dy > 0;
                }
            });
        }
    }

}