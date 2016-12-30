package com.lixh.uandroid.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

import com.lixh.uandroid.R;
import com.lixh.view.UToolBar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Main2Activity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    UToolBar toolbar;
    @Bind(R.id.button1)
    ImageButton button1;
    String dd = "&quot密码卡";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initTitleBar();
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



    private void initTitleBar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("年份的的多出dfddfdfdfd");
        toolbar.setSubtitle("dddddddddddddd");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.back_normal,"返回dfdfdfd");
//        button1.setImageBitmap(drawTextToBitmap(R.mipmap.back_normal, "dddddd"));

    }
}
