package com.kanfs.fileop;

public class Parser {
    String sourcePath;
    String backupPath;
    String newFileName;
    boolean[] attributes;
    Filter filter;
    boolean compress;
    boolean decompress;
    String cipher;
    boolean encrypt;
    boolean decrypt;
    public Parser(String sourcePath, String backupPath, String newFileName, boolean[] attributes, Filter filter,
                  boolean compress, boolean decompress, String cipher, boolean encrypt, boolean decrypt) {
        this.sourcePath = sourcePath;
        this.backupPath = backupPath;
        this.newFileName = newFileName;
        this.attributes = attributes;
        this.filter = filter;
        this.compress = compress;
        this.decompress = decompress;
        this.cipher = cipher;
        this.encrypt = encrypt;
        this.decrypt = decrypt;
    }
}
