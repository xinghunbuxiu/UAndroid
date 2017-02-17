package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.lixh.base.BaseFragment;
import com.lixh.uandroid.R;
import com.lixh.view.refresh.SpringView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;


public class SecondFragment extends BaseFragment {

    @Bind(R.id.recycle)
    RecyclerView recycle;
    @Bind(R.id.refresh)
    SpringView refresh;
    List<Integer> list = new ArrayList<>();
    public static SecondFragment newInstance(String param1) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        for (int i = 0; i < 15; i++) {
            list.add(R.mipmap.ic_launcher);
        }
    }

    @Override
    public boolean initTitle() {
        toolBar.setTitle("recycle_view");
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.recycle_view;
    }


}
