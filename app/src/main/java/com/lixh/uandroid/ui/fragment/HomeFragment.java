package com.lixh.uandroid.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;

import com.lixh.base.BaseFragment;
import com.lixh.base.adapter.recycleview.DividerDecoration;
import com.lixh.base.adapter.recycleview.EasyRVAdapter;
import com.lixh.base.adapter.recycleview.EasyRVHolder;
import com.lixh.uandroid.R;
import com.lixh.view.UToolBar;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {
   RecyclerView recyclerView;
    List<Integer> list = new ArrayList<>();
    public HomeFragment(){

    }

    @Override
    public void initTitle(UToolBar toolBar) {
        toolBar.setTitle("home");
    }

    EasyRVAdapter adapter;
    protected void initList() {
        for (int i = 0; i < 2; i++) {
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
        recyclerView.addItemDecoration(new DividerDecoration(activity, OrientationHelper.VERTICAL,R.dimen.space_6,R.color.main_color));

        recyclerView.setAdapter(adapter);
        initList();
    }


    @Override
    public int getLayoutId() {
        return R.layout.base_recyview;
    }

}
