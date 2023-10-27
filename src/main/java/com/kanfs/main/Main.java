package com.kanfs.main;

import com.kanfs.fileop.Filter;
import com.kanfs.gui.MainWindow;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        new MainWindow();
    }

//    @Test
//    public void test() throws ParseException, IOException {
//        File sourceFile = new File( "C:\\Users\\kanfs\\Desktop\\SoftwareDevelopment\\modaoshi\\新建文本文档.txt" );
//        File backupFile = new File("C:\\Users\\kanfs\\Desktop\\Virus\\mofashi");
//        //Filter filter = new Filter("", "txt|pdf|mp4|cpp", "", "", "", "", "", false, false);
//        System.out.println(sourceFile.length());
////        BasicFileAttributes timeAttributes = Files.readAttributes(sourceFile.toPath(), BasicFileAttributes.class);
////        FileTime lastModifiedTime = timeAttributes.lastModifiedTime();
////        System.out.println(lastModifiedTime.toMillis());
////        String fileDate = filter.fileDF.format(lastModifiedTime.toMillis());
////        System.out.println(fileDate);
////        copyFile(sourceFile, backupFile, new boolean[]{false, false, false}, filter);
//
//    }

}