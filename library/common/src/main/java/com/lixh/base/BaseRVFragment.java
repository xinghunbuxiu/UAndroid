/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lixh.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lixh.R;
import com.lixh.base.adapter.BaseViewHolder;
import com.lixh.base.adapter.DividerDecoration;
import com.lixh.base.adapter.EasyRVAdapter;
import com.lixh.base.adapter.EasyRVHolder;
import com.lixh.base.adapter.OnLoadMoreListener;
import com.lixh.base.adapter.OnRvItemClickListener;
import com.lixh.base.adapter.RecyclerArrayAdapter;
import com.lixh.utils.LoadingTip.onReloadListener;
import com.lixh.view.refresh.SpringView;

import java.util.List;

/**
 * recycleView列表
 * 下拉刷新
 */
public abstract class BaseRVFragment extends BaseFragment implements OnLoadMoreListener, SpringView.OnRefreshListener, onReloadListener {
    protected RecyclerView mRecyclerView;
    protected SpringView springView;
    protected int page = 0;
    private RecyclerArrayAdapter mAdapter;
    private EasyRVAdapter adapter;


    public abstract RecyclerView.Adapter initAdapter();

    @Override
    public int getLayoutId() {
        return R.layout.base_recyview;
    }

    protected void init(Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) $(R.id.recycle);
        springView = (SpringView) $(R.id.springView);
        if (initAdapter() instanceof RecyclerArrayAdapter) {
            mAdapter = (RecyclerArrayAdapter) initAdapter();
            initAdapter(true, true);
        } else if (initAdapter() instanceof EasyRVAdapter) {
            adapter = (EasyRVAdapter) initAdapter();
            initAdapter(false, false);
        }
    }

    public void finishRefreshAndLoadMore() {
        if (springView != null)
            springView.finishRefreshAndLoadMore();
    }

    /**
     * Set the ItemDecoration to the recycler
     *
     * @param color
     * @param height
     * @param paddingLeft
     * @param paddingRight
     */
    public void setItemDecoration(int color, int height, int paddingLeft, int paddingRight) {
        DividerDecoration itemDecoration = new DividerDecoration(color, height, paddingLeft, paddingRight);
        itemDecoration.setDrawLastItem(false);
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //重新请求监听
        tip.setOnReloadListener(this);
    }

    protected void initAdapter(boolean refreshable, boolean loadMoreAble) {
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            setItemDecoration(R.color.gray, 1, 0, 0);
        }
        if (mAdapter != null) {
            mAdapter.setError(R.layout.common_error_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.resumeMore();

                }
            });
            if (loadMoreAble) {
                mAdapter.setMore(R.layout.common_more_view, this);
                mAdapter.setNoMore(R.layout.common_nomore_view);
            }
            if (refreshable && mRecyclerView != null) {
                springView.setOnRefreshListener(this);
            }
            mRecyclerView.setAdapter(mAdapter);
        } else if (adapter != null) {
            mRecyclerView.setAdapter(adapter);

        }
    }

    public abstract class CommonAdapter<T> extends EasyRVAdapter<T> implements OnRvItemClickListener<T> {

        public CommonAdapter(@LayoutRes int layoutRes, List<T> list) {
            super(activity, list, layoutRes);
        }

        @Override
        protected void onBindData(final EasyRVHolder holder, final int position, final T item) {

            holder.setOnItemViewClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(holder.getItemView(), position, item);
                }
            });
        }
    }

    public abstract class CommonArrayAdapter<T> extends RecyclerArrayAdapter<T> implements OnRvItemClickListener<T> {


        public CommonArrayAdapter(@LayoutRes int layoutRes) {
            super(activity, layoutRes);
        }

        public CommonArrayAdapter(List<T> objects, int layoutRes) {
            super(activity, objects, layoutRes);
        }

        public CommonArrayAdapter(T[] objects, int layoutRes) {
            super(activity, objects, layoutRes);
        }

        @Override
        protected void onBindData(final BaseViewHolder viewHolder, final int position, final T item) {
            viewHolder.setOnItemViewClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(viewHolder.getItemView(), position, item);
                }
            });
        }
    }

    //加载这么隐藏
    @Override
    public void onLoadMore() {

    }

    //下拉刷新
    @Override
    public void onRefresh() {

    }


}
