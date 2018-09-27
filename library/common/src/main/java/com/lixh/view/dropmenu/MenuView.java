
package com.p2peye.remember.ui.capital.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.ValueAnimator;
import com.p2peye.common.commonutils.DisplayUtil;
import com.p2peye.remember.R;

import java.util.List;

/**
 * 菜单 适配
 */
public class MenuView<T> extends RelativeLayout {
    private final int textSelectColor;
    private final int textUnSelectColor;
    private final Animation inAnim;
    private final Animation outAnim;
    private ValueAnimator alphaAnim;
    private BaseMenuAdapter adapter;
    private LinearLayout navLayout;
    private LinearLayout selectLayout;
    //遮罩半透明View，点击可关闭DropDownMenu
    private FrameLayout maskView;
    MenuLayout layout;
    int menuSize = 0;
    private int mNavHeight = 40;
    private int mNavId = 0x100;
    private int mSelectId = 0x101;
    private int mMaskId = 0x102;
    private MenuView.OnItemClickListener onItemClickListener;
    private View contentView;
    private SparseIntArray indexMap = new SparseIntArray();//保存上一次点击的位置
    int tabSelect = 0;
    int oldTabSelect = -1;
    //tab选中图标
    private int menuSelectedIcon;
    //tab未选中图标
    private int menuUnselectedIcon;
    private int menuSelectedNoExtendIcon;
    private int menuTextSize = 14;
    //遮罩颜色
    private static String maskColor = "#888888";
    private long duration = 500;
    boolean isBelowTitle = true;
    private int preId = 0;

    public int getLastPosition() {
        return indexMap.get(tabSelect);
    }

    public int getNavTitleHeight() {
        return DisplayUtil.dip2px(mNavHeight);
    }

    /**
     * 回调接口
     */
    public interface OnItemClickListener<T> {
        /**
         * @param v
         * @param t
         * @param menuIndex 点击位置的 index
         * @param position  菜单位置的 index —> position
         */
        public void onItemClicked(View v, T t, int menuIndex, int position);
    }

