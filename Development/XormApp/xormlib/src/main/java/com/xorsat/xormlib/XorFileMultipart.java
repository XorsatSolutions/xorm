package com.xorsat.xormlib;

import okhttp3.MediaType;

/**
 * Created by khawar on 5/9/16.
 */
public class XorFileMultipart {
    private String name;
    private String fileName;
    private MediaType fileType;
    private String filePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public MediaType getFileType() {
        return fileType;
    }

    public void setFileType(String strFileType) {
        this.fileType = MediaType.parse(strFileType);
        // this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
