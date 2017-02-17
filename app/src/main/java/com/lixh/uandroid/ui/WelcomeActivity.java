package com.lixh.uandroid.ui;

import com.lixh.base.LaunchActivity;
import com.lixh.uandroid.R;

/**
 * Created by LIXH on 2017/2/7.
 * email lixhVip9@163.com
 * des
 */

public class WelcomeActivity extends LaunchActivity {


    @Override
    public boolean isFirst() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_launch;
    }

    /**
     * 这里执行动画
     */
    @Override
    public void initView() {
        super.initView();
    }

    @Override
    public Class toActivity(int what) {
        return what == GO_HOME ? TabsActivity.class : TabsActivity.class;
    }

}
