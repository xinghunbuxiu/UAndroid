package com.lixh.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.lixh.R;
import com.lixh.setting.AppConfig;

import java.io.File;

/**
 * Description : 图片加载工具类 使用glide框架封装
 */
public class ImageLoaderUtils {
    static int ic_image_loading = AppConfig.ImageUtilConfig.ic_image_loading;
    static int ic_empty_picture = AppConfig.ImageUtilConfig.ic_empty_picture;
    static int ic_round_picture = AppConfig.ImageUtilConfig.ic_round_picture;

    public static void display(Context context, ImageView imageView, String url, int placeholder, int error) {
        if (imageView == null) {
            return;
        }
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(error);
        try {
            Glide.with(context).load(url)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void display(Context context, ImageView imageView, String url) {
        if (imageView == null) {
            return;
        }
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(ic_image_loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(ic_empty_picture);
        try {
            Glide.with(context).load(url)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void display(Context context, ImageView imageView, File url) {
        if (imageView == null) {
            return;
        }
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(ic_image_loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(ic_empty_picture);
        try {
            Glide.with(context).load(url)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displaySmallPhoto(Context context, ImageView imageView, String url) {
        if (imageView == null) {
            return;
        }
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(ic_image_loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(ic_empty_picture);
        try {
            Glide.with(context).load(url)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayBigPhoto(Context context, ImageView imageView, String url) {
        if (imageView == null) {
            return;
        }
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(ic_image_loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(ic_empty_picture);
        try {
            Glide.with(context).load(url)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void display(Context context, ImageView imageView, int url) {
        if (imageView == null) {
            return;
        }
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(ic_image_loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(ic_empty_picture);
        try {
            Glide.with(context).load(url)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayRound(Context context, ImageView imageView, String url) {
        if (imageView == null) {
            return;
        }
        RequestOptions requestOptions = RequestOptions.circleCropTransform()
                .dontAnimate()
                .placeholder(ic_round_picture)
                .circleCrop()
                .error(ic_round_picture);
        try {
            Glide.with(context).load(url)
                    .apply(requestOptions).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
