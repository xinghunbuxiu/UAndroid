package com.lixh.utils;

import android.content.Context;

import com.lixh.app.BaseApplication;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件工具类
 * 
 * @author gdpancheng@gmail.com 2013-10-22 下午1:08:52
 */
public class UProperties {
	public final static String TAG = "Tools_Properties";

	/**
	 * 根据文件名和文件路径 读取Properties文件

	 * @param fileName
	 * @param dirName
	 * @return 设定文件
	 */
	public static Properties loadProperties(String fileName, String dirName) {
		Properties props = new Properties();
		try {
			int id = BaseApplication.getAppResources()
					.getIdentifier(fileName, dirName,
							BaseApplication.getAppContext().getPackageName());
			props.load(BaseApplication.getAppContext().getResources()
					.openRawResource(id));
		} catch (Exception e) {
			ULog.e(TAG, e.toString());
		}
		return props;
	}

	/**
	 * 读取Properties文件(指定目录)
	 *
	 * @param file
	 * @return 设定文件
	 */
	public static Properties loadConfig(String file) {
		Properties properties = new Properties();
		try {
			FileInputStream s = new FileInputStream(file);
			properties.load(s);
		} catch (Exception e) {
			ULog.e(TAG, e.toString());
		}
		return properties;
	}

	/**
	 * 保存Properties(指定目录)
	 *
	 * @param file
	 * @param properties
	 *            设定文件
	 */
	public static void saveConfig(String file, Properties properties) {
		try {
			FileOutputStream s = new FileOutputStream(file, false);
			properties.store(s, "");
		} catch (Exception e) {
			ULog.e(TAG, e.toString());
		}
	}

	/**
	 * 读取文件 文件在/data/data/package_name/files下 无法指定位置
	 * @param fileName
	 * @return 设定文件
	 */
	public static Properties loadConfigNoDirs(String fileName) {
		Properties properties = new Properties();
		try {
			FileInputStream s = BaseApplication.getAppContext().openFileInput(
					fileName);
			properties.load(s);
		} catch (Exception e) {
			ULog.e(TAG, e.toString());
		}
		return properties;
	}

	/**
	 * 保存文件到/data/data/package_name/files下 无法指定位置
	 *
	 * @param fileName
	 * @param properties
	 *            设定文件
	 */
	public static void saveConfigNoDirs(String fileName, Properties properties) {
		try {
			FileOutputStream s = BaseApplication.getAppContext().openFileOutput(
					fileName, Context.MODE_PRIVATE);
			properties.store(s, "");
		} catch (Exception e) {
			ULog.e(TAG, e.toString());

		}
	}

	public static Properties loadConfigAssets(String fileName) {

		Properties properties = new Properties();
		try {
			InputStream is = BaseApplication.getAppContext().getAssets()
					.open(fileName);
			properties.load(is);
		} catch (Exception e) {
			ULog.e(TAG, e.toString());
		}
		return properties;
	}
}
