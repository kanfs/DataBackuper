package com.kanfs.fileop;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.*;
import java.util.List;

public class Basic {
    public static void backup(Parser parser) {

        try {
            // 打开文件
            File sourceFile = new File(parser.sourcePath);
            File backupFile = new File(parser.backupPath + parser.newFileName);

            // 拷贝文件
            copyFile(sourceFile, backupFile, parser.attributes);
            System.out.println(backupFile.getAbsoluteFile() + " Back up Completed");

        } catch (FileNotFoundException e) {
            System.out.println("unable to find target file.");
            throw new RuntimeException(e);
        } catch ( IOException e) {
            System.out.println("unable to copy file.");
            throw new RuntimeException(e);
        }
    }

    private static void copyFile(File sourceFile, File backupFile, boolean[] attributes) throws IOException {
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
                copyFile(sourceFile, backupFile, attributes);
            }
        }else{
            // 读取源文件元数据
            if ( attributes[0] ) // owner
            {
                FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(sourceFile.toPath(), FileOwnerAttributeView.class);
                UserPrincipal owner = ownerAttributeView.getOwner();
                Files.getFileAttributeView(backupFile.toPath(), FileOwnerAttributeView.class).setOwner(owner);
            }
            if ( attributes[1] ) // time  creationTime + lastAccessTime + lastModifiedTime
            {
                BasicFileAttributes sourceTimeAttributes = Files.readAttributes(sourceFile.toPath(), BasicFileAttributes.class);
                FileTime creationTime = sourceTimeAttributes.creationTime();
                FileTime lastModifiedTime = sourceTimeAttributes.lastModifiedTime();
                FileTime lastAccessTime = sourceTimeAttributes.lastAccessTime();
                Files.getFileAttributeView(backupFile.toPath(), BasicFileAttributeView.class).setTimes(lastModifiedTime, lastAccessTime, creationTime);
            }
            if ( attributes[2] ) // acl
            {
                AclFileAttributeView sourceACLAttributes = Files.getFileAttributeView(sourceFile.toPath(), AclFileAttributeView.class);
                List<AclEntry> acl = sourceACLAttributes.getAcl();
                Files.getFileAttributeView(backupFile.toPath(), AclFileAttributeView.class).setAcl(acl);
            }

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

    public static void restore(Parser parser) {
        try {
            // 打开文件
            File sourceFile = new File(parser.sourcePath + parser.newFileName);
            File backupFile = new File(parser.backupPath);

            // 拷贝文件
            copyFile(backupFile, sourceFile, parser.attributes);
            System.out.println(sourceFile.getAbsoluteFile() + " Restored Completed");

        } catch (FileNotFoundException e) {
            System.out.println("unable to find back up file.");
            throw new RuntimeException(e);
        } catch ( IOException e) {
            System.out.println("unable to copy file.");
            throw new RuntimeException(e);
        }
    }

    // 文件元数据拷贝
}
