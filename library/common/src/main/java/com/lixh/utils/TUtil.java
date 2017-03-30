package com.lixh.utils;

import com.lixh.BuildConfig;

import java.lang.reflect.ParameterizedType;

/**
 * 类转换初始化
 */
public class TUtil {
    public static <T> T getT(Object o, int i) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[i])
                    .newInstance();
        } catch (InstantiationException e) {
            if (BuildConfig.LOG_DEBUG) {
                e.printStackTrace();
            }
        } catch (IllegalAccessException e) {
            if (BuildConfig.LOG_DEBUG) {
                e.printStackTrace();
            }
        } catch (ClassCastException e) {
            if (BuildConfig.LOG_DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
