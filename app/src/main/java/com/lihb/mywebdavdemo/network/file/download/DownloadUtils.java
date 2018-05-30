package com.lihb.mywebdavdemo.network.file.download;

import android.support.annotation.NonNull;
import android.util.Log;

import com.lihb.mywebdavdemo.network.IApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Description: 下载工具类
 * Created by jia on 2017/11/30.
 * 人之所以能，是相信能
 */
public class DownloadUtils {

    private static final String TAG = "DownloadUtils";

    private static final int DEFAULT_TIMEOUT = 15;

    private Retrofit retrofit;

    private JsDownloadListener listener;

    private String baseUrl;

    private String downloadUrl;

    public interface DownloadService {
        @Streaming
        @GET
        Observable<ResponseBody> download(@Url String url);
    }

    public DownloadUtils(JsDownloadListener listener) {

        this.baseUrl = IApi.BASE_URL;
        this.listener = listener;

        JsDownloadInterceptor mInterceptor = new JsDownloadInterceptor(listener);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i(TAG, message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(mInterceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 开始下载
     *
     * @param url
     * @param filePath
     * @param subscriber
     */
    public void download(@NonNull String url, final String filePath, final DefaultObserver<InputStream> subscriber) {

        listener.onStartDownload();

        // subscribeOn()改变调用它之前代码的线程
        // observeOn()改变调用它之后代码的线程
        retrofit.create(DownloadService.class)
                .download(url)
                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, InputStream>() {
                    @Override
                    public InputStream apply(ResponseBody responseBody) throws Exception {
                        return responseBody.byteStream();
                    }
                })
                .observeOn(Schedulers.io()) // 用于计算任务
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) throws Exception {
                        writeFile(inputStream, filePath);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<InputStream>() {
                    @Override
                    public void onNext(InputStream o) {
                        if(subscriber!=null){
                            subscriber.onNext(o);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(subscriber!=null){
                            subscriber.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (listener != null) {
                            listener.onFinishDownload();
                        }
                        if(subscriber!=null){
                            subscriber.onComplete();
                        }
                    }
                });

    }

    /**
     * 将输入流写入文件
     *
     * @param inputString
     * @param filePath
     */
    private void writeFile(InputStream inputString, String filePath) {
        Log.i(TAG, "writeFile: " + filePath);
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);

            byte[] b = new byte[1024];

            int len;
            while ((len = inputString.read(b)) != -1) {
                fos.write(b, 0, len);
            }
        } catch (FileNotFoundException e) {
            if (listener != null) {
                listener.onFail("下载失败");
            }
        } catch (IOException e) {
            if (listener != null) {
                listener.onFail("保存失败");
            }
        } finally {
            try {
                inputString.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
