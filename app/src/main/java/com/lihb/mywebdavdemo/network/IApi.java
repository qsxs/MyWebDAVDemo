package com.lihb.mywebdavdemo.network;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;

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
    String BASE_URL = "http://47.52.167.12:8899/";

    /**
     * 首页广告
     *
     * @return
     */
    @GET("https://dav.jianguoyun.com/dav/")
    Observable<Response> propfind();


}
