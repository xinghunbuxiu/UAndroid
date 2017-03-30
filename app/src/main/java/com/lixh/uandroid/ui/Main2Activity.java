package com.lixh.uandroid.ui;

import android.os.Bundle;
import android.widget.NumberPicker;

import com.lixh.base.BaseActivity;
import com.lixh.uandroid.R;
import com.lixh.view.UToolBar;

public class Main2Activity extends BaseActivity {
    NumberPicker picker;

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.picker;
    }

    @Override
    public boolean initTitle(UToolBar toolBar) {
        toolBar.setTitle("ddddd");
        return false;
    }


}
