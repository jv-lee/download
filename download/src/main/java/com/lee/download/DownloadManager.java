package com.lee.download;

import android.content.BroadcastReceiver;

import com.lee.download.core.DownloadCore;
import com.lee.download.listener.DownloadListener;
import com.lee.download.request.DownloadRequest;

/**
 * @author jv.lee
 * @date 2019-11-13
 * @description
 */
public class DownloadManager {

    private static DownloadCore downloadCore;

    public static void download(DownloadRequest request, DownloadListener downloadListener) {
        downloadCore = new DownloadCore(request, downloadListener);
        downloadCore.downloadFile();
    }

    public static BroadcastReceiver getRecevier() {
        if (downloadCore != null) {
            return downloadCore.getRecevier();
        }
        return null;
    }

}
