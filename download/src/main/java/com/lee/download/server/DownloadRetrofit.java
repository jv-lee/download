package com.lee.download.server;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author jv.lee
 * @date 2019/11/12.
 * @description
 */
public class DownloadRetrofit {

    private static DownloadRetrofit instance;
    private Api api;

    public synchronized static DownloadRetrofit getInstance() {
        if (instance == null) {
            instance = new DownloadRetrofit();
        }
        return instance;
    }

    private DownloadRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder().client(client)
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://www.baidu.com")
                .build();
        api = retrofit.create(Api.class);
    }

    public Api getApi() {
        return api;
    }

    public interface Api {
        @Streaming
        @GET
        Call<ResponseBody> downloadFile(@Url String url);
    }
}
