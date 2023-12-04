package com.kanfs.fileop;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.*;
import java.util.Date;
import java.util.regex.Pattern;

public class Filter {

    private String path;
    private String[] typeList;
    private String name;
    private Date startTime;
    private Date endTime;
    private int minSize = 0;
    private int maxSize = Integer.MAX_VALUE;
    private boolean pathRE;
    private boolean nameRE;
    private DateFormat fileDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss['Z']");
    private DateFormat filterDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public Filter(String path, String type, String name, String startTime, String endTime, String minSize,
                  String maxSize, boolean pathRE, boolean nameRE, int minSizeUnit, int maxSizeUnit) throws ParseException{
        if ( startTime != null && !startTime.equals("") ) this.startTime = filterDF.parse(startTime);
        if ( endTime != null && !endTime.equals("")) this.endTime = filterDF.parse(endTime);
        this.path = path;
        this.typeList = type.split("\\|");
        this.name = name;
        if ( minSize != null && !minSize.equals("") ) this.minSize = Integer.parseInt(minSize) * (int)Math.pow(1024,minSizeUnit);
        if ( maxSize != null && !maxSize.equals("") ) this.maxSize = Integer.parseInt(maxSize) * (int)Math.pow(1024,maxSizeUnit);
        this.pathRE = pathRE;
        this.nameRE = nameRE;
    }

    public boolean accept(File file) throws IOException, ParseException {
        boolean flag = true;

        // 获取文件的路径、类型、名字、最后修改时间、大小
        String filePath = file.getPath();
        String fileName = file.getName();
        String fileType = null;
        if ( fileName.lastIndexOf('.') != -1 )
            fileType = fileName.substring(fileName.lastIndexOf('.') + 1);
        BasicFileAttributes timeAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        FileTime lastModifiedTime = timeAttributes.lastModifiedTime();
        String fileDateStr = fileDF.format(lastModifiedTime.toMillis());
        Date fileDate = fileDF.parse(fileDateStr);
        long fileSize = file.length();

        // 依次进行比较筛选
        // 路径
        if ( this.path != null && !this.path.equals("") )
        {
            if ( this.pathRE ) flag = Pattern.matches(this.path, filePath);
            else flag = filePath.contains(path);
            if ( !flag ) return false;
        }
        // 类别
        if ( this.typeList != null && !this.typeList[0].equals("") && fileType != null)
        {
            flag = false;
            for ( String type : typeList)
            {
                if ( fileType.equals(type) )
                {
                    flag = true;
                    break;
                }
            }
            if ( !flag ) return false;
        }
        // 名字
        System.out.println("name = " + name);
        if ( this.name != null && !this.name.equals("") )
        {
            if ( this.nameRE ) flag = Pattern.matches(this.name, fileName);
            else flag = fileName.contains(name);
            if ( !flag ) return false;
        }
        // 时间
        if ( startTime != null ) flag = fileDate.after(startTime);
        if ( !flag ) return false;
        if ( endTime != null ) flag = endTime.before(endTime);
        if ( !flag ) return false;
        // 大小
        if ( Integer.max(0, minSize) > fileSize || fileSize > maxSize ) return false;
        return true;
    }
}
