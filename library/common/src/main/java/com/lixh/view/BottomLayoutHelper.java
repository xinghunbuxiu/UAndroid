package com.lixh.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationBar.OnTabSelectedListener;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.lixh.R;

/**
 * Created by LIXH on 2017/10/25.
 * email lixhVip9@163.com
 * des
 */

public class BottomLayoutHelper {
    View view;
    FrameLayout layFrame;
    BottomNavigationBar bottomNavigationBar;
    LoadView.Builder builder;
    private Fragment fragments[];
    OnTabSelectedListener listener;
    FragmentManager fm;

    public BottomLayoutHelper(LoadView.Builder builder) {
        this.builder = builder;
        fm = builder.supportFragmentManager;
        view = builder.inflate(builder.bottomBarLayout <= 0 ? R.layout.common_tab_layout : builder.bottomBarLayout);
    }

    public View getLayout() {
        return view;
    }

    public void initBottomBarLayout() {
        if (builder.bottomBarLayout <= 0) {
            layFrame = (FrameLayout) view.findViewById(R.id.layFrame);
            bottomNavigationBar = (BottomNavigationBar) view.findViewById(R.id.bottom_navigation_bar);
            bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
            bottomNavigationBar
                    .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC
                    );
            addItem(builder.items).setFirstSelectedPosition(0)
                    .initialise();
            listener = builder.tabSelectedListener;
            fragments = builder.fragments;
            bottomNavigationBar.setTabSelectedListener(new MyOnTabSelectedListener());
            setDefaultFragment();
        }else{
            builder.initBottomBarLayout(view);
        }
    }

    /**
     * 添加底部
     *
     * @param items
     * @return
     */
    public BottomNavigationBar addItem(BottomNavigationItem... items) {
        for (BottomNavigationItem item : items) {
            bottomNavigationBar.addItem(item);
        }
        return bottomNavigationBar;
    }

    /**
     * 设置默认的
     */
    private void setDefaultFragment() {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.layFrame, fragments[0]);
        transaction.commit();
    }


    class MyOnTabSelectedListener implements OnTabSelectedListener {
        @Override
        public void onTabSelected(int position) {
            if (listener != null) {
                listener.onTabSelected(position);
            }
            if (fragments != null) {
                if (position < fragments.length) {
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment fragment = fragments[position];
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
            if (listener != null) {
                listener.onTabSelected(position);
            }
            if (fragments != null) {
                if (position < fragments.length) {
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment fragment = fragments[position];
                    ft.remove(fragment);
                    ft.commitAllowingStateLoss();
                }
            }
        }

        @Override
        public void onTabReselected(int position) {
//       intent.withBoolean("sss",false).withBoolean("dddd",true).go(WelcomeActivity.class);
        }
    }


}
