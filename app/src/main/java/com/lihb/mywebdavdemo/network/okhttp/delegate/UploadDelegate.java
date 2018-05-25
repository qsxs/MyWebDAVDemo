package com.lihb.mywebdavdemo.network.okhttp.delegate;

import com.lihb.mywebdavdemo.network.okhttp.OkParams;
import com.lihb.mywebdavdemo.network.okhttp.ResultCallback;

import java.io.File;
import java.lang.reflect.Type;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UploadDelegate extends BaseGsonDelegate{

    /**
     * 异步基于post的文件上传:主方法
     */
    public void postAsyn(String url, OkParams params, ResultCallback callback, Type type) {
        Request request = buildMultipartFormRequest(url, params);
        deliveryResult(callback, request, type);
    }

    private Request buildMultipartFormRequest(String url, OkParams params) {
        if (params == null) {
            params = new OkParams();
        }
        IdentityHashMap<String, String> urlParams = params.getUrlParams();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        Set<IdentityHashMap.Entry<String, String>> entries = urlParams.entrySet();
        for (IdentityHashMap.Entry<String, String> entry : entries) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                    RequestBody.create(null, entry.getValue()));
        }
        LinkedHashMap<String, File> fileParams = params.getFileParams();
        if (fileParams != null && fileParams.size() > 0) {
            RequestBody fileBody = null;
            Set<LinkedHashMap.Entry<String, File>> fileEntries = fileParams.entrySet();
            for (LinkedHashMap.Entry<String, File> entry : fileEntries) {
                File file = entry.getValue();
                String fileKey = entry.getKey();
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                //TODO 根据文件名设置contentType
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + fileKey + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        }

        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .tag(url)
                .build();
    }
    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }
}
