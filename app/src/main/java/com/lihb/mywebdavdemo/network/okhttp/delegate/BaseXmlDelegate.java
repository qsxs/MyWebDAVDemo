package com.lihb.mywebdavdemo.network.okhttp.delegate;

import android.os.Handler;
import android.os.Looper;

import com.lihb.mywebdavdemo.network.okhttp.ResultCallback;

import java.lang.reflect.Type;

import okhttp3.Request;

public class BaseXmlDelegate implements IDelegate {
    private Handler mDelivery = new Handler(Looper.getMainLooper());
    private final ResultCallback<String> DEFAULT_RESULT_CALLBACK = new ResultCallback<String>() {
        @Override
        public void onError(Request request, Exception e) {

        }

        @Override
        public void onResponse(String response) {

        }
    };

    public void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(request, e);
                callback.onAfter();
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(object);
                callback.onAfter();
            }
        });
    }

    @Override
    public void deliveryResult(ResultCallback callback, Request request, Type mClass) {

    }

    @Override
    public void deliveryResult(ResultCallback callback, Request request) {

    }
}
