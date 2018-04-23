/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lixh.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Base64;

import com.lixh.app.BaseApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Map;
import java.util.Set;

/**
 * Created by lfh on 2016/8/13.
 */
public class SharedPreferencesUtil {

    private static SharedPreferencesUtil prefsUtil;
    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;

    public synchronized static SharedPreferencesUtil getInstance() {

        return getInstance(BaseApplication.getAppContext().getPackageName(), Context.MODE_PRIVATE);
    }

    public static SharedPreferencesUtil getInstance(String prefsName, int mode) {
        prefsUtil = new SharedPreferencesUtil();
        prefsUtil.prefs = BaseApplication.getAppContext().getSharedPreferences(prefsName, mode);
        prefsUtil.editor = prefsUtil.prefs.edit();
        return prefsUtil;
    }

    private SharedPreferencesUtil() {
    }


    public boolean getBoolean(String key, boolean defaultVal) {
        return prefs.getBoolean(key, defaultVal);
    }

    public boolean getBoolean(String key) {
        return prefs.getBoolean(key, false);
    }


    public String getString(String key, String defaultVal) {
        return prefs.getString(key, defaultVal);
    }

    public String getString(String key) {
        return prefs.getString(key, null);
    }

    public int getInt(String key, int defaultVal) {
        return prefs.getInt(key, defaultVal);
    }

    public int getInt(String key) {
        return prefs.getInt(key, 0);
    }


    public float getFloat(String key, float defaultVal) {
        return prefs.getFloat(key, defaultVal);
    }

    public float getFloat(String key) {
        return prefs.getFloat(key, 0f);
    }

    public long getLong(String key, long defaultVal) {
        return prefs.getLong(key, defaultVal);
    }

    public long getLong(String key) {
        return prefs.getLong(key, 0l);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getStringSet(String key, Set<String> defaultVal) {
        return prefs.getStringSet(key, defaultVal);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getStringSet(String key) {
        return prefs.getStringSet(key, null);
    }

    public Map<String, ?> getAll() {
        return prefs.getAll();
    }

    public boolean exists(String key) {
        return prefs.contains(key);
    }


    public SharedPreferencesUtil putString(String key, String value) {
        editor.putString(key, value);
        return this;
    }

    public SharedPreferencesUtil putInt(String key, int value) {
        editor.putInt(key, value);
        return this;
    }

    public SharedPreferencesUtil putFloat(String key, float value) {
        editor.putFloat(key, value);
        return this;
    }

    public SharedPreferencesUtil putLong(String key, long value) {
        editor.putLong(key, value);
        return this;
    }

    public SharedPreferencesUtil putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        return this;
    }

    public void commit() {
        if (editor != null)
            editor.commit();
    }

    public void apply() {
        if (editor != null)
            editor.apply();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SharedPreferencesUtil putStringSet(String key, Set<String> value) {
        editor.putStringSet(key, value);
        return this;
    }

    public void putObject(String key, Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            editor.putString(key, objectVal);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public <T> T getObject(String key, Class<T> clazz) {
        if (prefs.contains(key)) {
            String objectVal = prefs.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public SharedPreferencesUtil remove(String key) {
        editor.remove(key);
        return this;
    }

    public SharedPreferencesUtil removeAll() {
        editor.clear();
        return this;
    }
}
