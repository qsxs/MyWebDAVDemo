package com.lihb.mywebdavdemo.network;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lihuabin on 2017/12/4.
 */

public class BaseResponse<T> implements IResponse {

    /**
     * is_success : false
     * error_message : {"token":["token can not be empty!"]}
     */

    private boolean is_success;
    private HashMap<String, List<String>> error_message;
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isIs_success() {
        return is_success;
    }

    public void setIs_success(boolean is_success) {
        this.is_success = is_success;
    }

    public HashMap<String, List<String>> getError_message() {
        return error_message;
    }

    public void setError_message(HashMap<String, List<String>> error_message) {
        this.error_message = error_message;
    }

    @Override
    public boolean isSuccess() {
        return is_success;
    }

    @Override
    public String getErrorMessage() {
        StringBuilder sb = new StringBuilder();
        if (error_message != null) {
            for (String key : error_message.keySet()) {
                sb.append("\n");
                sb.append(key);
                sb.append(":");
                List<String> list = error_message.get(key);
                for (String s : list) {
                    sb.append(s);
                }
            }
            sb.replace(0, 1, "");
        }
        return sb.toString();
    }

}
