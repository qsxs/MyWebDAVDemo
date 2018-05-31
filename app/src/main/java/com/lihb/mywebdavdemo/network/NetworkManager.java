package com.lihb.mywebdavdemo.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lihb.mywebdavdemo.App;
import com.lihb.mywebdavdemo.KeyConfig;
import com.lihb.mywebdavdemo.network.webdav.WebDavConverterFactory;
import com.lihb.mywebdavdemo.utils.Base64Util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * 请求网络类
 * Created by lihuabin on 2017/12/4.
 */

public class NetworkManager {
    private Retrofit retrofit;
    //    private static final String TAG = "NetworkManager";
//    private IApi service;
    private Map<Class, Object> serviceMap = new HashMap<>();

    private NetworkManager() {
        // HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //   日志拦截器
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("NetworkManager", message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        File cacheFile = new File(App.context().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(IApi.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(IApi.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor)
                .addNetworkInterceptor(new HttpCacheInterceptor())
                .cache(cache)
                .build();


        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
//                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(WebDavConverterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(IApi.BASE_URL)
                .build();

    }

    //  创建单例
    private static class SingletonHolder {
        private static final NetworkManager INSTANCE = new NetworkManager();
    }

    public static IApi getApiService() {
        Class clazz = IApi.class;
        IApi iApi = (IApi) SingletonHolder.INSTANCE.serviceMap.get(clazz);
        if (iApi == null) {
            iApi = (IApi) SingletonHolder.INSTANCE.retrofit.create(clazz);
            SingletonHolder.INSTANCE.serviceMap.put(clazz, iApi);
        }
//        service = retrofit.create(clazz);
        return iApi;
    }

    public static <T> T getApiService(Class<T> clazz) {
        Object iApi = SingletonHolder.INSTANCE.serviceMap.get(clazz);
        if (iApi == null) {
            iApi = SingletonHolder.INSTANCE.retrofit.create(clazz);
            SingletonHolder.INSTANCE.serviceMap.put(clazz, iApi);
        }
//        service = retrofit.create(clazz);
        return (T) iApi;
    }


    class HttpCacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            Request.Builder builder = request.newBuilder();
//            String token = SpHelper.getToken();
//            if (!TextUtils.isEmpty(token)) {
//                builder.addHeader("LM-Token", token);
//                LogUtil.si("NetworkManager", "token:" + token);
//            }
            request = builder
                    .addHeader("Authorization", "Basic " + Base64Util.encode(KeyConfig.WEBDAV_USERNAME + ":" + KeyConfig.WEBDAV_PASSWORD))
                    .build();

            Response originalResponse = chain.proceed(request);
            return originalResponse;
//            if (CheckUtil.isNetworkConnected(App.context())) {
            //有网的时候读接口上的@Headers里的配置，可以在这里进行统一的设置
//            String cacheControl = request.cacheControl().toString();
//            return originalResponse.newBuilder()
//                    .header("Cache-Control", cacheControl)
//                    .addHeader("LM-Token", token)
//                    .addHeader("Content-Type", "text/plain;application/json")
//                    .removeHeader("Pragma")
//                    .build();
//            } else {
//                return originalResponse.newBuilder()
//                        .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
//                        .removeHeader("Pragma")
//                        .build();
//            }
        }
    }
}
