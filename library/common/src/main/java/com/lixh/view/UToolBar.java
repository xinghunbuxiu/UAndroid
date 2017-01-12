package com.lixh.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;

public class UToolBar extends Toolbar {

    private TextView mTitleTextView = null;
    private TextView mSubtitleTextView = null;
    private ImageButton mRightIcon = null;
    private TextView mRightTextView = null;
    private ImageView mLogoView;


    private ActionMenuView menu;


    public ActionMenuView getMenus() {
        return menu;
    }

    public ImageButton getRightIcon() {
        return mRightIcon;
    }

    public TextView getRightTextView() {
        return mRightTextView;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    int gravity = Gravity.CENTER_HORIZONTAL;

    public UToolBar(Context context) {
        this(context, null);
    }

    public UToolBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public void setLogo(@DrawableRes int resId) {
        super.setLogo(resId);
        try {
            mLogoView = (ImageView) get("mLogoView");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    /**
     * tittle的显示必须为 false 不然次不管用
     *
     *
     * @param isEnabled
     * @return
     */
    public UToolBar setDisplayShowTitleEnabled(boolean isEnabled) {
        ((AppCompatActivity) getContext()).setSupportActionBar(this);
        (((AppCompatActivity)getContext())).getSupportActionBar().setDisplayShowTitleEnabled(isEnabled);
        return this;
    }

    /**
     * 是否显示返回 默认带返回按钮 子类如果为true
     * 将不显示返回按钮
     *
     * @param isShow
     * @return
     */
    public UToolBar setDisplayHomeAsUpEnabled(boolean isShow) {
        ((AppCompatActivity) getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(isShow);
        setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) getContext()).onBackPressed();
            }
        });
        return this;
    }

    /**
     * 设置返回按钮
     *
     * @param id
     * @param onClickListener
     */
    public void setNavigationIcon(int id, View.OnClickListener onClickListener) {
        setNavigationIcon(id);
        setNavigationOnClickListener(onClickListener);
    }
    public UToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        menu = new ActionMenuView(getContext());
        Toolbar.LayoutParams lp = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);
        menu.setLayoutParams(lp);
        ViewCompat.setLayoutDirection(menu, ViewCompat.LAYOUT_DIRECTION_RTL);


    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mTitleTextView = getTitleValue(title, "mTitleTextView");
    }

    @Override
    public void setSubtitle(CharSequence subtitle) {
        super.setSubtitle(subtitle);
        mSubtitleTextView = getTitleValue(subtitle, "mSubtitleTextView");
    }

    public void setNavigationIcon(@DrawableRes int resId, String backStr) {
        try {
            ImageButton mNavButtonView = (ImageButton) get("mNavButtonView");
            Bitmap bitmap = drawTextToBitmap(resId, backStr);
            mNavButtonView.setImageBitmap(bitmap);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public Bitmap drawTextToBitmap(@DrawableRes int icon, String text) {
        Resources resources = getResources();
        float scale = resources.getDisplayMetrics().density;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setTextSize((int) (14 * scale));
        Bitmap bitmap =
                BitmapFactory.decodeResource(resources, icon);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        Bitmap canvasBmp = Bitmap.createBitmap(bitmap.getWidth() + bounds.width(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasBmp);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawText(text, bitmap.getWidth(), (bitmap.getHeight() + bounds.height()) / 2, paint);
        return canvasBmp;
    }


    public TextView getTitleValue(CharSequence tittle, String fieldName) {
        TextView childTitle = null;
        try {
            childTitle = (TextView) get(fieldName);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return childTitle;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (gravity == Gravity.CENTER_HORIZONTAL) {
            setCenter(mTitleTextView);
            setCenter(mSubtitleTextView);
            setLogoViewCenter(mLogoView);
        }
    }


    /**
     * 增加多个按钮
     *
     * @param view
     */
    public void addActionMenu(View view) {
        removeView(menu);
        menu.addView(view);
        addView(menu);
    }


    /**
     * 右边图片
     *
     * @param resId
     * @param id    资源id
     */
    public void setRightImage(int resId, int id) {
        createRightImage(resId, null, 0);
    }

    /**
     * 右边图片 监听
     *
     * @param resId
     */
    public void setRightImage(int resId, View.OnClickListener clickListener) {
        createRightImage(resId, clickListener, 0);
    }

    /**
     * @param resId 默认id=0
     */
    public void setRightImage(int resId) {
        createRightImage(resId, null, 0);
    }

    /**
     * 添加文字
     *
     * @param str
     * @param clickListener 监听
     */
    public void setRightText(String str, View.OnClickListener clickListener) {
        createRightText(str, clickListener, 1);
    }

    /**
     * 添加文字
     *
     * @param str
     * @param id  资源id
     */
    public void setRightText(String str, int id) {
        createRightText(str, null, id);
    }

    /**
     * 添加文字
     *
     * @param str 默认id=1
     */
    public void setRightText(String str) {
        createRightText(str, null, 1);
    }

    private void createRightImage(int resId, View.OnClickListener clickListener, int id) {
        if (resId == 0) return;
        if (mRightIcon == null) {
            mRightIcon = new AppCompatImageButton(getContext(), null,
                    android.support.v7.appcompat.R.attr.toolbarNavigationButtonStyle);
            mRightIcon.setId(id);
            mRightIcon.setImageResource(resId);
//            mRightIcon.setOnClickListener(clickListener);
            menu.addView(mRightIcon);
            removeView(menu);
            addView(menu);
        }
    }

    private void createRightText(String str, View.OnClickListener clickListener, int id) {
        if (!TextUtils.isEmpty(str)) {
            if (mRightTextView == null) {
                final Context context = getContext();
                mRightTextView = new AppCompatTextView(context);
                mRightTextView.setSingleLine();
                mRightTextView.setEllipsize(TextUtils.TruncateAt.END);
                mRightTextView.setText(str);
//                mRightTextView.setOnClickListener(clickListener);
                menu.addView(mRightTextView);
                removeView(menu);
                addView(menu);
            }

        }
    }


    public void setLogoViewCenter(ImageView logoViewCenter) {
        float tx = 0;
        int deviceWidth = getMeasuredWidth();
        if (logoViewCenter == null) {
            return;
        }
        if (mTitleTextView != null) {
            Paint p = mTitleTextView.getPaint();
            float textWidth = p.measureText(mTitleTextView.getText().toString());
            tx = (deviceWidth - textWidth) / 2.0f - mTitleTextView.getLeft();
        }
        if (mSubtitleTextView != null) {
            Paint p = mSubtitleTextView.getPaint();
            float textWidth = p.measureText(mSubtitleTextView.getText().toString());
            tx = Math.min(tx, (deviceWidth - textWidth) / 2.0f - mSubtitleTextView.getLeft());
        }
        float left = (tx - logoViewCenter.getLeft()) / 2.0f;
        logoViewCenter.setTranslationX(tx);
    }

    public void setCenter(TextView childTitle) {
        if (childTitle == null) {
            return;
        }
        int deviceWidth = getMeasuredWidth();
        Paint p = childTitle.getPaint();
        float textWidth = p.measureText(childTitle.getText().toString());
        float tx = (deviceWidth - textWidth) / 2.0f - childTitle.getLeft();
        childTitle.setTranslationX(tx);
    }

    public Object get(String variableName) throws NoSuchFieldException {
        Class targetClass = getClass().getSuperclass();
        Toolbar superInst = (Toolbar) targetClass.cast(this);
        Field field;
        try {
            field = targetClass.getDeclaredField(variableName);
            //修改访问限制
            field.setAccessible(true);
            return field.get(superInst);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setOnMenuItemClickListener(OnClickListener onClickListener) {
        for (int i=0;i<menu.getChildCount();i++){
            menu.getChildAt(i).setOnClickListener(onClickListener);
        }
    }
}