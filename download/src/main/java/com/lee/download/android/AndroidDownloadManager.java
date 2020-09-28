package com.lee.download.android;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.lee.download.listener.DownloadListener;
import com.lee.download.request.DownloadRequest;

import java.io.File;

/**
 * @author jv.lee
 * @date 2019/11/19.
 * @description
 */
public class AndroidDownloadManager {
    private DownloadManager downloadManager;
    private Context context;
    private long downloadId;
    private String url;
    private String name;
    private String title;
    private String description;

    private String path;

    private DownloadListener listener;

    /**
     * @param context     上下文
     * @param url         下载地址
     * @param name        apk文件名 app.apk
     * @param title       下载任务标题
     * @param description 下载任务描述
     */
    public AndroidDownloadManager(Context context, String url, String name, String title, String description) {
        this.context = context;
        this.url = url;
        this.name = name;
        this.title = title;
        this.description = description;
    }

    public AndroidDownloadManager setListener(DownloadListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 开始下载
     */
    public AndroidDownloadManager download() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(false);
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle(title);
        request.setDescription(description);
        request.setVisibleInDownloadsUi(true);
        if (name.indexOf(DownloadRequest.TYPE_APK) > 0) {
            request.setMimeType("application/vnd.android.package-archive");
        }

        //设置下载的路径
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), name);
        if (file.exists()) {
            file.delete();
        }
        request.setDestinationUri(Uri.fromFile(file));
        path = file.getAbsolutePath();

        //获取DownloadManager
        if (downloadManager == null) {
            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        }
        //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
        if (downloadManager != null) {
            if (listener != null) {
                listener.onStart();
            }
            downloadId = downloadManager.enqueue(request);
        }

        //注册广播接收者，监听下载状态
        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        return this;
    }

    public BroadcastReceiver getReceiver() {
        return receiver;
    }

    /**
     * 广播监听下载的各个状态
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query query = new DownloadManager.Query();
            //通过下载的id查找
            query.setFilterById(downloadId);

            Cursor cursor = downloadManager.query(query);
            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    //下载暂停
                    case DownloadManager.STATUS_PAUSED:
                        break;
                    //下载延迟
                    case DownloadManager.STATUS_PENDING:
                        break;
                    //正在下载
                    case DownloadManager.STATUS_RUNNING:
                        break;
                    //下载完成
                    case DownloadManager.STATUS_SUCCESSFUL:
                        if (listener != null) {
                            listener.onFinish(path);
                        }
                        cursor.close();
                        context.unregisterReceiver(this);
                        break;
                    //下载失败
                    case DownloadManager.STATUS_FAILED:
                        if (listener != null) {
                            listener.onError("Download Error ...");
                        }
                        cursor.close();
                        context.unregisterReceiver(this);
                        break;
                    default:
                }
            }
        }
    };


    // ——————————————————————私有方法———————————————————————

    /**
     * 通过URL获取文件名
     *
     * @param url
     * @return
     */
    private static final String getFileNameByUrl(String url) {
        String filename = url.substring(url.lastIndexOf("/") + 1);
        filename = filename.substring(0, filename.indexOf("?") == -1 ? filename.length() : filename.indexOf("?"));
        return filename;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public String getPath() {
        return path;
    }
}
