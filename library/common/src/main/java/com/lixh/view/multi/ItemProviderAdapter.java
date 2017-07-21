/*
 * Copyright 2013 Joan Zapata
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lixh.view.multi;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public abstract class ItemProviderAdapter<T, K extends ItemViewHolder> extends MultiLayoutView.Adapter<K> {
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    //item
    private LinearLayout mItemLayout;
    protected Context mContext;
    protected int mLayoutResId;
    protected LayoutInflater mLayoutInflater;
    protected List<T> mData;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param layoutResId The layout resource id of each item.
     * @param data        A new list is created out of this one to avoid mutable list
     */
    public ItemProviderAdapter(@LayoutRes int layoutResId, @Nullable List<T> data) {
        this.mData = data == null ? new ArrayList<T>() : data;
        if (layoutResId != 0) {
            this.mLayoutResId = layoutResId;
        }
    }


    /**
     * Get the data of list
     *
     * @return
     */
    @NonNull
    public List<T> getData() {
        return mData;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Nullable
    public T getItem(@IntRange(from = 0) int position) {
        if (position < mData.size())
            return mData.get(position);
        else
            return null;
    }

    public K onCreateViewHolder(ViewGroup parent, int viewType) {
        K baseViewHolder = null;
        this.mContext = parent.getContext();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        baseViewHolder = createBaseViewHolder(mItemLayout);
        bindViewClickListener(baseViewHolder);
        return baseViewHolder;

    }

    /**
     * To bind different types of holder and solve different the bind events
     *
     * @param holder
     * @param positions
     */
    public void onBindViewHolder(K holder, int positions) {
        int viewType = holder.getItemViewType();
        convert(holder, mData.get(positions));
    }

    private void bindViewClickListener(final ItemViewHolder baseViewHolder) {
        if (baseViewHolder == null) {
            return;
        }
        final View view = baseViewHolder.itemView;
        if (view == null) {
            return;
        }
        if (getOnItemClickListener() != null) {
            view.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    getOnItemClickListener().onItemClick(ItemProviderAdapter.this, v, baseViewHolder.getItemViewType());
                }
            });
        }
        if (getOnItemLongClickListener() != null) {
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return getOnItemLongClickListener().onItemLongClick(ItemProviderAdapter.this, v, baseViewHolder.getItemViewType());
                }
            });
        }
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return this.onItemLongClickListener;
    }

    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return this.onItemClickListener;
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    protected K onCreateDefViewHolder(ViewGroup parent, int viewType) {
        int layoutId = mLayoutResId;
        return createBaseViewHolder(parent, layoutId);
    }

    protected K createBaseViewHolder(ViewGroup parent, int layoutResId) {
        return createBaseViewHolder(getItemView(layoutResId, parent));
    }

    /**
     * if you want to use subclass of ItemViewHolder in the adapter,
     * you must override the method to create new ViewHolder.
     *
     * @param view view
     * @return new ViewHolder
     */
    @SuppressWarnings("unchecked")
    protected K createBaseViewHolder(View view) {
        Class temp = getClass();
        Class z = null;
        while (z == null && null != temp) {
            z = getInstancedGenericKClass(temp);
            temp = temp.getSuperclass();
        }
        K k = createGenericKInstance(z, view);
        return null != k ? k : (K) new ItemViewHolder(view);
    }

    /**
     * try to create Generic K instance
     *
     * @param z
     * @param view
     * @return
     */
    @SuppressWarnings("unchecked")
    private K createGenericKInstance(Class z, View view) {
        try {
            Constructor constructor;
            // inner and unstatic class
            if (z.isMemberClass() && !Modifier.isStatic(z.getModifiers())) {
                constructor = z.getDeclaredConstructor(getClass(), View.class);
                constructor.setAccessible(true);
                return (K) constructor.newInstance(this, view);
            } else {
                constructor = z.getDeclaredConstructor(View.class);
                constructor.setAccessible(true);
                return (K) constructor.newInstance(view);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get generic parameter K
     *
     * @param z
     * @return
     */
    private Class getInstancedGenericKClass(Class z) {
        Type type = z.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            for (Type temp : types) {
                if (temp instanceof Class) {
                    Class tempClass = (Class) temp;
                    if (ItemViewHolder.class.isAssignableFrom(tempClass)) {
                        return tempClass;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Return root layout of item
     */

    public LinearLayout getHeaderLayout() {
        return mItemLayout;
    }

    /**
     * Append item to the rear of the mHeaderLayout.
     *
     * @param item
     */
    public int addItemView(View item) {
        return addItemView(item, -1);
    }

    /**
     * Add item view to mHeaderLayout and set item view position in mHeaderLayout.
     * When index = -1 or index >= child count in mHeaderLayout,
     *
     * @param item
     * @param index the position in mHeaderLayout of this item.
     *              When index = -1 or index >= child count in mHeaderLayout,
     */
    public int addItemView(View item, int index) {
        return addItemView(item, index, LinearLayout.VERTICAL);
    }

    /**
     * @param item
     * @param index
     * @param orientation
     */
    public int addItemView(View item, int index, int orientation) {
        if (mItemLayout == null) {
            mItemLayout = new LinearLayout(item.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                mItemLayout.setOrientation(LinearLayout.VERTICAL);
                mItemLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            } else {
                mItemLayout.setOrientation(LinearLayout.HORIZONTAL);
                mItemLayout.setLayoutParams(new LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            }
        }
        final int childCount = mItemLayout.getChildCount();
        if (index < 0 || index > childCount) {
            index = childCount;
        }
        mItemLayout.addView(item, index);
        return index;
    }

    public int setItemView(View item) {
        return setItemView(item, 0, LinearLayout.VERTICAL);
    }

    public int setItemrView(View item, int index) {
        return setItemView(item, index, LinearLayout.VERTICAL);
    }

    public int setItemView(View item, int index, int orientation) {
        if (mItemLayout == null || mItemLayout.getChildCount() <= index) {
            return addItemView(item, index, orientation);
        } else {
            mItemLayout.removeViewAt(index);
            mItemLayout.addView(item, index);
            return index;
        }
    }

    /**
     * @param layoutResId ID for an XML layout resource to load
     * @param parent      Optional view to be the parent of the generated hierarchy or else simply an object that
     *                    provides a set of LayoutParams values for root of the returned
     *                    hierarchy
     * @return view will be return
     */
    protected View getItemView(@LayoutRes int layoutResId, ViewGroup parent) {
        return mLayoutInflater.inflate(layoutResId, parent, false);
    }

    private int getItemPosition(T item) {
        return item != null && mData != null && !mData.isEmpty() ? mData.indexOf(item) : -1;
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    protected abstract void convert(K helper, T item);

    /**
     * Interface definition for a callback to be invoked when an childView in this
     * view has been clicked and held.
     */
    public interface OnItemLongClickListener {
        /**
         * callback method to be invoked when an item in this view has been
         * click and held
         *
         * @param view     The childView whihin the itemView that was clicked and held.
         * @param position The position of the view int the adapter
         * @return true if the callback consumed the long click ,false otherwise
         */
        boolean onItemLongClick(ItemProviderAdapter adapter, View view, int position);
    }


    /**
     * Interface definition for a callback to be invoked when an item in this
     * RecyclerView itemView has been clicked.
     */
    public interface OnItemClickListener {

        /**
         * Callback method to be invoked when an item in this RecyclerView has
         * been clicked.
         *
         * @param adapter  the adpater
         * @param view     The itemView within the RecyclerView that was clicked (this
         *                 will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         */
        void onItemClick(ItemProviderAdapter adapter, View view, int position);
    }

}
