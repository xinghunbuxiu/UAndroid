package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;

import com.lixh.base.BaseFragment;
import com.lixh.uandroid.R;


public class ThreeFragment extends BaseFragment {

    public static ThreeFragment newInstance(String param1) {
        ThreeFragment fragment = new ThreeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    public boolean initTitle() {
        toolBar.setTitle("Three");
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }


}
