package com.grgbanking.ct.utils;

import android.util.Log;

import com.hlct.framework.pda.common.entity.ResultInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文件读写工具类
 *
 * @author cmy
 */
public class FileUtil {

    /**
     * 判断文件是否存在
     *
     * @param path：文件路径
     * @return boolean
     */
    public static boolean isExist(String path) {
        File file = new File(path);
        boolean status = file.exists();
        Log.d("isExist",""+status);
        return status;
    }

    /**
     * 如果文件不存在，就创建文件
     *
     * @param path 文件路径
     * @return 文件路径
     */
    public static String createIfNotExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    /**
     * 向文件中写入数据
     *
     * @param filePath 目标文件全路径
     * @param data     要写入的数据
     * @return true表示写入成功  false表示写入失败
     */
    public static boolean writeBytes(String filePath, byte[] data) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(data);
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从文件中读取数据
     *
     * @param file
     * @return byte[]
     */
    public static byte[] readBytes(String file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            int len = fis.available();
            byte[] buffer = new byte[len];
            fis.read(buffer);
            fis.close();
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 向文件中写入字符串String类型的内容
     *
     * @param file    文件路径
     * @param content 文件内容
     * @param charset 写入时候所使用的字符集
     */
    public static void writeString(String file, String content, String charset) {
        try {
            byte[] data = content.getBytes(charset);
            writeBytes(file, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 从文件中读取数据，返回类型是ResultInfo类型
     *
     * @param file 文件路径
     * @return
     */
    public static Object readString(String file) {
        ResultInfo ret = null;
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream obj = new ObjectInputStream(in);
            ret = (ResultInfo) obj.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String readTXT(String file) {
        String str = null;
        try {
            File fl = new File(file);
            byte[] by1 = new byte[(int) fl.length()];
            FileInputStream fs1 = new FileInputStream(fl);
            fs1.read(by1);
            fs1.close();
            str = new String(by1, "GBK");
            str = str.replaceAll("\\r\\n", "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 将string类型转化为byte
     *
     * @param str
     * @return
     */
    public static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        }
        byte[] bytes = str.getBytes();
        return bytes;
    }

    /**
     * 获取时间戳
     *
     * @return yyyyMMdd
     */
    public static String getDate() {
        String str = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            Date curDate = new Date(System.currentTimeMillis());
            str = format.format(curDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 遍历获取某目录下制定类型的所有文件
     *
     * @param filePath
     * @param type
     * @return
     */
    public List<String> getFileDir(String filePath, String type) {
        List<String> picList = new ArrayList<String>();
        try {
            File f = new File(filePath);
            File[] files = f.listFiles();// 列出所有文件
            // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (checkIsImageFile(file.getPath(), type)) {
                    picList.add(file.getPath());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 返回得到的图片列表
        return picList;
    }

    /**
     * 检查扩展名，得到图片格式的文件
     *
     * @param fName
     * @param type
     * @return
     */
    private boolean checkIsImageFile(String fName, String type) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals(type)) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }

}