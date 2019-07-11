package com.lixh.utils;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * author: rsw
 * created on: 2018/10/24 上午11:55
 * description:序列化工具类，可用于序列化对象到文件或从文件反序列化对象
 */

public class SerializeUtils {
    /**
     * 从文件反序列化对象
     *
     * @param tag
     * @return
     * @throws RuntimeException if an error occurs
     */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Object deserializationSync(final Context context, final String tag, final boolean delete) {
        try (
                FileInputStream fileInputStream = context.openFileInput(tag);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            Object object = objectInputStream.readObject();
            if (delete) {
                context.deleteFile(tag);
            }
            return object;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //同步序列化
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void serializationSync(Context context, final String tag, final Object obj) {
        if (null == obj) {
            return;
        }

        try (FileOutputStream fileOutputStream = context.openFileOutput(tag, Context.MODE_PRIVATE);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}