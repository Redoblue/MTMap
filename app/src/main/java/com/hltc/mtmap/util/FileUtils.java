package com.hltc.mtmap.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.hltc.mtmap.app.AppConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The type File utils.
 *
 * @author Redoblue
 * @version 1.0
 * @created 2015 -4-15
 */
public class FileUtils {

    /**
     * Write void.
     *
     * @param context  the context
     * @param fileName the file name
     * @param content  the content
     */
    public static void write(Context context, String fileName, String content) {
        if (content == null) {
            content = "";
        }
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read string.
     *
     * @param context  the context
     * @param fileName the file name
     * @return the string
     */
    public static String read(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            return readInStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Read in stream.
     *
     * @param inputStream the input stream
     * @return the string
     */
    public static String readInStream(InputStream inputStream) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            baos.close();
            inputStream.close();
            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create file.
     *
     * @param folderPath the folder path
     * @param fileName   the file name
     * @return the file
     */
    public static File createFile(String folderPath, String fileName) {
        File targetDir = new File(folderPath);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        return new File(folderPath, fileName + fileName);
    }

    /**
     * Write file.
     *
     * @param buffer   the buffer
     * @param folder   the folder
     * @param fileName the file name
     * @return the boolean
     */
    public static boolean writeFile(byte[] buffer, String folder, String fileName) {
        boolean success = false;
        boolean sdcardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        String folderPath = "";
        if (sdcardExist) {
            folderPath = Environment.getExternalStorageDirectory() + File.separator + folder + File.separator;
        } else {
            success = false;
        }

        File fileDir = new File(folderPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File file = new File(folderPath + fileName);
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(file);
            out.write(buffer);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    /**
     * Gets file name.
     *
     * @param filePath the file path
     * @return the file name
     */
    public static String getFileName(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return "";
        }
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * Gets app cache dir.
     *
     * @param context the context
     * @param dir     the dir
     * @return the app cache
     */
    public static String getAppCache(Context context, String dir) {
        String savePath = context.getCacheDir().getAbsolutePath() + File.separator + dir + File.separator;
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        saveDir = null;
        return savePath;
    }

    /**
     * Gets all dirs.
     *
     * @param dir the dir
     * @return the dirs
     */
    public static List<String> getAllDirs(String dir) {
        List<String> allDir = new ArrayList<String>();
        SecurityManager checker = new SecurityManager();
        File path = new File(dir);
        checker.checkRead(dir);
        // 过滤掉以.开始的文件夹
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                if (f.isDirectory() && !f.getName().startsWith(".")) {
                    allDir.add(f.getAbsolutePath());
                }
            }
        }
        return allDir;
    }

    /**
     * Gets all files.
     *
     * @param dir the dir
     * @return the all files
     */
    public static List<File> getAllFiles(String dir) {
        List<File> files = new ArrayList<File>();
        SecurityManager manager = new SecurityManager();
        File path = new File(dir);
        manager.checkRead(dir);
        File[] all = path.listFiles();
        for (File f : all) {
            if (f.isFile())
                files.add(f);
            else
                getAllDirs(f.getAbsolutePath());
        }
        return files;
    }

    public static boolean checkFileExistence(String path) {
        return new File(path).exists();
    }


    public static void saveBitmap(Bitmap bm, String picName) {
        Log.e("", "保存图片");
        try {
            if (!isFileExist("")) {
                File tempf = createSDDir("");
            }
            File f = new File(AppConfig.DEFAULT_APP_ROOT_PATH + "photo/", picName + ".JPEG");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.e("", "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File createSDDir(String dirName) throws IOException {
        File dir = new File(AppConfig.DEFAULT_APP_ROOT_PATH + "photo/" + dirName);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            System.out.println("createSDDir:" + dir.getAbsolutePath());
            System.out.println("createSDDir:" + dir.mkdir());
        }
        return dir;
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(AppConfig.DEFAULT_APP_ROOT_PATH + "photo/" + fileName);
        file.isFile();
        return file.exists();
    }

    public static void delFile(String fileName) {
        File file = new File(AppConfig.DEFAULT_APP_ROOT_PATH + "photo/" + fileName);
        if (file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    public static void deleteDir() {
        File dir = new File(AppConfig.DEFAULT_APP_ROOT_PATH + "photo/");
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }
}
