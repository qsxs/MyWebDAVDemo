package com.lihb.mywebdavdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.lihb.mywebdavdemo.network.IApi;
import com.lihb.mywebdavdemo.network.webdav.MultiStatus;
import com.lihb.mywebdavdemo.network.webdav.Status;
import com.lihb.mywebdavdemo.network.webdav.xml.DomUtil;
import com.lihb.mywebdavdemo.utils.Base64Util;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    OkHttpClient okHttpClient;
    String value;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
//                LogUtil.si("NetworkManager", message);
                Log.i(TAG, message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        File cacheFile = new File(App.context().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb

        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(IApi.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(IApi.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor)
//                .addNetworkInterceptor(new NetworkManager.HttpCacheInterceptor())
                .cache(cache)
                .build();

        value = "Basic " + Base64Util.encode(KeyConfig.WEBDAV_USERNAME + ":" + KeyConfig.WEBDAV_PASSWORD);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Request request = new Request.Builder()
                        .url("https://dav.jianguoyun.com/dav/%e6%88%91%e7%9a%84%e5%9d%9a%e6%9e%9c%e4%ba%91/")
                        .addHeader("Authorization", value)
//                        .addHeader("Content-Type", "Mozilla/5.0 (Android) ownCloud-android/2.7.0")
                        .method("PROPFIND", null)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i(TAG, "onFailure: ");
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        Log.i(TAG, "onResponse: ");
                        InputStream in = response.body().byteStream();
                        if (in != null) {
                            // read response and try to build a xml document
                            try {
                                Document document = DomUtil.parseDocument(in);
                                MultiStatus xml = MultiStatus.createFromXml(document.getDocumentElement());
                                Status[] status = xml.getResponses()[0].getStatus();
                                DavResumeModel davResumeModel = DavResumeModel.create(xml);
                                Log.i(TAG, "onResponse: ");
//                                xml.getResponses()[0]
                            } catch (ParserConfigurationException e) {
                                IOException exception =
                                        new IOException("XML parser configuration error");
                                exception.initCause(e);
                                throw exception;
                            } catch (SAXException e) {
                                IOException exception = new IOException("XML parsing error");
                                exception.initCause(e);
                                throw exception;
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw e;
                            } finally {
                                in.close();
                            }
                        }

//                NetworkManager.getApiService().propfind().subscribe(new DefaultObserver<Response>() {
//                    @Override
//                    public void onNext(Response response) {
//                        Log.i(TAG, "onNext: "+response.toString());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.i(TAG, "onError: "+e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.i(TAG, "onComplete: ");
//                    }
//                });
                    }
                });

            }
        });
    }
}