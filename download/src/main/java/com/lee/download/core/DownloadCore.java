package com.lee.download.core;

import android.content.BroadcastReceiver;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.lee.download.android.AndroidDownloadManager;
import com.lee.download.listener.DownloadListener;
import com.lee.download.request.DownloadRequest;
import com.lee.download.server.DownloadRetrofit;
import com.lee.download.utils.ThreadUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author jv.lee
 * @date 2019/11/12.
 * @description
 */
public class DownloadCore {

    private static final String TAG = "DownloadCore";

    private AndroidDownloadManager androidDownloadManager;

    private DownloadRequest request;
    private DownloadListener downloadListener;
    private Handler handler;
    private long currentSize = 0;

    public DownloadCore(DownloadRequest request, DownloadListener downloadListener) {
        this.request = request;
        this.downloadListener = downloadListener;
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 下载apk
     */
    public void downloadFile() {
        Log.i(TAG, "downloadFile method start .");
        if (request.isAndroid()) {
            androidDownload();
        } else {
            clientDownload();
        }
    }

    public BroadcastReceiver getRecevier(){
        return androidDownloadManager.getReceiver();
    }

    private void androidDownload() {
        androidDownloadManager = new AndroidDownloadManager(request.getContext(), request.getUrl(), request.getFileName() + request.getFileType(), request.getTitle(), request.getDescription())
                .setListener(downloadListener)
                .download();
    }

    private void clientDownload() {
        DownloadRetrofit.getInstance()
                .getApi()
                .downloadFile(request.getUrl())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.i(TAG, "download onResponse");
                        if (response.isSuccessful()) {
                            ThreadUtil.getInstance().addTask(new FileDownloadRun(response));
                        } else {
                            downloadListener.onError(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        downloadListener.onError(t.getMessage());
                    }
                });
    }

    public class FileDownloadRun implements Runnable {
        Response<ResponseBody> mResponseBody;

        private FileDownloadRun(Response<ResponseBody> responseBody) {
            mResponseBody = responseBody;
        }

        @Override
        public void run() {
            writeResponseBodyToDisk(mResponseBody.body());
        }
    }


    /**
     * @param body
     * @return
     */
    private void writeResponseBodyToDisk(ResponseBody body) {
        if (downloadListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    downloadListener.onStart();
                }
            });
        }
        try {
            // 改成自己需要的存储位置
            File dir = new File(request.getFilePath());
            if (!dir.exists()) {
                boolean mkdir = dir.mkdir();
            }
            final File file = new File(dir, request.getFileName() + request.getFileType());
            Log.e(TAG, "writeResponseBodyToDisk() file=" + file.getPath());
            if (file.exists()) {
                file.delete();
            }
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                final long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    //计算当前下载百分比，并经由回调传出
                    if (downloadListener != null) {
                        final long currentProgress = (int) (100 * fileSizeDownloaded / fileSize);
                        if (currentSize == currentProgress) {
                            continue;
                        } else {
                            currentSize = currentProgress;
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                downloadListener.onProgress((int) currentProgress);
                            }
                        });

                    }
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                if (downloadListener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            downloadListener.onFinish(file.getPath());
                        }
                    });
                }
                outputStream.flush();

            } catch (final IOException e) {
                if (downloadListener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            downloadListener.onError(e.getMessage());
                        }
                    });
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (final IOException e) {
            if (downloadListener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadListener.onError(e.getMessage());
                    }
                });
            }
        }
    }

}
