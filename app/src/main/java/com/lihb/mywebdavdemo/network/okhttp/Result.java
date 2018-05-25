package com.lihb.mywebdavdemo.network.okhttp;


/**
 * 返回对象基础实体类
 * Created by zhengwenjie on 2015/8/18.
 */
public class Result<T> {
    public String code = "";
    public String message = "";
    public String token = "";
    public T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code.equals("200");
    }

    public boolean isNoLogin() {
        return code.equals("201");
    }

    public boolean noData() {
        return "30001".equals(code) || "100006".equals(code);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
