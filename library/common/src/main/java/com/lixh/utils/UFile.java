package com.lixh.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import com.lixh.app.BaseApplication;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * 文件处理util
 */
public class UFile {
    static final String FILES_PATH = "Compressor";
    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private UFile() {

    }

    public static File from(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(context, uri);
        String[] splitName = splitFileName(fileName);
        File tempFile = File.createTempFile(splitName[0], splitName[1]);
        tempFile = rename(tempFile, fileName);
        tempFile.deleteOnExit();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }
        return tempFile;
    }

    /**
     * 获取文件名
     * @param fileName
     * @return
     */
    static String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    /**
     * 根据uri获取文件路径
     * @param context
     * @param uri
     * @return
     */
    static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * 根据uri获取真文件路径
     * @param context
     * @param contentUri
     * @return
     */
    static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String realPath = cursor.getString(index);
            cursor.close();
            return realPath;
        }
    }

    static File rename(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists()) {
                if (newFile.delete()) {
                    Log.d("UFile", "Delete old " + newName + " file");
                }
            }
            if (file.renameTo(newFile)) {
                Log.d("UFile", "Rename file to " + newName);
            }
        }
        return newFile;
    }

    static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
    }

    static long copyLarge(InputStream input, OutputStream output, byte[] buffer)
            throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isExistsFile(String filePath) {
        if (isExistsSdcard()) {// 判断SDcard是否存在
            File directory = new File(filePath);
            if (directory.exists()) {
                return true;
            }
        }
        return false;
    }

    /***
     * 获取项目文件
     *
     * @return
     */
    public static File getDir() {
        String packName = BaseApplication.getAppContext().getPackageName();
        String name = packName.substring(packName.lastIndexOf(".") + 1,
                packName.length());
        File dir = null;
        if ((!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))) {
            dir = BaseApplication.getAppContext().getCacheDir();
        } else {
            dir = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/" + name);
        }
        dir.mkdirs();
        return dir;
    }

    /**
     * 获取项目缓存文件
     *
     * @return
     */
    public static String getCacheDir() {
        File file = new File(getDir().getAbsolutePath() + "/cache");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取项目使用过程中产生的图片文件
     *
     * @return
     */
    public static String getImageDir() {
        File file = new File(getDir().getAbsolutePath() + "/image");
        file.mkdirs();
        return file.getAbsolutePath();
    }

    /**
     * 是否存在SDcard
     *
     * @return
     */
    public static boolean isExistsSdcard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 获取项目使用的数据库
     *
     * @return
     */
    public static String getDbDir() {
        File file = new File(getDir().getAbsolutePath() + "/db");
        file.mkdirs();
        return file.getAbsolutePath();
    }

    /**
     * uri装换文件
     *
     * @param context
     * @param uri
     * @return
     */
    public static File uriToFile(Activity context, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = context.managedQuery(uri, proj, null, null,
                null);
        int actual_image_column_index = actualimagecursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor
                .getString(actual_image_column_index);
        File file = new File(img_path);
        return file;
    }

    /**
     * 写入文件
     *
     * @param in
     * @param file
     */
    public static void write(InputStream in, File file) {
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            while (in.read(buffer) > -1) {
                out.write(buffer);
            }
            out.flush();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入文件
     *
     * @param in
     * @param file
     */
    public static void write(String in, File file, boolean append) {
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file, append);
            fw.write(in);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读文
     *
     * @param file
     * @return
     */
    public static String read(File file) {
        if (!file.exists()) {
            return "";
        }
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            StringBuffer buffer = new StringBuffer();
            String s;
            while ((s = br.readLine()) != null) {
                buffer.append(s);
            }
            return buffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 以字符为单位读取文件，常用于写文本，数字等类型的文件
     *
     * @param fileDir  文件目录
     * @param fileName 文件名
     * @param is
     * @return
     * @throws IOException
     */
    public static boolean wirteFileByChars(String fileDir, String fileName,
                                           FileInputStream is) throws IOException {
        boolean isSuccess = false;
        if (isExistsSdcard()) {// 判断SDcard是否存在
            File directory = new File(fileDir);// 目录
            if (!directory.exists()) {// 如果不存在，创建此目录
                directory.mkdirs();
            }
            File file = new File(fileDir, fileName);
            if (!file.exists()) // 如果不存在，创建此文件
                file.createNewFile();
            PrintWriter pWriter = null;
            if (is != null) {
                pWriter = new PrintWriter(new OutputStreamWriter(
                        new FileOutputStream(file)));
                BufferedReader bReader = new BufferedReader(
                        new InputStreamReader(is));
                String lineContent;
                while ((lineContent = bReader.readLine()) != null) {
                    pWriter.println(lineContent);
                }
                pWriter.flush();
                bReader.close();
                pWriter.close();
                isSuccess = true;
            }
        }
        return isSuccess;
    }

    /**
     * 以字符为单位读取文件，常用于写文本，数字等类型的文件
     *
     * @param fileDir  文件目录
     * @param fileName 文件名
     * @return
     * @throws IOException
     */
    public static boolean wirteFileByChars(String fileDir, String fileName,
                                           String content) throws IOException {
        boolean isSuccess = false;
        if (isExistsSdcard()) {// 判断SDcard是否存在
            File directory = new File(fileDir);// 目录
            if (!directory.exists()) {// 如果不存在，创建此目录
                directory.mkdirs();
            }
            File file = new File(fileDir, fileName);
            if (!file.exists()) // 如果不存在，创建此文件
                file.createNewFile();
            PrintWriter pWriter = null;
            if (content != null) {
                pWriter = new PrintWriter(new OutputStreamWriter(
                        new FileOutputStream(file)));
                pWriter.write(content, 0, content.length());
                pWriter.flush();
                pWriter.close();
                isSuccess = true;
            }
        }
        return isSuccess;
    }

    /**
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     *
     * @param filePath 文件路径
     * @return
     * @throws IOException
     */
    public static String readFileByChars(String filePath) throws IOException {
        if (isExistsSdcard()) {// 判断SDcard是否存在
            File direstory = new File(filePath);
            if (direstory.exists()) {
                BufferedReader bReader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(direstory)));
                StringBuffer sb = new StringBuffer();
                String lineContent;
                while ((lineContent = bReader.readLine()) != null) {
                    sb.append(lineContent);
                }
                bReader.close();
                return sb.toString();
            }
        }
        return null;
    }

    /**
     * 以字节为单位写文件，常用于读二进制文件，如图片、声音、影像等文件
     *
     * @param fileDir  文件目录
     * @param fileName 文件名
     * @param is
     * @return
     * @throws IOException
     */
    public static boolean wirteFileByBytes(String fileDir, String fileName,
                                           InputStream is) throws IOException {
        boolean isSuccess = false;
        if (isExistsSdcard()) {// 判断SDcard是否存在
            File directory = new File(fileDir);// 目录
            if (!directory.exists()) {// 如果不存在，创建此目录
                directory.mkdirs();
            }
            File file = new File(fileDir, fileName);
            if (!file.exists()) // 如果不存在，创建此文件
                file.createNewFile();
            FileOutputStream fileOutPutStream = null;
            if (is != null) {
                fileOutPutStream = new FileOutputStream(file);
                byte[] b = new byte[1024];
                int size = -1;
                while ((size = is.read(b)) != -1) {
                    fileOutPutStream.write(b, 0, size);
                }
                fileOutPutStream.flush();
                is.close();
                fileOutPutStream.close();
                isSuccess = true;
            }
        }
        return isSuccess;
    }

    /**
     * 以字节为单位写文件，常用于读二进制文件，如图片、声音、影像等文件
     *
     * @param filePath 文件路径
     * @return
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public static byte[] readFileByBytes(String filePath) throws IOException {
        if (isExistsSdcard()) {// 判断SDcard是否存在
            File direstory = new File(filePath);
            if (direstory.exists()) {
                FileInputStream fileInputStream = new FileInputStream(direstory);
                ByteBuffer buffer = ByteBuffer.allocate(fileInputStream
                        .available());
                byte[] b = new byte[1024];
                int size = -1;
                while ((size = fileInputStream.read(b)) != -1) {
                    buffer.put(b, 0, size);
                }
                return buffer.array();
            }
        }
        return null;
    }

    static String message;

    /**
     * 将图片写入SDcard中
     *
     * @param fileName
     * @param mBitmap
     * @return
     */
    public static boolean wirteBitmap(String fileDir, String fileName,
                                      Bitmap mBitmap) {
        if (isExistsSdcard()) {// 判断SDcard是否存在
            File directory = new File(fileDir);// 目录
            if (!directory.exists()) {// 如果不存在，创建此目录
                directory.mkdirs();
            }
            File file = new File(fileDir, fileName);// 文件
            if (!file.exists()) {
                try {
                    BufferedOutputStream stream = new BufferedOutputStream(
                            new FileOutputStream(file));// 向文件中写入数据
                    boolean isSuccess = mBitmap.compress(
                            Bitmap.CompressFormat.PNG, 100, stream);
                    if (!isSuccess) {
                        message = "保存失败！";
                    } else {
                        message = "保存成功！";
                    }
                    stream.flush();
                    stream.close();
                    return isSuccess;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                message = "已保存！";
                return true;
            }
        } else {
            message = "SDcard不存在！";
        }
        return false;
    }

    /**
     * 读取SDcard中指定目录下的指定后缀的文件路径
     *
     * @param dir     目录
     * @param postfix 后缀
     * @return
     */
    public static ArrayList<String> readFiles(String dir, String postfix) {


        if (isExistsSdcard()) {// 判断SDcard是否存在
            File directory = new File(dir);// 目录
            if (!directory.exists()) {
                message = "目前没有图片！";
                return null;
            }
            File[] files = directory.listFiles();
            ArrayList<String> filesPath = new ArrayList<String>();
            for (int i = 0; i < files.length; i++) {
                String path = files[i].getAbsolutePath();
                if (path.endsWith(postfix)) {
                    filesPath.add(path);
                }
            }
            return filesPath;
        } else {
            message = "SDcard不存在！";
        }
        return null;
    }

    /**
     * 删除指定文件
     */
    public static boolean deleteFile(String filePath) {
        if (isExistsSdcard()) {// 判断SDcard是否存在
            File directory = new File(filePath);
            if (directory.exists()) {
                return directory.delete();
            }
        }
        return false;
    }

    /**
     * 删除指定文件
     */
    public static boolean deleteDir(File dir) {
        if (!dir.exists()) {
            return true;
        }
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        dir.delete();

        return true;
    }

    /**
     * 删除指定目录中的所有文件
     */
    public static boolean deleteDirFile(String delPath) {
        if (isExistsSdcard()) {// 判断SDcard是否存在
            File directory = new File(delPath);
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirFile(files[i].getAbsolutePath());
                    files[i].delete();
                } else {
                    files[i].delete();
                }
            }
            return true;
        }
        return false;
    }


    /**
     * 返回此抽象路径名表示的文件最后一次被修改的时间
     *
     * @param filePath
     * @return
     */
    public static long lastModified(String filePath) {
        if (isExistsSdcard()) {// 判断SDcard是否存在
            File directory = new File(filePath);
            if (directory.exists()) {
                return directory.lastModified();
            }
        }
        return System.currentTimeMillis();
    }

}
