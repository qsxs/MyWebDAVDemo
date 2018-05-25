package com.lihb.mywebdavdemo.network;

/**
 * Created by lihuabin on 2017/12/11.
 */

public class ErrorInfo {
    /**
     * code : 105
     * error : invalid field name: bl!ng
     */

    private int code;
    private String error;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
