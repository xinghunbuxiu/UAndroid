package com.lixh.setting;


import com.lixh.R;

/**
 * ************************************************************************
 * **                              _oo0oo_                               **
 * **                             o8888888o                              **
 * **                             88" . "88                              **
 * **                             (| -_- |)                              **
 * **                             0\  =  /0                              **
 * **                           ___/'---'\___                            **
 * **                        .' \\\|     |// '.                          **
 * **                       / \\\|||  :  |||// \\                        **
 * **                      / _ ||||| -:- |||||- \\                       **
 * **                      | |  \\\\  -  /// |   |                       **
 * **                      | \_|  ''\---/''  |_/ |                       **
 * **                      \  .-\__  '-'  __/-.  /                       **
 * **                    ___'. .'  /--.--\  '. .'___                     **
 * **                 ."" '<  '.___\_<|>_/___.' >'  "".                  **
 * **                | | : '-  \'.;'\ _ /';.'/ - ' : | |                 **
 * **                \  \ '_.   \_ __\ /__ _/   .-' /  /                 **
 * **            ====='-.____'.___ \_____/___.-'____.-'=====             **
 * **                              '=---='                               **
 * ************************************************************************
 * **                        佛祖保佑      镇类之宝                         **
 * ************************************************************************
 */
public class AppConfig {

    /**
     * The constant DEBUG_TAG.
     */
    public static final String DEBUG_TAG = "logger";// LogCat的标记
    /* 自动更新配置*/
    //fire.im的token
    public static String API_FIRE_TOKEN = "a4f8aa03dc120fc81fcc96464fd03a4b";
    //fire.im的应用id
    public static String APP_FIRE_ID = "57e8ccd4ca87a851e4001199";
    public static int TITLE_ID = 999999;

    /**
     * 配置图片的默认头像
     */
    public static class ImageUtilConfig {
        public static int ic_image_loading = R.mipmap.ic_image_loading;
        public static int ic_empty_picture = R.mipmap.ic_empty_picture;
        public static int ic_round_picture = R.mipmap.ic_round_picture;
    }

}
