package com.lee.download.request;

import android.content.Context;
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

    private final Context context;
    private final String url;
    private final String fileName;
    private final String fileType;
    private final String filePath;
    private final boolean isAndroid;
    private final String title;
    private final String description;

    public Context getContext() {
        return context;
    }

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

    public boolean isAndroid() {
        return isAndroid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public static class Builder {
        private Context context;
        private String url;
        private String fileName = String.valueOf(System.currentTimeMillis());
        private String fileType;
        private String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        private boolean isAndroid = false;
        private String title;
        private String description;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder isAndroid(boolean isAndroid) {
            this.isAndroid = isAndroid;
            return this;
        }

        public Builder setNotificationTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setNotificationDescription(String description) {
            this.description = description;
            return this;
        }

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
        this.context = builder.context;
        this.isAndroid = builder.isAndroid;
        this.url = builder.url;
        this.fileName = builder.fileName;
        this.fileType = builder.fileType;
        this.filePath = builder.filePath;
        this.title = builder.title;
        this.description = builder.description;
    }

    public static Builder create(Context context) {
        return new Builder(context);
    }

}
