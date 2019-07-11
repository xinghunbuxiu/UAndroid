package com.lixh.base.adapter;

import android.view.View;

/**
 * 类名: {@link ActionListener}
 * <br/> 功能描述: recyclerView万能适配器的点击事件
 * <br/> 作者: MouTao
 * <br/> 时间: 2017/5/17
 * <br/> 最后修改者:
 * <br/> 最后修改内容:
 */
public interface ActionListener {
    void onActionClick(View view, int position, Object mData);
}
