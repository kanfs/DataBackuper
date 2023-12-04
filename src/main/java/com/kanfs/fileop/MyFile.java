package com.kanfs.fileop;

import java.io.*;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MyFile implements Serializable{
    String name; // 文件名
    String path; // 文件相对路径
    boolean isDirectory; // 目录与否
    long size; // 文件大小
    transient FileTime creationTime; // 创建时间
    transient FileTime lastModifiedTime; // 最后修改时间
    transient FileTime lastAccessTime; // 最后访问时间
    private DateFormat fileDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final long serialVersionUID = 6943331267174557950L;


    public MyFile(String name, String path, boolean isDirectory, long size, FileTime creationTime, FileTime lastModifiedTime, FileTime lastAccessTime) {
        this.name = name;
        this.path = path;
        this.isDirectory = isDirectory;
        this.size = size;
        this.creationTime = creationTime;
        this.lastModifiedTime = lastModifiedTime;
        this.lastAccessTime = lastAccessTime;
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject(); // 默认序列化
        // 手动序列化FileTime，将其转换为字符串
        String ct = creationTime.toString();
        if( ct.indexOf('.')!=-1 ) ct = ct.substring(0, ct.indexOf('.'))+"Z";
        out.writeObject(ct);
        String lmt = lastModifiedTime.toString();
        if( lmt.indexOf('.')!=-1 ) lmt = lmt.substring(0, lmt.indexOf('.'))+"Z";
        out.writeObject(lmt);
        String lat = lastAccessTime.toString();
        if( lat.indexOf('.')!=-1 ) lat = lat.substring(0, lat.indexOf('.'))+"Z";
        out.writeObject(lat);
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException, ParseException {
        in.defaultReadObject(); // 默认反序列化
        // 手动反序列化 FileTime
        String ct = (String) in.readObject();
        Date ctDate = fileDF.parse(ct);
        creationTime = FileTime.fromMillis(ctDate.getTime());
        String lmt = (String) in.readObject();
        Date lmtDate = fileDF.parse(lmt);
        lastModifiedTime = FileTime.fromMillis(lmtDate.getTime());
        String lat = (String) in.readObject();
        Date latDate = fileDF.parse(lat);
        lastAccessTime = FileTime.fromMillis(latDate.getTime());
    }
    @Serial
    private void readObjectNoData() throws ObjectStreamException
    {

    }

    // 获取对象对应的byte数组
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(this);
        }
        return byteArrayOutputStream.toByteArray();
    }

}
