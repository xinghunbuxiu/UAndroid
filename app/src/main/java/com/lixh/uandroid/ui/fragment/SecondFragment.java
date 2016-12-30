package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;

import com.lixh.base.BaseFragment;
import com.lixh.uandroid.R;


public class SecondFragment extends BaseFragment {

    public static SecondFragment newInstance(String param1) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    public boolean initTitle() {
        toolBar.setTitle("Second");
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }


}
