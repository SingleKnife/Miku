package com.fyd.miku.utils;

import java.io.File;

public class FileUtils {

    public static void deleteDir(String dir) {
        deleteDir(new File(dir));
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
}
