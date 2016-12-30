package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;

import com.lixh.base.BaseFragment;
import com.lixh.uandroid.R;


public class FirstFragment extends BaseFragment {

    public static FirstFragment newInstance(String param1) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    public boolean initTitle() {
        toolBar.setTitle("first");
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }


}
