package com.kanfs.main;

import java.io.File;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        // 图形化界面接收源文件路径和目的路径
        String sourcePath = "source_path";
        String backupPath = "backup_path";

        //备份
        bcakup(sourcePath, backupPath);

        //还原
        restore(sourcePath, backupPath);

    }

    private static void bcakup(String sourcePath, String backupPath) {
        File sourceFile = new File(sourcePath);
        // 根据备份的是文件还是目录作不同处理
        if ( sourceFile.isDirectory() )
        {
            // 复制文件夹
            File backupFile = new File(backupPath, sourceFile.getName());
            copyDirectory(sourceFile.toPath(), backupFile.toPath());
            System.out.println(backupFile.getAbsoluteFile() + " Back up Completed");
        } else if ( sourceFile.isFile() ) {
            // 复制文件
            File backupFile = new File(backupPath, sourceFile.getName());
            copyFile(sourceFile.toPath(), backupFile.toPath());
            System.out.println(backupFile.getAbsoluteFile() + " Back up Completed");
        } else {
            System.out.println("Unsupported source type.");
        }
    }

    private static void copyFile(Path sourcePath, Path backupPath) {
    }

    private static void copyDirectory(Path sourcePath, Path backupPath) {
    }

    private static void restore(String sourcePath, String backupPath) {
    }
}