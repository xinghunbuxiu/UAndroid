package com.lixh.base;


import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lixh.R;
import com.lixh.base.adapter.recycleview.BaseViewHolder;
import com.lixh.base.adapter.recycleview.DividerDecoration;
import com.lixh.base.adapter.recycleview.EasyRVAdapter;
import com.lixh.base.adapter.recycleview.EasyRVHolder;
import com.lixh.base.adapter.recycleview.OnLoadMoreListener;
import com.lixh.base.adapter.recycleview.RecyclerArrayAdapter;
import com.lixh.utils.LoadingTip;
import com.lixh.view.refresh.SpringView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by LIXH on 2017/2/28.
 * email lixhVip9@163.com
 * des
 */

public class Page<T> implements OnLoadMoreListener, SpringView.OnRefreshListener, LoadingTip.onReloadListener {
    Builder builder;
    View rootView;
    RecyclerView recyclerView;
    SpringView springView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    private RecyclerArrayAdapter arrayAdapter;//带有加载更多，header footer
    private EasyRVAdapter rvAdapter;//普通列表
    int page = 1;
    private OnLoadingListener onLoadingListener;
    LoadingTip tip;
    RecyclerView.OnScrollListener onScrollListener;

    @Override
    public void reload() {
        onRefresh();
    }

    public interface OnLoadFinish<T> {
        void finish(List<T> list, @LoadingTip.LoadStatus int loadStatus);
    }

    public interface OnLoadingListener {
        void load(int page, OnLoadFinish onLoadFinish);
    }

    /**
     * @return RecyclerArrayAdapter
     */
    public RecyclerArrayAdapter getArrayAdapter() {
        return arrayAdapter;
    }

    /**
     * @return EasyRVAdapter
     */
    public EasyRVAdapter getRvAdapter() {
        return rvAdapter;
    }

    @Override
    public void onLoadMore() {
        page++;
        onLoad(onLoadingListener);
    }

    /**
     * @param list       结束后加载list信息
     * @param loadStatus //状态
     */
    public void finish(List<T> list, @LoadingTip.LoadStatus int loadStatus) {
        onFinish(list);
        onError(loadStatus);
    }

    /**
     * 结束填充
     *
     * @param list
     */
    public void onFinish(List<T> list) {
        springView.finishRefreshAndLoadMore();
        onError(LoadingTip.LoadStatus.FINISH);
        if (arrayAdapter != null) {
            arrayAdapter.addAll(list);
        } else if (rvAdapter != null) {
            rvAdapter.addAll(list);
        }
    }

    /**
     * @param loadStatus 结束状态
     */
    public void onError(@LoadingTip.LoadStatus int loadStatus) {
        if (arrayAdapter != null) {
            switch (loadStatus) {
                case LoadingTip.LoadStatus.SHOW_LOAD_MORE_VIEW: //分页加载时
                    if (page > 1) {
                        page--;
                    }
                    arrayAdapter.pauseMore();
                    break;
                default:
                    if (tip != null)
                        tip.setLoadingTip(loadStatus);
                    break;
            }
        }
    }

