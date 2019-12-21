package com.test.download;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lee.download.DownloadManager;
import com.lee.download.listener.DownloadListener;
import com.lee.download.request.DownloadRequest;
import com.lee.permission.annotation.Permission;
import com.lee.permission.annotation.PermissionCancel;
import com.lee.permission.annotation.PermissionDenied;

import java.io.File;

/**
 * @author jv.lee
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = findViewById(R.id.progress);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });
        findViewById(R.id.btn_android).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidDownload();
            }
        });
    }

    @Permission(value = {Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode = 200)
    public void download() {
        DownloadRequest request = DownloadRequest.create(this)
                .setFileName("app")
                .setFileType(DownloadRequest.TYPE_APK)
                .setFilePath(getFilesDir() + File.separator + "apk")
                .setUrl("http://3g.163.com/links/4636")
                .build();

        DownloadManager.download(request, new DownloadListener() {
            @Override
            public void onStart() {
                Toast.makeText(MainActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(int p) {
                Log.i(TAG, "onProgress: " + p);
                progress.setProgress(p);
            }

            @Override
            public void onFinish(String path) {
                Toast.makeText(MainActivity.this, "下载完毕:" + path, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(MainActivity.this, "下载失败:" + msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Permission(value = Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode = 201)
    public void androidDownload() {
        DownloadRequest request = DownloadRequest.create(this)
                .isAndroid(true)
                .setNotificationTitle("testapk1")
                .setNotificationDescription("test")
                .setFileName("testapk1")
                .setFileType(DownloadRequest.TYPE_APK)
//                .setUrl("http://3g.163.com/links/4636")
                .setUrl("http://39.108.172.174:8284/iDana_com.ididid2.yo924_1.1.8_118_20190924_release.apk")
                .build();

        DownloadManager.download(request, new DownloadListener() {
            @Override
            public void onStart() {
                Toast.makeText(MainActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(int p) {
                Log.i(TAG, "onProgress: " + p);
                progress.setProgress(p);
            }

            @Override
            public void onFinish(String path) {
                Toast.makeText(MainActivity.this, "下载完毕:" + path, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onFinish: "+path);
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(MainActivity.this, "下载失败:" + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @PermissionDenied
    public void permissionDenied() {
        Toast.makeText(this, "权限申请失败", Toast.LENGTH_SHORT).show();
    }

    @PermissionCancel
    public void permissionCancel() {
        Toast.makeText(this, "权限申请被取消", Toast.LENGTH_SHORT).show();
    }

}
