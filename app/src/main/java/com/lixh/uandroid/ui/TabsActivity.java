package com.lixh.uandroid.ui;

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
import com.lixh.uandroid.view.SlideLeftView;
import com.lixh.view.LoadView;
import com.lixh.view.SlideMenu;
import com.lixh.view.UToolBar;

/**
 * Created by LIXH on 2016/12/21.
 * email lixhVip9@163.com
 * des
 */
public class TabsActivity extends BaseActivity<TabPresenter> {

    SlideLeftView slideLeftView;

    @Override
    public boolean isShowBack( ) {
        return false;
    }
    @Override
    public boolean isDoubleExit() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void initTitle(UToolBar toolBar) {

    }

    @Override
    public void initLoad(LoadView.Builder builder) {
        slideLeftView = new SlideLeftView(this);
        builder.setSlideMenu(SlideMenu.Slide.LEFT, slideLeftView);
        builder.swipeBack = false;
        builder.hasToolbar = false;
        builder.hasBottomBar = true;
        builder.addItem(new BottomNavigationItem(R.mipmap.ic_favorites, "Home").setActiveColorResource(R.color.colorAccent)
                , new BottomNavigationItem(R.mipmap.ic_friends, "Books").setActiveColorResource(R.color.colorAccent)
                , new BottomNavigationItem(R.mipmap.ic_nearby, "Music").setActiveColorResource(R.color.blue)
                , new BottomNavigationItem(R.mipmap.ic_recents, "Movies & TV").setActiveColorResource(R.color.colorAccent)
                , new BottomNavigationItem(R.mipmap.ic_restaurants, "Games").setActiveColorResource(R.color.colorAccent));
        builder.addFragment(new HomeFragment(), new FirstFragment(), new SecondFragment(), new ThreeFragment(), new FourFragment());
        builder.setOnTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {

            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

}
