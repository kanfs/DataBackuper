package com.kanfs.fileop;

import com.kanfs.gui.ErrorDialog;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.*;

import static com.kanfs.fileop.AESCoder.*;
import static com.kanfs.fileop.Packer.*;

public class Basic {
    public static void backup(Parser parser) throws IOException, ParseException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
        // 打开文件
        File sourceFile = new File(parser.sourcePath);
        // 拷贝文件
        if ( parser.compress ) // 打包压缩
        {
            File backupFile = new File(parser.backupPath + "/" + parser.newFileName+".kan");
            packFile(sourceFile, backupFile, parser);
            System.out.println(backupFile.getAbsoluteFile() + " Back up Zipped");
        }else { // 直接复制
            File backupFile = new File(parser.backupPath + "/" + parser.newFileName + getFileExtension(sourceFile.getName()));
            copyFile(sourceFile, backupFile, parser);
            System.out.println(backupFile.getAbsoluteFile() + " Back up Completed");
        }
    }

    public static void restore(Parser parser) throws IOException, ClassNotFoundException, ParseException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
        File backupFile = new File(parser.backupPath);
        if( parser.decompress ) // 需要解包
        {
            File sourceFile = new File(parser.sourcePath+File.separator+parser.newFileName);
            unpackFile(sourceFile, backupFile, parser);
        }
        else // 直接还原
        {
            File sourceFile = new File(parser.sourcePath+File.separator+parser.newFileName+getFileExtension(parser.backupPath));
            copyFile(backupFile, sourceFile, parser);
        }
    }

    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }

    // DFS 复制文件(夹)的直接办法
    private static boolean copyFile(File sourceFile, File backupFile, Parser parser) throws IOException, ParseException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        boolean flag = false;
        if( sourceFile.isDirectory() )
        {
            // 创建目标文件夹
            backupFile.mkdir();

            // 获取源文件夹下的所有子文件和子文件夹
            String[] files = sourceFile.list();
            for (String filePath : files)
            {
                // 打开文件
                File subSourceFile = new File(sourceFile.getAbsolutePath()+"/"+filePath);
                File subBackupFile = new File(backupFile.getAbsolutePath()+"/"+filePath);

                // 递归拷贝文件
                flag |= copyFile(subSourceFile, subBackupFile, parser);
            }
            if ( !flag ) backupFile.delete(); // 若文件夹下没有任意一个文件被复制 删除这个文件夹
            else copyAttributes(sourceFile, backupFile, parser.attributes); // 否则复制元数据
        }else{
            // 查看是否通过筛选器
            if ( parser.filter != null && !parser.filter.accept(sourceFile) ) return false;
            flag = true;

            // 创建带缓冲区的IO流
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(backupFile));

            // 文件复制
            // 加密
            if ( parser.encrypt ) encryptFile(bis, bos, parser.cipher);
            // 解密
            else if ( parser.decrypt ) decryptFile(bis, bos, parser.cipher);
            // 普通处理
            else bos.write(bis.readAllBytes());

            // 关闭IO流
            bis.close();
            bos.close();

            copyAttributes(sourceFile, backupFile, parser.attributes); //复制元数据

        }
        return flag;
    }

    private static void copyAttributes(File sourceFile, File backupFile, boolean[] attributes) throws IOException {
        // 读取源文件元数据写入到备份文件中
        if ( attributes[0] ) // owner
        {
            FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(sourceFile.toPath(), FileOwnerAttributeView.class);
            UserPrincipal owner = ownerAttributeView.getOwner();
            Files.getFileAttributeView(backupFile.toPath(), FileOwnerAttributeView.class).setOwner(owner);
        }
        if ( attributes[2] ) // acl
        {
            AclFileAttributeView sourceACLAttributes = Files.getFileAttributeView(sourceFile.toPath(), AclFileAttributeView.class);
            List<AclEntry> acl = sourceACLAttributes.getAcl();
            Files.getFileAttributeView(backupFile.toPath(), AclFileAttributeView.class).setAcl(acl);
        }
        if ( attributes[1] ) // time  creationTime + lastAccessTime + lastModifiedTime
        {
            BasicFileAttributes sourceTimeAttributes = Files.readAttributes(sourceFile.toPath(), BasicFileAttributes.class);
            FileTime creationTime = sourceTimeAttributes.creationTime();
            FileTime lastModifiedTime = sourceTimeAttributes.lastModifiedTime();
            FileTime lastAccessTime = sourceTimeAttributes.lastAccessTime();
            Files.getFileAttributeView(backupFile.toPath(), BasicFileAttributeView.class).setTimes(lastModifiedTime, lastAccessTime, creationTime);
        }
    }

}