    public void setOnItemClickListener(MenuView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MenuView(Context context) {
        this(context, null);
    }

    public MenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.MenuView);
        textSelectColor = a.getColor(R.styleable.MenuView_textSelect, Color.BLACK);
        textUnSelectColor = a.getColor(R.styleable.MenuView_textUnSelect, Color.BLACK);
        menuSelectedIcon = a.getResourceId(R.styleable.MenuView_selectedIcon, menuSelectedIcon);
        menuUnselectedIcon = a.getResourceId(R.styleable.MenuView_unSelectedIcon, menuUnselectedIcon);
        menuSelectedNoExtendIcon = a.getResourceId(R.styleable.MenuView_selectedNoExtendIcon, menuSelectedNoExtendIcon);
        menuTextSize = a.getDimensionPixelSize(R.styleable.MenuView_navTextSize, menuTextSize);
        a.recycle();
        inAnim = AnimationUtils.loadAnimation(context, R.anim.dd_menu_in);
        outAnim = AnimationUtils.loadAnimation(context, R.anim.dd_menu_out);
        inAnim.setDuration(duration);
        outAnim.setDuration(duration);
        initView(context);
    }

    private void initView(Context context) {
        navLayout = new LinearLayout(context);
        navLayout.setId(mNavId);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(mNavHeight));
        navLayout.setBackgroundResource(R.drawable.drawable_bottom_border);
        navLayout.setLayoutParams(lp);
        navLayout.setPadding(DisplayUtil.dip2px(15), 0, DisplayUtil.dip2px(15), 0);
        navLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(navLayout);
        selectLayout = new LinearLayout(context);
        selectLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        selectLayout.setBackgroundResource(R.drawable.drawable_bottom_border);
        selectLayout.setId(mSelectId);
        selectLayout.setOrientation(LinearLayout.VERTICAL);
        addView(selectLayout);
        maskView = new FrameLayout(getContext());
        maskView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        maskView.setForeground(new ColorDrawable(Color.parseColor(maskColor)));
        maskView.getForeground().setAlpha(0);
        maskView.setVisibility(GONE);
        maskView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyTitleChanged(tabSelect, indexMap.get(tabSelect), 2);
                selectLayout.startAnimation(outAnim);
            }
        });
        addView(maskView);
        inAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                selectLayout.setVisibility(VISIBLE);
                maskView.setVisibility(VISIBLE);
                executeAlphaAnimation(0, 150);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                selectLayout.setVisibility(GONE);
                executeAlphaAnimation(150, 0);
                maskView.setVisibility(GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    // 标题隐藏
    public void setNavTitle(int index, String title) {
        if (navLayout.getChildCount() > index) {
            ImageTextView child = (ImageTextView) navLayout.getChildAt(index);
            child.setText(title);
        }
    }

    public void setNavTitle(int visible) {
        navLayout.setVisibility(visible);
    }


    //遮罩层
    private void executeAlphaAnimation(final int from, final int to) {
        alphaAnim = ValueAnimator.ofInt(from, to);
        alphaAnim.setDuration(duration);
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int newAlpha = (int) animation.getAnimatedValue();
                maskView.getForeground().setAlpha(newAlpha);
                if (newAlpha >= to) {
                    alphaAnim.cancel();
                }
            }
        });
        alphaAnim.start();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 2) {
            throw new RuntimeException("Content view must contains one child views at least.");
        }
        maskView.bringToFront();
        selectLayout.bringToFront();
        navLayout.bringToFront();
    }

    public boolean isBelowTitle() {
        return this.isBelowTitle;
    }

    public void setBelowTitle(boolean belowTitle) {
        this.isBelowTitle = belowTitle;
        if (contentView != null) {
            onViewAdded(contentView);
        }
    }

    @Override
    public void onViewAdded(View child) {
        if (child.getId() != mNavId) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (isBelowTitle) {
                lp.addRule(BELOW, mNavId);
            } else {
                lp.addRule(BELOW, 0);
            }
            child.setLayoutParams(lp);

            contentView = (child.getId() != mSelectId && child.getId() != mMaskId) ? child : null;
        }
        super.onViewAdded(child);
    }

    public void bindData(MenuLayout layout) {
        this.layout = layout;
        adapter = layout.adapter;
        menuSize = layout.menuItems.size();
        adapter.setMenuView(this);
        addTab();
    }


    private void addTab() {
        for (int i = 0; i < menuSize; i++) {
            final ImageTextView tab = new ImageTextView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            tab.setLayoutParams(lp);
            tab.setId(i);
            tab.setSingleLine();
            tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize);
            tab.setTextColor(textUnSelectColor);
            tab.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), menuUnselectedIcon), null);
            tab.setGravity(i == 0 ? Gravity.LEFT | Gravity.CENTER_VERTICAL : i == 2 ? Gravity.RIGHT | Gravity.CENTER_VERTICAL : Gravity.CENTER);
            MenuLayout.Builder.MenuItem menuItems = layout.menuItems.get(i);
            indexMap.put(i, menuItems.mSelect);
            adapter.initTitle(menuItems, menuItems.mSelect, tab);
            tab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (oldTabSelect != v.getId() || selectLayout.getVisibility() == View.GONE) {
                        if (oldTabSelect >= 0) {
                            notifyTitleChanged(oldTabSelect, indexMap.get(oldTabSelect), 2);//关闭上次菜单状态
                        }
                        oldTabSelect = v.getId();
                        notifyTitleChanged(v.getId(), indexMap.get(v.getId()), 1);
                        setTabSelect(v.getId());
                        selectLayout.startAnimation(inAnim);
                    }
                }
            });
            navLayout.addView(tab);
        }
    }

    /**
     * @param index          选择一级菜单
     * @param tabSelectIndex 二级
     * @param action         1 展开   2关闭 不更新title 3 更新title
     */
    public void notifyTitleChanged(int index, int tabSelectIndex, int action) {
        if (navLayout.getChildCount() > 0 && index >= 0) {
            ImageTextView textView = (ImageTextView) navLayout.getChildAt(index);
            if (action == 1) {
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), menuSelectedIcon), null);
                textView.setTextColor(textSelectColor);
            } else {
                List<MenuLayout.Builder.MenuItem> menuItems = layout.menuItems;
                int defaultIndex = menuItems.get(index).mSelect;
                textView.setTextColor(tabSelectIndex != defaultIndex ? textSelectColor : textUnSelectColor);
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), tabSelectIndex != defaultIndex ? menuSelectedNoExtendIcon : menuUnselectedIcon), null);
                if (action == 3) {
                    adapter.initTitle(menuItems.get(index), tabSelectIndex, textView);
                }
            }

        }
    }

    public int getTabSelectIndex(int menuIndex) {
        return indexMap.get(menuIndex);
    }

    public void resetPreIndex() {
        indexMap.put(tabSelect, preId);
        notifyTitleChanged(tabSelect, preId, 3);
    }
    public void setTabSelect(final int index) {
        if (adapter == null) {
            return;
        }
        tabSelect = index;
        adapter.setTabIndex(index);
        adapter.replace(layout.menuItems.get(index).dates);
        selectLayout.removeAllViews();
        for (int i = 0; i < adapter.getCount(); i++) {
            final int tmp = i;
            final T obj = (T) adapter.getItem(i);
            final View v = adapter.getView(i, null, null);
            // view 点击事件触发时回调我们自己的接口
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //上一次点击以当前点击相同 直接消失
                    preId = indexMap.get(index);
                    if (indexMap.get(index) != tmp) {
                        indexMap.put(index, tmp);
                    }
                    notifyTitleChanged(index, tmp, 3);
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClicked(v, obj, index, tmp);
                    }
                    selectLayout.startAnimation(outAnim);
                }
            });
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(50));
            v.setLayoutParams(lp1);
            selectLayout.addView(v);
        }
    }
}
