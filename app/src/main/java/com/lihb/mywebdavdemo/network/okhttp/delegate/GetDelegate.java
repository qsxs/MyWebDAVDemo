package com.lihb.mywebdavdemo.network.okhttp.delegate;

import com.lihb.mywebdavdemo.network.okhttp.ResultCallback;

import java.lang.reflect.Type;

import okhttp3.Request;

public class GetDelegate extends BaseGsonDelegate{
    /**
     * 异步的get请求
     */
    public void getAsyn(String url, final ResultCallback callback, Type type) {
        final Request request = new Request.Builder()
                .url(url)
                .tag(url)
//                .addHeader("User-Agent", userAgent)
                .build();
        deliveryResult(callback, request, type);
    }
}
