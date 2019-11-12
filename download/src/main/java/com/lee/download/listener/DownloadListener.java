package com.lee.download.listener;

/**
 * @author jv.lee
 * @date 2019/11/12.
 * @description
 */
public interface DownloadListener {
    void onStart();
    void onProgress(int p);
    void onFinish(String path);
    void onError(String msg);
}
