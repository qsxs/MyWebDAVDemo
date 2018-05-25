package com.lihb.mywebdavdemo.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * 默认的请求回调
 * Created by lihuabin on 2017/12/4.
 */

public abstract class BaseObserver<T> implements Observer<T> {
    private Context context;
    //  Activity 是否在执行onStop()时取消订阅
//    private boolean isAddInStop = false;
//    private CommonDialogUtils dialogUtils;
    private ProgressDialog dialog;
    private static final String TAG = "BaseObserver";

    public BaseObserver(Context context) {
        this(context, true, null);
    }

    public BaseObserver(Context context, boolean isShowLoading) {
        this(context, isShowLoading, "加载中");
    }

    public BaseObserver(Context context, boolean isShowLoading, String loadingMsg) {
        this.context = context;
//        dialogUtils = new CommonDialogUtils();
        if (isShowLoading) {
            showProgress(context, loadingMsg);
//            dialogUtils.showProgress(activity, "Loading...");
        }
    }

    private void showProgress(Context activity, String loadingMsg) {
//        CustomProgress.show(activity, loadingMsg, false, null);
//        ProgressDialogUtil.show(activity, loadingMsg);
    }

    private void dismissProgress() {
//        CustomProgress.hideProgress();
//        ProgressDialogUtil.dismiss();
    }


    @Override
    public final void onNext(T response) {
        if (isSuccess(response)) {
            onSuccess(response);
        } else {
            onFail(response);
        }
        dismissProgress();
    }

    protected abstract boolean isSuccess(T response);

    @Override
    public void onSubscribe(Disposable d) {

    }

    public void onEnd(){

    }

    @Override
    public final void onComplete() {
        onEnd();
    }

    abstract public void onSuccess(T response);

    public void onFail(T response) {
        String message = getErrorMessage(response);
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    protected abstract String getErrorMessage(T response);

    @Override
    public final void onError(Throwable e) {
        if (e instanceof HttpException) {     //   HTTP错误
//            HttpException he = ((HttpException) e);
//            if (he.response().errorBody() != null) {
//                try {
//                    ErrorInfo userInfo = new Gson().fromJson(he.response().errorBody().string(), ErrorInfo.class);
//                    dismissProgress();
//                    onFail(userInfo);
//                } catch (JsonSyntaxException je) {
//                    onException(ExceptionReason.PARSE_ERROR, e);
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                    onException(ExceptionReason.BAD_NETWORK, e);
//                }
//            } else
            onException(ExceptionReason.BAD_NETWORK, e);
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {   //   连接错误
            onException(ExceptionReason.CONNECT_ERROR, e);
        } else if (e instanceof InterruptedIOException) {   //  连接超时
            onException(ExceptionReason.CONNECT_TIMEOUT, e);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {   //  解析错误
            onException(ExceptionReason.PARSE_ERROR, e);
        } else {
            onException(ExceptionReason.UNKNOWN_ERROR, e);
        }
    }

    public void onException(ExceptionReason reason, Throwable e) {
        dismissProgress();
        CharSequence message;
        switch (reason) {
            case CONNECT_ERROR:
                message = "网络连接失败,请检查网络";
                break;

            case CONNECT_TIMEOUT:
                message = "连接超时,请稍后再试";
                break;

            case BAD_NETWORK:
                message = "服务器异常";
                break;

            case PARSE_ERROR:
                message = "解析服务器响应数据失败";
                break;

            case UNKNOWN_ERROR:
            default:
                message = "未知错误";
                break;
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        onEnd();
    }

    /**
     * 请求网络失败原因
     */
    public enum ExceptionReason {
        /**
         * 解析数据失败
         */
        PARSE_ERROR,
        /**
         * 网络问题
         */
        BAD_NETWORK,
        /**
         * 连接错误
         */
        CONNECT_ERROR,
        /**
         * 连接超时
         */
        CONNECT_TIMEOUT,
        /**
         * 未知错误
         */
        UNKNOWN_ERROR,
    }
}