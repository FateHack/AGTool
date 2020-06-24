package com.fate.AGTool;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {

    /**
     * 获取路径下所有文件夹名
     *
     * @param path
     * @return
     */
    public static List<String> getAllDirs(String path) {
        File file = new File(path);
        List<String> allDirPath = new ArrayList<String>();
        if (file != null) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File file1 : files) {
                        if (file1.isDirectory()) {
                            allDirPath.add(file1.getName());
                        }
                    }
                }
            }
        }
        Collections.sort(allDirPath);
        return allDirPath;
    }


    /**
     * 获取路径下所有文件
     *
     * @param path
     * @return
     */
    public static List<String> getAllFiles(String path) {
        File file = new File(path);
        List<String> allDirPath = new ArrayList<String>();
        if (file != null) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    if (file1.isDirectory()) {
                        allDirPath.add(file1.getName());
                    }else {
                        if(file1.getName().contains(".so")){
                            allDirPath.add(file1.getName());
                        }
                    }
                }
            }
        }
        Collections.sort(allDirPath);
        return allDirPath;
    }
}
