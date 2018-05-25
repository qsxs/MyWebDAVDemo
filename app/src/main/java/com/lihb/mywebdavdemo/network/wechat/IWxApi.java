package com.lihb.mywebdavdemo.network.wechat;

public interface IWxApi {

    /**
     * 微信登录
     * @param appid
     * @param secret
     * @param code
     * @param grant_type 固定为 "authorization_code"
     * @return
     */
//    @POST("https://api.weixin.qq.com/sns/oauth2/access_token")
//    Observable<WxLogin> getWxToken(
//            @Query("appid") String appid,
//            @Query("secret") String secret,
//            @Query("code") String code,
//            @Query("grant_type") String grant_type
//    );
}
