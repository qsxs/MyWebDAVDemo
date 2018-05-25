package com.lihb.mywebdavdemo.network.file;


import com.lihb.mywebdavdemo.network.BaseResponse;

public interface OnFileUploadCallback {
    void onBefore();

    void onSuccess(FileUpload fileUpload);

    void onFail(BaseResponse<FileUpload> baseResponse);

    void onException(Throwable e);

    void onEnd();
}
