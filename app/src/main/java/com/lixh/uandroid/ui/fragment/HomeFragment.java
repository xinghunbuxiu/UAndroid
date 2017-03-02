package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lixh.base.BaseFragment;
import com.lixh.base.adapter.recycleview.EasyRVAdapter;
import com.lixh.base.adapter.recycleview.EasyRVHolder;
import com.lixh.uandroid.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {
   RecyclerView recyclerView;
    List<Integer> list = new ArrayList<>();
    public HomeFragment(){

    }
    EasyRVAdapter adapter;
    protected void initList() {
        for (int i = 0; i < 15; i++) {
            list.add(R.mipmap.ic_launcher);
        }
        adapter.addAll(list);
    }
    @Override
    protected void init(Bundle savedInstanceState) {
        recyclerView= (RecyclerView) $(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter=new EasyRVAdapter<Integer>(activity,list,R.layout.item_ciew) {

            @Override
            protected void onBindData(EasyRVHolder viewHolder, int position, Integer item) {

            }
        };
        recyclerView.setAdapter(adapter);
        initList();
    }

    @Override
    public boolean initTitle() {
        toolBar.setTitle("home");
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.base_recyview;
    }

}
