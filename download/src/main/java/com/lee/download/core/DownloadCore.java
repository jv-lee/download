package com.lee.download.core;

import android.util.Log;

import com.lee.download.listener.DownloadListener;
import com.lee.download.server.DownloadRetrofit;
import com.lee.download.request.DownloadRequest;
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

    private DownloadRequest request;
    private DownloadListener downloadListener;

    public DownloadCore(DownloadRequest request, DownloadListener downloadListener) {
        this.request = request;
        this.downloadListener = downloadListener;
    }

    /**
     * 下载apk
     */
    public void downloadFile() {
        DownloadRetrofit.getInstance()
                .getApi()
                .downloadFile(request.getUrl())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
            downloadListener.onStart();
        }
        try {
            // 改成自己需要的存储位置
            File dir = new File(request.getFilePath());
            if (!dir.exists()) {
                boolean mkdir = dir.mkdir();
            }
            File file = new File(dir, request.getFileName() + request.getFileType());
            Log.e(TAG, "writeResponseBodyToDisk() file=" + file.getPath());
            if (file.exists()) {
                file.delete();
            }
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
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
                        downloadListener.onProgress((int) (100 * fileSizeDownloaded / fileSize));
                    }
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                if (downloadListener != null) {
                    downloadListener.onFinish(file.getPath());
                }
                outputStream.flush();

            } catch (IOException e) {
                if (downloadListener != null) {
                    downloadListener.onError(e.getMessage());
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            if (downloadListener != null) {
                downloadListener.onError(e.getMessage());
            }
        }
    }

}
