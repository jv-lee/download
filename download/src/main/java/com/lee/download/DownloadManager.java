package com.lee.download;

import com.lee.download.core.DownloadCore;
import com.lee.download.listener.DownloadListener;
import com.lee.download.request.DownloadRequest;

/**
 * @author jv.lee
 * @date 2019-11-13
 * @description
 */
public class DownloadManager {

    public static void download(DownloadRequest request, DownloadListener downloadListener) {
        new DownloadCore(request, downloadListener).downloadFile();
    }

}
