package com.lihb.mywebdavdemo.network.file.upload;


import com.lihb.mywebdavdemo.network.BaseResponse;

public interface OnFileUploadCallback {
    void onBefore();

    void onSuccess(FileUpload fileUpload);

    void onFail(BaseResponse<FileUpload> baseResponse);

    void onException(Throwable e);

    void onEnd();
}
