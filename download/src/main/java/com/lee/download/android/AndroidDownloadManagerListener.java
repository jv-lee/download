package com.lee.download.android;

/**
 * @author jv.lee
 * @date 2019/11/19.
 * @description
 */
public interface AndroidDownloadManagerListener {
    void onPrepare();

    void onSuccess(String path);

    void onFailed(Throwable throwable);
}
