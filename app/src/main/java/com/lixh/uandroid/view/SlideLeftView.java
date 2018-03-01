package com.lixh.uandroid.view;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.lixh.uandroid.R;
import com.lixh.view.BaseSlideView;

/**
 * Created by LIXH on 2017/5/15.
 * email lixhVip9@163.com
 * des
 */

public class SlideLeftView extends BaseSlideView {

    public SlideLeftView(Activity activity) {
        super(activity);
    }

    @Override
    public int getLayoutId() {
        return R.layout.slide_menu_layout;
    }

    @Override
    public void init() {
        isAnim = false;
        following = false;
    }

    @Override
    public void initView(View slideView) {
        TextView textView = $(R.id.textView);
        textView.setText("dddddddd");
    }


}
