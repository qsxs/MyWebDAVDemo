package com.lihb.mywebdavdemo.network.okhttp;

import android.util.Log;

import com.lihb.mywebdavdemo.utils.Check;

import java.io.File;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;

/**
 * 新的请求体
 * Created by zhengwenjie on 2015/8/27.
 */
public class OkParams {
    private static final String TAG = "OkParams";
    protected IdentityHashMap<String, String> urlParams;
    protected LinkedHashMap<String, File> fileParams;//需要排序

    private String specifiedVersion;//某些请求需要特定的版本号
    private String txcode;

    public OkParams() {
        init();
    }

    public OkParams(String key, String value) {
        init();
        put(key, value);
    }

    private void init() {
        urlParams = new IdentityHashMap<String, String>();
        fileParams = new LinkedHashMap<String, File>();
    }

    public IdentityHashMap<String, String> getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(IdentityHashMap<String, String> urlParams) {
        this.urlParams = urlParams;
    }

    public LinkedHashMap<String, File> getFileParams() {
        return fileParams;
    }

    public void setFileParams(LinkedHashMap<String, File> fileParams) {
        this.fileParams = fileParams;
    }

    /**
     * 添加value到request中
     *
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        if (!Check.isEmpty(key) && !Check.isEmpty(value)) {
//            urlParams.put(key, URLEncoder.encode(value));
            urlParams.put(key, value);
        }
    }

    /**
     * 添加value到request中
     *
     * @param key
     */
    public void remove(String key) {
        if (key != null) {
            urlParams.remove(key);
        }
    }

    /**
     * 添加文件到request中
     *
     * @param key
     * @param file
     */
    public void put(String key, File file) {
        if (key != null && file != null && file.isFile() && file.exists()) {
            fileParams.put(key, file);
        } else {
//            ToastHelper.showErrorToast("文件不存在");
            Log.i(TAG, "put: 文件不存在");
        }
    }

    public String getSpecifiedVersion() {
        return specifiedVersion;
    }

    public void setSpecifiedVersion(String specifiedVersion) {
        this.specifiedVersion = specifiedVersion;
    }

    public String getTxcode() {
        return txcode;
    }

    public void setTxcode(String txcode) {
        this.txcode = txcode;
    }

    @Override
    public String toString() {
        StringBuilder paramsStr = new StringBuilder();
        if (urlParams != null) {
            for (String key : urlParams.keySet()) {
                paramsStr.append(key).append("=").append(urlParams.get(key)).append("&");
            }
            paramsStr.delete(paramsStr.length() - 1, paramsStr.length());
        }
        return paramsStr.toString();
    }

}
