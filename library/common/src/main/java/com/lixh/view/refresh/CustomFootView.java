package com.lixh.view.refresh;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.lixh.R;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by LIXH on 2017/1/3.
 * email lixhVip9@163.com
 * des
 */

public class CustomFootView extends FooterView {


    ImageView ivNormalRefreshFooterChrysanthemum;
    TextView tvNormalRefreshFooterStatus;

    public CustomFootView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_normal_refresh_footer;
    }

    @Override
    public void initView() {
        ivNormalRefreshFooterChrysanthemum = $(R.id.iv_normal_refresh_footer_chrysanthemum);
        tvNormalRefreshFooterStatus = $(R.id.tv_normal_refresh_footer_status);
    }

    @Override
    public void Scroll(int maxY, int y) {

    }

    @Override
    public void onScrollChange(StateType state) {
        switch (state) {
            case NONE:
                ObjectAnimator.clearAllAnimations();
            case PULL:
                tvNormalRefreshFooterStatus.setText("上拉加载...");
                break;
            case RELEASE:
                tvNormalRefreshFooterStatus.setText("释放加载...");
                break;
            case LOADING:
                tvNormalRefreshFooterStatus.setText("正在加载...");
                AnimUtil.startRotation(ivNormalRefreshFooterChrysanthemum, ViewHelper.getRotation(ivNormalRefreshFooterChrysanthemum) + 359.99f, 500, 0, -1);
                break;
            case LOAD_CLOSE:
                tvNormalRefreshFooterStatus.setText("加载完毕...");
                break;
        }
    }
}
