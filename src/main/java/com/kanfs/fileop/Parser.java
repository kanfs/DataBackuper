package com.kanfs.fileop;

public class Parser {
    String sourcePath;
    String backupPath;
    String newFileName;
    boolean[] attributes;
    Filter filter;
    public Parser(String sourcePath, String backupPath, String newFileName, boolean[] attributes, Filter filter) {
        this.sourcePath = sourcePath;
        this.backupPath = backupPath;
        this.newFileName = newFileName;
        this.attributes = attributes;
        this.filter = filter;
    }
}
