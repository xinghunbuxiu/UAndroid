package com.lixh.uandroid.view;

import com.lixh.uandroid.R;
import com.lixh.view.multi.ItemViewHolder;
import com.lixh.view.multi.ItemViewProvider;

import java.util.List;


/**
 * Created by LIXH on 2017/7/21.
 * email lixhVip9@163.com
 * des
 */
public class RecyclerItemViewProvider
        extends ItemViewProvider<RecyclerItem, ItemViewHolder> {

    /**
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public RecyclerItemViewProvider(List<RecyclerItem> data) {
        super(data);
        addItemType(RecyclerItem.ITEM_HEADER, R.layout.design_navigation_item);
        addItemType(RecyclerItem.ITEM_HEADER, R.layout.design_navigation_item);
    }


    @Override
    protected void convert(ItemViewHolder helper, RecyclerItem item) {
        switch (item.getItemType()){

        }
    }
}
