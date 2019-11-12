package com.lee.download.request;

import android.os.Environment;

/**
 * @author jv.lee
 * @date 2019-11-13
 * @description
 */
public class DownloadRequest {

    public static final String TYPE_APK = ".apk";
    public static final String TYPE_JPG = ".jpg";
    public static final String TYPE_PNG = ".png";
    public static final String TYPE_MP4 = ".mp4";
    public static final String TYPE_MP3 = ".mp3";
    public static final String TYPE_WAV = ".wav";

    private final String url;
    private final String fileName;
    private final String fileType;
    private final String filePath;

    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFilePath() {
        return filePath;
    }


    public static class Builder {
        private String url;
        private String fileName = String.valueOf(System.currentTimeMillis());
        private String fileType;
        private String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setFileType(String fileType) {
            this.fileType = fileType;
            return this;
        }

        public Builder setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public DownloadRequest build() {
            return new DownloadRequest(this);
        }
    }

    private DownloadRequest(Builder builder) {
        if (builder.url == null) {
            throw new IllegalArgumentException("DownloadRequest url 参数为必填值");
        } else if (builder.fileType == null) {
            throw new IllegalArgumentException("DownloadRequest fileType 参数为必填值");
        }
        this.url = builder.url;
        this.fileName = builder.fileName;
        this.fileType = builder.fileType;
        this.filePath = builder.filePath;
    }

    public static Builder create(){
        return new Builder();
    }

}
