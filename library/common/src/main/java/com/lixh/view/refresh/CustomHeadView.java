package com.lixh.view.refresh;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.lixh.R;

/**
 * Created by LIXH on 2017/1/3.
 * email lixhVip9@163.com
 * des
 */

public class CustomHeadView extends HeaderView {

    ImageView ivNormalRefreshHeader;
    TextView tvNormalRefreshHeaderStatus;

    public CustomHeadView(Context context) {
        super(context);
    }

    @Override
    public void onScrollChange(StateType state) {
        switch (state) {
            case NONE:
            case PULL:
                tvNormalRefreshHeaderStatus.setText("下拉刷新...");
                break;
            case RELEASE:
                tvNormalRefreshHeaderStatus.setText("释放刷新...");
                break;
            case LOADING:
                tvNormalRefreshHeaderStatus.setText("正在刷新...");
                break;
            case LOAD_CLOSE:
                tvNormalRefreshHeaderStatus.setText("刷新结束...");
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_refresh_header_normal;
    }

    @Override
    public void initView() {
        tvNormalRefreshHeaderStatus = $(R.id.tv_normal_refresh_header_status);
        ivNormalRefreshHeader = $(R.id.iv_normal_refresh_header);

    }

    @Override
    public void Scroll(int maxY, int y) {
        int currentProgress = Math.abs((y / getHeight()));
        if (currentProgress >= 11) {
            currentProgress = 11;
        }
        ivNormalRefreshHeader.getDrawable().setLevel(currentProgress);
    }


}
