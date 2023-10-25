package com.kanfs.fileop;

public class Parser {
    String sourcePath;
    String backupPath;
    String newFileName;
    boolean[] attributes;
    public Parser(String sourcePath, String backupPath, String newFileName, boolean[] attributes) {
        this.sourcePath = sourcePath;
        this.backupPath = backupPath;
        this.newFileName = newFileName;
        this.attributes = attributes;
    }
}
