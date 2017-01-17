package com.lixh.uandroid.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.lixh.base.BaseActivity;
import com.lixh.uandroid.R;
import com.lixh.uandroid.presenter.TabPresenter;
import com.lixh.uandroid.ui.fragment.FirstFragment;
import com.lixh.uandroid.ui.fragment.FourFragment;
import com.lixh.uandroid.ui.fragment.HomeFragment;
import com.lixh.uandroid.ui.fragment.SecondFragment;
import com.lixh.uandroid.ui.fragment.ThreeFragment;
import com.lixh.view.UToolBar;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by LIXH on 2016/12/21.
 * email lixhVip9@163.com
 * des
 */

public class TabsActivity extends BaseActivity<TabPresenter> implements BottomNavigationBar.OnTabSelectedListener {
    @Bind(R.id.layFrame)
    FrameLayout layFrame;
    @Bind(R.id.bottom_navigation_bar)
    BottomNavigationBar bottomNavigationBar;
    private ArrayList<Fragment> fragments;

    @Override
    public boolean hasToolBar() {
        return false;
    }
    @Override
    public boolean initTitle(UToolBar toolBar) {
        return false;
    }
    @Override
    protected void init(Bundle savedInstanceState) {
        initBottomBar();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tab;
    }



    public void initBottomBar() {
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC
                );
        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_favorites, "Home").setActiveColorResource(R.color.colorAccent))
                .addItem(new BottomNavigationItem(R.mipmap.ic_friends, "Books").setActiveColorResource(R.color.colorAccent))
                .addItem(new BottomNavigationItem(R.mipmap.ic_nearby, "Music").setActiveColorResource(R.color.blue))
                .addItem(new BottomNavigationItem(R.mipmap.ic_recents, "Movies & TV").setActiveColorResource(R.color.colorAccent))
                .addItem(new BottomNavigationItem(R.mipmap.ic_restaurants, "Games").setActiveColorResource(R.color.colorAccent))
                .setFirstSelectedPosition(0)
                .initialise();

        fragments = getFragments();
        setDefaultFragment();
        bottomNavigationBar.setTabSelectedListener(this);
    }

    /**
     * 设置默认的
     */
    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.layFrame, fragments.get(0));
        transaction.commit();
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(HomeFragment.newInstance("Home"));
        fragments.add(FirstFragment.newInstance("Books"));
        fragments.add(SecondFragment.newInstance("Music"));
        fragments.add(ThreeFragment.newInstance("Movies & TV"));
        fragments.add(FourFragment.newInstance("Games"));
        return fragments;
    }

    @Override
    public void onTabSelected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                if (fragment.isAdded()) {
                    ft.replace(R.id.layFrame, fragment);
                } else {
                    ft.add(R.id.layFrame, fragment);
                }
                ft.commitAllowingStateLoss();
            }
        }

    }

    @Override
    public void onTabUnselected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                ft.remove(fragment);
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabReselected(int position) {

    }

}
