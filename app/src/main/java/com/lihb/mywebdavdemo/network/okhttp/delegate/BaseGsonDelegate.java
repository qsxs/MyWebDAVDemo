package com.lihb.mywebdavdemo.network.okhttp.delegate;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lihb.mywebdavdemo.BuildConfig;
import com.lihb.mywebdavdemo.network.okhttp.OkHttpClientManager;
import com.lihb.mywebdavdemo.network.okhttp.ResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

class BaseGsonDelegate implements IDelegate {
    static final String TAG = "BaseDelegate";
    static String vesion = "1.0";
    static String userAgent = "";
    private Gson mGson = new Gson();
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

    /**
     * 解析结果
     *
     * @param callback
     * @param request
     */
    public void deliveryResult(ResultCallback callback, Request request, final Type mClass) {
        if (callback == null) callback = DEFAULT_RESULT_CALLBACK;
        final ResultCallback resCallBack = callback;
        //UI thread
        callback.onBefore();
        OkHttpClientManager.getInstance().getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedStringCallback(call.request(), e, resCallBack);
            }

            @Override
            public void onResponse(Call call, Response response) {
                String string = "";
                try {
                    string = response.body().string();
                    if (TextUtils.isEmpty(string)) {
                        sendFailedStringCallback(response.request(), new IllegalStateException("response is empty(\"\" or null"), resCallBack);
                    } else {
                        if (mClass == String.class) {
                            if (BuildConfig.DEBUG && resCallBack.showLog) {
                                Log.e(TAG, string);
                            }
                            sendSuccessResultCallback(string, resCallBack);
                        } else {
                            Object o = mGson.fromJson(string, mClass);
                            if (BuildConfig.DEBUG && resCallBack.showLog) {
                                Log.i(TAG, string);
                            }

                            sendSuccessResultCallback(o, resCallBack);
                        }
                    }

                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, resCallBack);
                } catch (com.google.gson.JsonParseException e) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "onResponse: ", e);
                        Log.e(TAG, string);

                        Observable.just(1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new DefaultObserver<Integer>() {
                            @Override
                            public void onNext(Integer integer) {
//                                Toast.makeText("json解析出错，详情请查看logcat");
                                Log.e(TAG, "json解析出错，详情请查看logcat");
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });

                    }
                    sendFailedStringCallback(response.request(), e, resCallBack);
                }


            }

//            @Override
//            public void onFailure(final Request request, final IOException e) {
//                sendFailedStringCallback(request, e, resCallBack);
//            }
//
//            @Override
//            public void onResponse(final Response response) {
//                String string = "";
//                try {
//                    string = response.body().string();
//                    if (TextUtils.isEmpty(string)) {
//                        sendFailedStringCallback(response.request(), new IllegalStateException("response is empty(\"\" or null"), resCallBack);
//                    } else {
//                        if (mClass == String.class) {
//                            if (BuildConfig.DEBUG && resCallBack.showLog) {
//                                Logger.e(string);
//                            }
//                            sendSuccessResultCallback(string, resCallBack);
//                        } else {
//                            Object o = mGson.fromJson(string, mClass);
//                            if (BuildConfig.DEBUG && resCallBack.showLog) {
//                                Logger.json(string);
//                            }
//
//                            sendSuccessResultCallback(o, resCallBack);
//                        }
//                    }
//
//                } catch (IOException e) {
//                    sendFailedStringCallback(response.request(), e, resCallBack);
//                } catch (com.google.gson.JsonParseException e) {
//                    if (BuildConfig.DEBUG) {
//                        Logger.e(e.toString());
//                        Logger.e(string);
//                        Observable.just(1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
//                            @Override
//                            public void call(Integer integer) {
//                                ToastHelper.showErrorToast("json解析出错，详情请查看logcat");
//                            }
//                        });
//
//                    }
//                    sendFailedStringCallback(response.request(), e, resCallBack);
//                }
//
//            }
        });
    }

    /**
     * 解析结果
     *
     * @param callback
     * @param request
     */
    public void deliveryResult(ResultCallback callback, Request request) {
        if (callback == null) callback = DEFAULT_RESULT_CALLBACK;
        final ResultCallback resCallBack = callback;
        //UI thread
        callback.onBefore();
        OkHttpClientManager.getInstance().getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedStringCallback(call.request(), e, resCallBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = "";
                try {
                    string = response.body().string();
                    JSONObject jsonObject = new JSONObject(string);

                    if (BuildConfig.DEBUG && resCallBack.showLog) {
                        Log.i(TAG, jsonObject.toString());
                    }
                    sendSuccessResultCallback(jsonObject, resCallBack);

                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, resCallBack);
                } catch (JSONException e) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "onResponse: ", e);
                        Log.e(TAG, string);
                        Observable.just(1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new DefaultObserver<Integer>() {
                            @Override
                            public void onNext(Integer integer) {
//                                Toast.makeText("json解析出错，详情请查看logcat");
                                Log.e(TAG, "json解析出错，详情请查看logcat");
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });

                    }
                    sendFailedStringCallback(response.request(), e, resCallBack);
                }

            }

        });
    }
}
