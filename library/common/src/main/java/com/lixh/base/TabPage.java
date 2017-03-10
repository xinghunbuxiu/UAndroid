package com.lixh.base;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.lixh.R;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by LIXH on 2017/3/6.
 * email lixhVip9@163.com
 * des
 */

public class TabPage {
    MagicIndicator magicIndicator;
    ViewPager mvPager;
    List<String> mDataList;
    CommonNavigator commonNavigator;
    Builder builder;
    Context context;
    View tab_layout;

    public TabPage(Builder builder) {
        initView(builder);
    }

    private void initView(Builder builder) {
        tab_layout = View.inflate(context, R.layout.tab_layout, null);
        magicIndicator = (MagicIndicator) tab_layout.findViewById(R.id.magic_indicator);
        mvPager = (ViewPager) tab_layout.findViewById(R.id.view_pager);
        commonNavigator = new CommonNavigator(context);
        mvPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);

            }

            @Override
            public void onPageSelected(int position) {
                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);

            }
        });
        commonNavigator.setAdapter(builder.getNavigatorAdapter());
        ViewPagerHelper.bind(magicIndicator, mvPager);
    }

    public abstract static class Builder {
        List<String> mDataList;
        CommonNavigatorAdapter navigatorAdapter;

        public Builder(Context context) {
            mDataList = null;
        }

        public CommonNavigatorAdapter getNavigatorAdapter() {
            return navigatorAdapter;
        }

        public Builder setData(String[] CHANNELS) {
            setData(Arrays.asList(CHANNELS));
            return this;
        }

        public Builder setData(final List<String> mDataList) {
            this.mDataList = mDataList;
            navigatorAdapter = new CommonNavigatorAdapter() {
                @Override
                public int getCount() {
                    return mDataList == null ? 0 : mDataList.size();
                }

                @Override
                public IPagerTitleView getTitleView(Context context, int i) {
                    return getTitleView(context, i);
                }

                @Override
                public IPagerIndicator getIndicator(Context context) {
                    return initIndicator(context);
                }
            };
            return this;
        }

        public abstract IPagerTitleView initTitleView(Context context, int i);

        public abstract IPagerIndicator initIndicator(Context context);

        public TabPage Build() {
            return new TabPage(this);
        }
    }

}
