package com.lihb.mywebdavdemo.network.okhttp.delegate;

import com.lihb.mywebdavdemo.network.okhttp.ResultCallback;

import java.lang.reflect.Type;

import okhttp3.Request;

public interface IDelegate {
    void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback);

    void sendSuccessResultCallback(final Object object, final ResultCallback callback);

    /**
     * 解析结果
     *
     * @param callback
     * @param request
     */
    void deliveryResult(ResultCallback callback, Request request, final Type mClass);

    /**
     * 解析结果
     *
     * @param callback
     * @param request
     */
    void deliveryResult(ResultCallback callback, Request request);
}
