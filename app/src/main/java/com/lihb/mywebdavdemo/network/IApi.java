package com.lihb.mywebdavdemo.network;

import com.lihb.mywebdavdemo.DavResumeModel;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.HTTP;
import retrofit2.http.Url;

/**
 * 所有接口集中管理
 * Created by lihuabin on 2017/12/4.
 */

public interface IApi {

    String BAIDU_VOICE_BASE_URL = "http://vop.baidu.com/server_api";
    /**
     * 网络请求超时时间毫秒
     */
    int DEFAULT_TIMEOUT = 20000;
    String BASE_IMG_URL = "http://47.52.167.12:25000";
    String BASE_URL = "https://dav.jianguoyun.com/";

    @HTTP(method = "PROPFIND")
    Observable<DavResumeModel> propfind(
            @Url String url
    );

    @HTTP(method = "MKCOL")
    Observable<ResponseBody> mkol(
            @Url String url
    );


}
