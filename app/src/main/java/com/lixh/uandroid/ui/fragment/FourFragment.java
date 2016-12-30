package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;

import com.lixh.base.BaseFragment;
import com.lixh.uandroid.R;


public class FourFragment extends BaseFragment {

    public static FourFragment newInstance(String param1) {
        FourFragment fragment = new FourFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    public boolean initTitle() {
        toolBar.setTitle("Four");
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }


}
