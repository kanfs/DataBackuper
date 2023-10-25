package com.kanfs.fileop;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class Basic {
    public static void backup(String sourcePath, String backupPath) {
        try {
            // 打开文件
            File sourceFile = new File(sourcePath);
            File backupFile = new File(backupPath);

            // 拷贝文件
            copyFile(sourceFile, backupFile);
            System.out.println(backupFile.getAbsoluteFile() + " Back up Completed");

        } catch (FileNotFoundException e) {
            System.out.println("unable to find target file.");
            throw new RuntimeException(e);
        } catch ( IOException e) {
            System.out.println("unable to copy file.");
            throw new RuntimeException(e);
        }
    }

    private static void copyFile(File sourceFile, File backupFile) throws IOException {
        if( sourceFile.isDirectory() )
        {
            // 创建目标文件夹
            backupFile.mkdir();

            // 获取源文件夹下的所有子文件和子文件夹
            String[] files = sourceFile.list();
            for (String filePath : files)
            {
                // 打开文件
                File subSourceFile = new File(sourceFile.getAbsoluteFile()+filePath);
                File subBackupFile = new File(backupFile.getAbsoluteFile()+filePath);

                // 递归拷贝文件
                copyFile(sourceFile, backupFile);
            }
        }else{
            // 创建带缓冲区的IO流
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(backupFile));

            // 文件复制
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = bis.read(buff)) != -1)
                bos.write(buff, 0, len);

            // 关闭IO流
            bis.close();
            bos.close();
        }

    }

    public static void restore(String sourcePath, String backupPath) {
        try {
            // 打开文件
            File sourceFile = new File(sourcePath);
            File backupFile = new File(backupPath);

            // 拷贝文件
            copyFile(backupFile, sourceFile);
            System.out.println(sourceFile.getAbsoluteFile() + " Restored Completed");

        } catch (FileNotFoundException e) {
            System.out.println("unable to find back up file.");
            throw new RuntimeException(e);
        } catch ( IOException e) {
            System.out.println("unable to copy file.");
            throw new RuntimeException(e);
        }
    }

}
