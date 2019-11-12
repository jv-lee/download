package com.test.download;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lee.download.DownloadManager;
import com.lee.download.listener.DownloadListener;
import com.lee.download.request.DownloadRequest;

import java.io.File;

/**
 * @author jv.lee
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadRequest request = DownloadRequest.create()
                .setUrl("")
                .setFileName("app")
                .setFileType(DownloadRequest.TYPE_APK)
                .setFilePath(getFilesDir().getAbsolutePath()+ File.separator+"file")
                .build();
        DownloadManager.download(request, new DownloadListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int p) {

            }

            @Override
            public void onFinish(String path) {

            }

            @Override
            public void onError(String msg) {

            }
        });

    }
}
