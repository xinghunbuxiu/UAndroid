package com.p2peye.remember.ui.capital.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.p2peye.remember.R;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by LIXH on 2017/7/31.
 * email lixhVip9@163.com
 * des
 */
public class MenuAdapter extends BaseMenuAdapter<String> {


    public MenuAdapter(Context context) {
        super(context, R.layout.menu_layout);
    }


    @Override
    public void convert(MenuHolder helper, String str, int lastPosition) {
        TextView title = helper.getView(R.id.title);
        ImageView rightImg = helper.getView(R.id.img_right);
        View topLine = helper.getView(R.id.topLine);
        topLine.setVisibility(helper.mPosition == 0 ? View.GONE : View.VISIBLE);
        title.setTextColor(helper.mPosition == lastPosition ? ContextCompat.getColor(context, R.color.color_4E8BED) : ContextCompat.getColor(context, R.color.color_39495F));
        if (helper.mPosition == lastPosition) {
            rightImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.menu_selected));
        } else {
            rightImg.setImageDrawable(null);
        }
        title.setText(str);

    }

    @Override
    public void initTitle(MenuLayout.Builder.MenuItem menuItem, int tmp, ImageTextView textView) {
        List<String> datas = menuItem.getDates();
        String title = datas.get(tmp);
        String c_time = DateTime.now().toString("MM");
        if (title.equals("本月")) {
            title = c_time + "\u2000本月";
        }
        textView.setText(title);

    }
}

