package com.lixh.base.adapter.recycleview;


import android.content.Context;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.lixh.R;
import com.lixh.base.adapter.VBaseAdapter;
import com.lixh.utils.LoadingTip;
import com.lixh.view.refresh.SpringView;

import java.util.List;

/**
 * Created by LIXH on 2017/2/28.
 * email lixhVip9@163.com
 * des
 */

public class PageView<T> implements SpringView.OnRefreshListener, LoadingTip.onReloadListener {
    View rootView;
    RecyclerView recyclerView;
    SpringView springView;
    int page = 0;
    LoadingTip loadingTip;
    private MyOnLoadFinish onLoadFinish;
    VirtualLayoutManager virtualLayoutManager;
    DelegateAdapter mAdapters;
    boolean refresh;
    boolean pullLoadMore;
    boolean isAutoLoadMore;
    boolean isAutoRefresh;
    @DimenRes
    int divideHeight = R.dimen.space_2;
    private int orientation = OrientationHelper.VERTICAL;
    private OnLoadingListener onLoadingListener;
    private RecyclerView.OnScrollListener onScrollListener;
    private int divideColor;
    RecyclerView.RecycledViewPool viewPool;
    private RecycledViewPool recycledViewPool;

    public static PageView with(Context context) {
        return new PageView(context);
    }

    public PageView(Context context) {
        mContext = context;
        rootView = inflate(R.layout.base_recyview);
        recyclerView = $(R.id.recycle);
        springView = $(R.id.springView);
        virtualLayoutManager = new VirtualLayoutManager(context);
        mAdapters = new DelegateAdapter(virtualLayoutManager, false);
        viewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public void reload() {
        onRefresh();
    }

    public void onLoadMore() {
        page++;
        onLoad(onLoadingListener);
    }

    public PageView setLayoutManager(VirtualLayoutManager layoutManager) {
        this.virtualLayoutManager = layoutManager;
        return this;
    }

    public PageView setRecycledViewPool(RecycledViewPool recycledViewPool) {
        this.recycledViewPool = recycledViewPool;
        return this;
    }

    public RecycledViewPool getRecycledViewPool() {
        return recycledViewPool;
    }

    public PageView setMaxRecycledViews(int viewType, int max) {
        if (recycledViewPool != null) {
            recycledViewPool.setMaxRecycledViews(viewType, max);
        }

        return this;
    }

    public void addAllAdapter(List<DelegateAdapter.Adapter> adapters) {
        if (mAdapters != null) {
            mAdapters.addAdapters(adapters);
        }
    }

    public void addAdapter(VBaseAdapter adapters) {
        if (mAdapters != null) {
            mAdapters.addAdapter(adapters);
        }
    }

    public void clear() {
        if (mAdapters != null) {
            mAdapters.clear();
        }
    }

    public interface OnLoadFinish<T> {
        void finish(@LoadingTip.LoadStatus int loadStatus);
    }

    public void setViewPool(RecycledViewPool viewPool) {
        this.viewPool = viewPool;
    }

    public interface OnLoadingListener {
        void load(int page, OnLoadFinish onLoadFinish);
    }

    /**
     * @param list       结束后加载list信息
     * @param loadStatus //状态
     */
    public void finish(@LoadingTip.LoadStatus int loadStatus) {
        onFinish();
    }

    /**
     * 结束填充
     */
    public void onFinish() {
        springView.finishRefreshAndLoadMore();
        onError(LoadingTip.LoadStatus.FINISH);
    }

    /**
     * @param loadStatus 结束状态
     */
    public void onError(@LoadingTip.LoadStatus int loadStatus) {
        switch (loadStatus) {
            case LoadingTip.LoadStatus.SHOW_LOAD_MORE_ERROR: //分页加载时
                if (page > 1) {
                    page--;
                }
                break;
            default:
                if (loadingTip != null)
                    loadingTip.setLoadingTip(loadStatus);
                break;
        }

    }

    public void onLoad(OnLoadingListener onLoadingListener) {
        if (this.onLoadingListener != null) {
            onLoadFinish = new MyOnLoadFinish(this);
            this.onLoadingListener.load(page, onLoadFinish);
        }
    }

    class MyOnLoadFinish<T> implements OnLoadFinish<T> {
        PageView page;

        public MyOnLoadFinish(PageView page) {
            this.page = page;
        }

        @Override
        public void finish(@LoadingTip.LoadStatus int loadStatus) {
            page.finish(loadStatus);
        }
    }

    ;

    @Override
    public void onRefresh() {
        page = 0;
        onLoad(onLoadingListener);
    }

    static Context mContext;

    public PageView build() {
        springView.setAutoRefresh(isAutoRefresh);
        onLoadingListener = getOnLoadingListener();
        if (loadingTip != null) {
            loadingTip.setLoadingTip(LoadingTip.LoadStatus.LOADING);
            loadingTip.setOnReloadListener(this);
        }
        recyclerView.setLayoutManager(virtualLayoutManager);
        if (viewPool != null) {
            recyclerView.setRecycledViewPool(viewPool);
        }
        if (onScrollListener != null) {
            recyclerView.addOnScrollListener(onScrollListener);
        }
        if (mAdapters != null) {
            if (isAutoLoadMore) {
            } else {
                recyclerView.setAdapter(mAdapters);
            }
            if (isPullLoadMore()) {
                springView.setOnLoadListener(() -> onLoadMore());
            }
            if (isRefresh() && springView != null) {
                springView.setOnRefreshListener(this);
            } else {
                onRefresh();
            }

        }
        return this;
    }

    /**
     * Set the ItemDecoration to the recycler
     *
     * @param color
     * @param height
     * @param paddingLeft
     * @param paddingRight
     */

    public void setItemDecoration(int color, @DimenRes int height, int paddingLeft,
                                  int paddingRight) {
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


    public PageView setLoadTip(LoadingTip loadTip) {
        this.loadTip = loadTip;
        return this;
    }

    private LoadingTip loadTip;

    public PageView setOnLoadingListener(OnLoadingListener onLoadingListener) {
        this.onLoadingListener = onLoadingListener;
        return this;
    }

    public PageView setAutoLoadMore(boolean autoLoadMore) {
        isAutoLoadMore = autoLoadMore;
        return this;
    }

    public PageView setAutoRefresh(boolean autoRefresh) {
        this.isAutoRefresh = autoRefresh;
        return this;
    }

    public boolean isPullLoadMore() {
        return pullLoadMore;
    }

    public PageView setPullLoadMore(boolean pullLoadMore) {
        this.pullLoadMore = pullLoadMore;
        return this;
    }

    public OnLoadingListener getOnLoadingListener() {
        return onLoadingListener;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public PageView setRefresh(boolean refresh) {
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

    public PageView setDivideHeight(@DimenRes int divideHeight) {
        this.divideHeight = divideHeight;
        return this;
    }

    public PageView setDivideHeight(@DimenRes int divideHeight, @ColorRes int color) {
        this.divideHeight = divideHeight;
        divideColor = color;
        return this;
    }


    /**
     * recycleView 滚动监听
     *
     * @param onScrollListener
     * @return
     */
    public PageView addOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
        return this;
    }

}