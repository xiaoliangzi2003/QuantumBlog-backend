package org.example.quantumblog.util;

import java.io.File;

/**
 * 文件工具类
 * @author xiaol
 */
public class FileUtils {
    public static void deleteDirectory(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteDirectory(f);
            }
        }
        file.delete();
    }
}
