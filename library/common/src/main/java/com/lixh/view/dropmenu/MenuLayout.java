
package com.p2peye.remember.ui.capital.view;

import android.support.v4.util.Pools;

import java.util.ArrayList;
import java.util.List;


/**
 * 菜单 适配
 */
public class MenuLayout {
    private static final Pools.SynchronizedPool<Builder> sPool = new Pools.SynchronizedPool<Builder>(3);//对象池
    Builder tBuilder;
    BaseMenuAdapter adapter;
    List<Builder.MenuItem> menuItems;

    public MenuLayout(Builder tBuilder) {
        this.tBuilder = tBuilder;
        adapter = tBuilder.adapter;
        menuItems = tBuilder.menuItems;
    }

    public final static class Builder<T> {

        List<MenuItem<T>> menuItems = new ArrayList<>();
        BaseMenuAdapter adapter;

        public static Builder obtain() {
            Builder b = sPool.acquire();
            if (b == null) {
                b = new Builder();
            }
            return b;
        }

        public Builder addItemView(int index, List<T> dates) {
            addItemView(index, dates, 0);
            return this;
        }

        public Builder addItemView(int index, List<T> dates, int mSelect) {
            MenuItem<T> item = new MenuItem<T>(index, dates, mSelect);
            menuItems.add(item);
            return this;
        }
        private static void recycle(Builder b) {
            b.menuItems = null;
            b.adapter = null;
            sPool.release(b);
        }

        public void setAdapter(BaseMenuAdapter adapter) {
            this.adapter = adapter;
        }

        final class MenuItem<T> {
            int index; //索引
            List<T> dates;// 对应的数据
            int mSelect = 0;

            public MenuItem(int index, List<T> dates, int mSelect) {
                this.index = index;
                this.dates = dates;
                this.mSelect = mSelect;
            }

            public List<T> getDates() {
                return this.dates;
            }
        }

        public MenuLayout build() {
            MenuLayout result = new MenuLayout(this);
            Builder.recycle(this);
            return result;
        }

    }
}
