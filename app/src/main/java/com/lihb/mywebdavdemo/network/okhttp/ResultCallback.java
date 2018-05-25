package com.lihb.mywebdavdemo.network.okhttp;

import okhttp3.Request;

/**
 * 网络回调
 * Created by zhengwenjie on 2015/9/7.
 */
public abstract class ResultCallback<T> {
    public boolean showLog = true;
    public boolean showErrorMsg = true;
    public boolean showFailedMsg = true;
    /**
     * 之前
     */
    public void onBefore() {
    }

    /**
     * 之后
     */
    public void onAfter() {
    }

    /**
     * 错误的请求
     * @param request
     * @param e
     */
    public abstract void onError(Request request, Exception e);

    /**
     * 请求响应
     * @param response
     */
    public abstract void onResponse(T response);
}