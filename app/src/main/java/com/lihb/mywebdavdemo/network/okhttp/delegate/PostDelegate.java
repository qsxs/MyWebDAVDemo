package com.lihb.mywebdavdemo.network.okhttp.delegate;

import android.text.TextUtils;
import android.util.Log;

import com.lihb.mywebdavdemo.network.okhttp.OkParams;
import com.lihb.mywebdavdemo.network.okhttp.ResultCallback;

import java.io.File;
import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PostDelegate extends BaseGsonDelegate{
    private final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream;charset=utf-8");
    private final MediaType MEDIA_TYPE_STRING = MediaType.parse("text/plain;charset=utf-8");

    /**
     * 异步的post请求
     */
    public void postAsyn(String url, OkParams params, final ResultCallback callback, Type mClass) {
        Request request = buildPostFormRequest(url, params);
        deliveryResult(callback, request, mClass);
    }

    /**
     * 异步的post请求
     */
    public void postAsyn(String url, OkParams params, final ResultCallback callback) {
        Request request = buildPostFormRequest(url, params);
        deliveryResult(callback, request);
    }


    /**
     * 直接将bodyStr以写入请求体
     */
    public void postAsyn(String url, String bodyStr, final ResultCallback callback, Type mClass) {
        postAsyn(url, bodyStr, MediaType.parse("text/plain;charset=utf-8"), callback, mClass);
    }

    /**
     * 直接将bodyStr以写入请求体
     */
    public void postAsyn(String url, String bodyStr, MediaType type, final ResultCallback callback, Type mClass) {
        RequestBody body = RequestBody.create(type, bodyStr);
        Request request = buildPostRequest(url, body);
        deliveryResult(callback, request, mClass);
    }

    /**
     * 直接将bodyFile以写入请求体
     */
    public void postAsyn(String url, File bodyFile, final ResultCallback callback, Type mClass) {
        postAsyn(url, bodyFile, MediaType.parse("application/octet-stream;charset=utf-8"), callback, mClass);
    }

    /**
     * 直接将bodyFile以写入请求体
     */
    public void postAsyn(String url, File bodyFile, MediaType type, final ResultCallback callback, Type mClass) {
        RequestBody body = RequestBody.create(type, bodyFile);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .tag(url)
                .build();
        deliveryResult(callback, request, mClass);
    }

    /**
     * 直接将bodyBytes以写入请求体
     */
    public void postAsyn(String url, byte[] bodyBytes, final ResultCallback callback, Type mClass) {
        postAsyn(url, bodyBytes, MediaType.parse("application/octet-stream;charset=utf-8"), callback, mClass);
    }

    /**
     * 直接将bodyBytes以写入请求体
     */
    public void postAsyn(String url, byte[] bodyBytes, MediaType type, final ResultCallback callback, Type mClass) {
        RequestBody body = RequestBody.create(type, bodyBytes);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .tag(url)
                .addHeader("User-Agent", userAgent)
                .build();
        deliveryResult(callback, request, mClass);
    }

    private Request buildPostRequest(String url, RequestBody body) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .tag(url)
                .addHeader("User-Agent", userAgent)
                .build();
        return request;
    }

    private Request buildPostFormRequest(String url, OkParams params) {
        if (params == null) {
            params = new OkParams();
        }
        String specifiedVersion = params.getSpecifiedVersion();

        if (TextUtils.isEmpty(specifiedVersion)) {
            specifiedVersion = vesion;
        }
        IdentityHashMap<String, String> urlParams = params.getUrlParams();

        FormBody.Builder builder = new FormBody.Builder();
        Set<IdentityHashMap.Entry<String, String>> entries = urlParams.entrySet();
        for (IdentityHashMap.Entry<String, String> entry : entries) {
            builder.add(entry.getKey(), entry.getValue());
        }

        RequestBody requestBody = builder.build();
        if (TextUtils.isEmpty(userAgent)) {
            userAgent = "BaiYangStore/" + specifiedVersion + " (Android; " + android.os.Build.VERSION.RELEASE + "; Scale/2.00)" + "android_360;";
        }
        Log.i(TAG, "User-Agent -----> " + userAgent);
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .tag(url)
                .addHeader("User-Agent", userAgent)
                .build();
    }
}
