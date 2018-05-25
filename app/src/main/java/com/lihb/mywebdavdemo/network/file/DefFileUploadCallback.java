package com.lihb.mywebdavdemo.network.file;

import android.content.Context;

import com.lihb.mywebdavdemo.network.BaseResponse;


public abstract class DefFileUploadCallback implements OnFileUploadCallback {
    private Context context;

    public DefFileUploadCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onBefore() {
//        ProgressDialogUtil.show(context, "上传中");
    }


    @Override
    public void onFail(BaseResponse<FileUpload> baseResponse) {
//        ToastUtil.show(baseResponse.getErrorMessage());
    }

    @Override
    public void onException(Throwable e) {
//        ToastUtil.show("上传失败，上传总大小太大");
    }

    @Override
    public void onEnd() {
//        ProgressDialogUtil.dismiss();
    }
}
