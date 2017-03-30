package com.lixh.utils;

import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 判断通知栏背景颜色
 * Created by dengqu on 2016/12/12.
 */
public class NotificationsUtils {
    private final static String TAG = NotificationsUtils.class.getSimpleName();
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
    private static final double COLOR_THRESHOLD = 180.0;
    private static int titleColor;

    /**
     * 判断通知栏背景颜色，现在手机通知栏大部分不是白色就是黑色背景
     *
     * @param context
     * @return
     */
    public static boolean isDarkNotifyCationBar(Context context) {
        return !isColorSimilar(Color.BLACK, getNotificationColor(context));
    }

    private static int getNotificationColor(Context context) {
        if (context instanceof AppCompatActivity) {
            return getNotificationColorCompat(context);
        } else {
            return getNotificationColorInternal(context);
        }
    }

    private static boolean isColorSimilar(int baseColor, int color) {
        int simpleBaseColor = baseColor | 0xff000000;
        int simpleColor = color | 0xff000000;
        int baseRed = Color.red(simpleBaseColor) - Color.red(simpleColor);
        int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleColor);
        int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleColor);
        double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
        if (value < COLOR_THRESHOLD) {
            return true;
        }
        return false;
    }

    private static int getNotificationColorInternal(Context context) {
        final String DUMMY_TITLE = "DUMMY_TITLE";
        Builder builder = new Builder(context);
        builder.setContentText(DUMMY_TITLE);
        Notification notification = builder.build();
        ViewGroup notificationRoot = (ViewGroup) notification.contentView.apply(context, new FrameLayout(context));
        final TextView titleView = (TextView) notificationRoot.findViewById(android.R.id.title);
        if (titleView == null) {
            iteratoryView(notificationRoot, new Filter() {
                @Override
                public void filter(View view) {
                    if (view instanceof TextView) {
                        TextView textView = (TextView) view;
                        if (DUMMY_TITLE.equals(textView.getText().toString())) {
                            titleColor = textView.getCurrentTextColor();
                        }
                    }
                }
            });
            return titleColor;
        } else {
            return titleView.getCurrentTextColor();
        }

    }

    private static int getNotificationColorCompat(Context context) {
        Builder builder = new Builder(context);
        Notification notification = builder.build();
        int layoutId = notification.contentView.getLayoutId();
        ViewGroup notificationRoot = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null);
        final TextView titleView = (TextView) notificationRoot.findViewById(android.R.id.title);
        if (titleView == null) {
            final List<TextView> textViews = new ArrayList<>();
            iteratoryView(notificationRoot, new Filter() {
                @Override
                public void filter(View view) {
                    textViews.add((TextView) view);
                }
            });

            float minTextSize = Integer.MIN_VALUE;
            int index = 0;
            for (int i = 0, j = textViews.size(); i < j; i++) {
                float currentSize = textViews.get(i).getTextSize();
                if (currentSize > minTextSize) {
                    minTextSize = currentSize;
                    index = i;
                }
            }
            return textViews.get(index).getCurrentTextColor();
        } else {
            return titleView.getCurrentTextColor();
        }
    }

    private static void iteratoryView(View view, Filter filter) {
        if (view == null || filter == null) {
            return;
        }
        filter.filter(view);
        if (view instanceof ViewGroup) {
            ViewGroup container = (ViewGroup) view;
            for (int i = 0, j = container.getChildCount(); i < j; i++) {
                View child = container.getChildAt(i);
                iteratoryView(child, filter);
            }
        }
    }

    private interface Filter {
        void filter(View view);
    }
}