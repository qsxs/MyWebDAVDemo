package com.lihb.mywebdavdemo.network.file;

import android.content.Context;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadFileHelper {

    public static void uploadFiles(List<File> files, final OnFileUploadCallback callback) {
        if (callback != null) {
            callback.onBefore();
        }
//        NetworkManager
//                .getApiService()
//                .uploadFile(filesToMultipartBody(files))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DefaultObserver<BaseResponse<FileUpload>>() {
//                    @Override
//                    public void onNext(BaseResponse<FileUpload> responseBody) {
//                        if (callback != null) {
//                            if (responseBody.isSuccess()) {
//                                callback.onSuccess(responseBody.getData());
//                            } else {
//                                callback.onFail(responseBody);
//                            }
//                            callback.onEnd();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        LogUtil.d("onError");
//                        if (callback != null) {
//                            callback.onException(e);
//                            callback.onEnd();
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        LogUtil.d("onComplete");
//                    }
//                });

    }

    public static void uploadFilesFromPath(List<String> files, final OnFileUploadCallback callback) {
        if (callback != null) {
            callback.onBefore();
        }
//        NetworkManager
//                .getApiService()
//                .uploadFile(pathsToMultipartBody(files))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DefaultObserver<BaseResponse<FileUpload>>() {
//                    @Override
//                    public void onNext(BaseResponse<FileUpload> responseBody) {
//                        if (callback != null) {
//                            if (responseBody.isSuccess()) {
//                                callback.onSuccess(responseBody.getData());
//                            } else {
//                                callback.onFail(responseBody);
//                            }
//                            callback.onEnd();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        LogUtil.d("onError");
//                        if (callback != null) {
//                            callback.onException(e);
//                            callback.onEnd();
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        LogUtil.d("onComplete");
//                    }
//                });
    }

    private static MultipartBody filesToMultipartBody(List<File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();

        for (File file : files) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            builder.addFormDataPart("file[]", file.getName(), requestBody);
        }
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

    private static MultipartBody pathsToMultipartBody(List<String> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();

        for (String filePath : files) {
            File file = new File(filePath);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            builder.addFormDataPart("file[]", file.getName(), requestBody);
        }
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

    public static void uploadFilesBeforeLuban(final Context context, List<String> files, final OnFileUploadCallback callback) {
//        Observable.just(files)
//                .observeOn(Schedulers.io())
//                .map(new Function<List<String>, List<File>>() {
//                    @Override
//                    public List<File> apply(List<String> list) throws Exception {
//                        // 同步方法直接返回压缩后的文件
//                        return Luban.with(context)
//                                .ignoreBy(100)
//                                .load(list)
//                                .get();
//                    }
//                })
//                .concatMap(new Function<List<File>, Observable<BaseResponse<FileUpload>>>() {
//                    @Override
//                    public Observable<BaseResponse<FileUpload>> apply(List<File> files) {
//                        return NetworkManager.getApiService().uploadFile(filesToMultipartBody(files));
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DefaultObserver<BaseResponse<FileUpload>>() {
//                    @Override
//                    public void onNext(BaseResponse<FileUpload> responseBody) {
//                        if (callback != null) {
//                            if (responseBody.isSuccess()) {
//                                callback.onSuccess(responseBody.getData());
//                            } else {
//                                callback.onFail(responseBody);
//                            }
//                            callback.onEnd();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        LogUtil.d("onError");
//                        if (callback != null) {
//                            callback.onException(e);
//                            callback.onEnd();
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        LogUtil.d("onComplete");
//                    }
//                });

    }

    private static String getPath() {
        return null;
    }
}