    public void onLoad(OnLoadingListener onLoadingListener) {
        if (this.onLoadingListener != null) {
            this.onLoadingListener.load(page, new OnLoadFinish<T>() {

                @Override
                public void finish(List<T> list, @LoadingTip.LoadStatus int loadStatus) {
                    onFinish(list);
                    onError(loadStatus);
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        if (arrayAdapter != null) {
            arrayAdapter.clear();
        }
        if (rvAdapter != null) {
            rvAdapter.clear();
        }
        onLoad(onLoadingListener);
    }


    @IntDef({
            PageType.List,
            PageType.Grid,
            PageType.StaggeredGrid,
            PageType.Custom
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PageType {
        int List = 0;
        int Grid = 1;
        int StaggeredGrid = 2;
        int Custom = 3;

    }

    static Context mContext;

    public Page(Builder builder, @PageType int type) {
        this.builder = builder;
        rootView = inflate(R.layout.base_recyview);
        recyclerView = $(R.id.recycle);
        springView = $(R.id.springView);
        onLoadingListener = builder.getOnLoadingListener();
        tip = builder.getLoadTip();
        if (tip != null) {
            tip.setLoadingTip(LoadingTip.LoadStatus.LOADING);
            tip.setOnReloadListener(this);
        }
        if (builder.getAdapter() instanceof RecyclerArrayAdapter)
            arrayAdapter = (RecyclerArrayAdapter) builder.getAdapter();
        else {
            rvAdapter = (EasyRVAdapter) builder.getAdapter();
        }
        switch (type) {
            case PageType.List:
                layoutManager = new LinearLayoutManager(mContext, builder.getOrientation(), false);
                break;
            case PageType.Grid:
                layoutManager = new GridLayoutManager(mContext, builder.getSpanCount(), builder.getOrientation(), false);
                break;
            case PageType.StaggeredGrid:
                layoutManager = new StaggeredGridLayoutManager(builder.getSpanCount(), builder.getOrientation());
                break;
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        onScrollListener = builder.getOnScrollListener();
        if (onScrollListener != null) {
            recyclerView.addOnScrollListener(onScrollListener);
        }
        setItemDecoration(builder.getDivideColor(), builder.getDivideHeight(), builder.getPaddingLeft(), builder.getPaddingRight());
        if (arrayAdapter != null) {
            if (builder.isAutoLoadMore()) {
                arrayAdapter.setError(builder.getError()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrayAdapter.resumeMore();
                    }
                });
                arrayAdapter.setMore(builder.getMore(), this);
                arrayAdapter.setNoMore(builder.getNoMore());
            }
            adapter = arrayAdapter;
        } else if (rvAdapter != null) {
            adapter = rvAdapter;
        }
        if (builder.isPullLoadMore()) {
            springView.setOnLoadListener(new SpringView.OnLoadListener() {
                @Override
                public void onLoad() {
                    onLoadMore();
                }
            });
        }
        if (builder.isRefresh() && recyclerView != null) {
            springView.setOnRefreshListener(this);
        }
        recyclerView.setAdapter(adapter);

    }

    /**
     * Set the ItemDecoration to the recycler
     *
     * @param color
     * @param height
     * @param paddingLeft
     * @param paddingRight
     */

    public void setItemDecoration(int color, @DimenRes int height, int paddingLeft, int paddingRight) {
        recyclerView.addItemDecoration(new DividerDecoration(mContext, OrientationHelper.VERTICAL, height, color));
    }

    public <T extends View> T $(int viewId) {
        return (T) rootView.findViewById(viewId);
    }

    public View getRootView() {
        return rootView;
    }

    protected View inflate(int layoutResID) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layoutResID, null);
        return view;
    }

    public abstract static class Builder<T> implements RecyclerArrayAdapter.OnItemClickListener<T> {
        @PageType
        int type;
        RecyclerView.Adapter adapter;
        private RecyclerArrayAdapter arrayAdapter;//带有加载更多，header footer
        private EasyRVAdapter rvAdapter;//普通列表
        boolean refresh;
        boolean pullLoadMore;
        boolean isAutoLoadMore;
        @DimenRes
        int divideHeight = R.dimen.space_2;
        @ColorRes
        int divideColor = R.color.main_color;
        @LayoutRes
        int noMore = R.layout.common_nomore_view;
        @LayoutRes
        int more = R.layout.common_more_view;
        @LayoutRes
        int error = R.layout.common_error_view;
        private int spanCount = 5;
        private int orientation = OrientationHelper.VERTICAL;
        private int paddingLeft;
        private int paddingRight;
        private OnLoadingListener onLoadingListener;
        private RecyclerView.OnScrollListener onScrollListener;

        public Builder setLoadTip(LoadingTip loadTip) {
            this.loadTip = loadTip;
            return this;
        }

        private LoadingTip loadTip;

        public Builder setOnLoadingListener(OnLoadingListener onLoadingListener) {
            this.onLoadingListener = onLoadingListener;
            return this;
        }


        public Builder setPaddingRight(int paddingRight) {
            this.paddingRight = paddingRight;
            return this;
        }

        public boolean isAutoLoadMore() {
            return isAutoLoadMore;
        }

        public Builder setAutoLoadMore(boolean autoLoadMore) {
            isAutoLoadMore = autoLoadMore;
            return this;
        }

        public boolean isPullLoadMore() {
            return pullLoadMore;
        }

        public Builder setPullLoadMore(boolean pullLoadMore) {
            this.pullLoadMore = pullLoadMore;
            return this;
        }


        /**
         * 加载错误
         *
         * @param layout
         * @return
         */
        public Builder setError(@LayoutRes int layout) {
            this.error = layout;
            return this;
        }

        /**
         * 加载更多
         *
         * @param layout
         * @return
         */
        public Builder setMore(@LayoutRes int layout) {
            this.more = layout;
            return this;
        }

        /**
         * 没有更多
         *
         * @param layout
         * @return
         */
        public Builder setNoMore(@LayoutRes int layout) {
            this.noMore = layout;
            return this;
        }

        public Builder setPaddingLeft(int paddingLeft) {
            this.paddingLeft = paddingLeft;
            return this;
        }

        public OnLoadingListener getOnLoadingListener() {
            return onLoadingListener;
        }

        @LayoutRes
        public int getError() {
            return error;
        }

        @LayoutRes
        public int getMore() {
            return more;
        }

        @LayoutRes
        public int getNoMore() {
            return noMore;
        }

        public int getPaddingLeft() {
            return paddingLeft;
        }

        public int getPaddingRight() {
            return paddingRight;
        }

        public boolean isRefresh() {
            return refresh;
        }

        public Builder setRefresh(boolean refresh) {
            this.refresh = refresh;
            return this;
        }

        @DimenRes
        public int getDivideHeight() {
            return divideHeight;
        }

        public void setOrientation(int orientation) {
            this.orientation = orientation;
        }

        public int getOrientation() {
            return orientation;
        }

        public Builder setSpanCount(int spanCount) {
            this.spanCount = spanCount;
            return this;
        }

        public Builder setDivideHeight(@DimenRes int divideHeight) {
            this.divideHeight = divideHeight;
            return this;
        }

        public Builder setDivideHeight(@DimenRes int divideHeight, @ColorRes int color) {
            this.divideHeight = divideHeight;
            this.divideColor = color;
            return this;
        }
        @ColorRes
        public int getDivideColor() {
            return divideColor;
        }

        public LoadingTip getLoadTip() {
            return loadTip;
        }

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setType(@PageType int type) {
            this.type = type;
            return this;
        }

        public Builder setAdapter(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder setRVAdapter(@LayoutRes int layoutRes, List<T> list) {
            this.adapter = this.rvAdapter = new EasyRVAdapter<T>(mContext, list, layoutRes) {
                @Override
                protected void onBindData(final EasyRVHolder viewHolder, final int position, final T item) {
                    onBindRVHolderData(viewHolder, position, item);
                    viewHolder.setOnItemViewClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemClick(viewHolder.getItemView(), position, item);
                        }
                    });
                }
            };
            return this;

        }

        public Builder setArrayAdapter(@LayoutRes int layoutRes) {
            this.adapter = this.arrayAdapter = new RecyclerArrayAdapter<T>(mContext, layoutRes) {

                @Override
                protected void onBindData(final BaseViewHolder viewHolder, final int position, final T item) {
                    onBindViewData(viewHolder, position, item);
                    viewHolder.setOnItemViewClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemClick(viewHolder.getItemView(), position, item);
                        }
                    });
                }
            };
            return this;
        }

        public Builder setArrayAdapter(@LayoutRes int layoutRes, List<T> objects) {
            this.adapter = this.arrayAdapter = new RecyclerArrayAdapter<T>(mContext, objects, layoutRes) {

                @Override
                protected void onBindData(final BaseViewHolder viewHolder, final int position, final T item) {
                    onBindViewData(viewHolder, position, item);
                    viewHolder.setOnItemViewClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemClick(viewHolder.getItemView(), position, item);
                        }
                    });
                }
            };
            return this;

        }

        public Builder setArrayAdapter(@LayoutRes int layoutRes, T[] objects) {
            this.adapter = this.arrayAdapter = new RecyclerArrayAdapter<T>(mContext, objects, layoutRes) {

                @Override
                protected void onBindData(final BaseViewHolder viewHolder, final int position, final T item) {
                    onBindViewData(viewHolder, position, item);
                    viewHolder.setOnItemViewClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemClick(viewHolder.getItemView(), position, item);
                        }
                    });
                }
            };
            return this;
        }

        public RecyclerView.OnScrollListener getOnScrollListener() {
            return onScrollListener;
        }

        /**
         * recycleView 滚动监听
         *
         * @param onScrollListener
         * @return
         */
        public Builder addOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
            this.onScrollListener = onScrollListener;
            return this;
        }
        public Page<T> Build(@PageType int type) {
            return new Page<T>(this, type);
        }

        public int getSpanCount() {
            return spanCount;
        }


        public RecyclerView.Adapter getAdapter() {
            return adapter;
        }

        @Override
        public abstract void onItemClick(View view, int position, T data);

        protected void onBindRVHolderData(EasyRVHolder viewHolder, int position, T item) {

        }

        public void onBindViewData(final BaseViewHolder viewHolder, final int position, final T item) {

        }

        public void onBindFooterViewData(View headerView, int position) {

        }

        public void onBindHeaderViewData(View footerView, int position) {

        }

        public Builder addFooterView(final int footerView) {
            if (arrayAdapter != null) {
                arrayAdapter.addFooter(new RecyclerArrayAdapter.FooterItemView() {
                    @Override
                    public View OnCreateFooterViewHolder(ViewGroup parent) {
                        return View.inflate(mContext, footerView, parent);
                    }

                    @Override
                    public void onBindFooterData(View headerView, int position) {
                        onBindFooterViewData(headerView, position);
                    }
                });
            }
            return this;
        }

        public Builder addHeaderView(final int header) {
            if (arrayAdapter != null) {
                arrayAdapter.addHeader(new RecyclerArrayAdapter.HeaderItemView() {
                    @Override
                    public View OnCreateHeaderViewHolder(ViewGroup parent) {
                        return View.inflate(mContext, header, parent);
                    }

                    @Override
                    public void onBindHeaderData(View headerView, int position) {
                        onBindFooterViewData(headerView, position);
                    }
                });
            }
            return this;
        }


    }
}