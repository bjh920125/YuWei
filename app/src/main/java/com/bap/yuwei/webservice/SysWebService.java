package com.bap.yuwei.webservice;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2017/10/30.
 */

public interface SysWebService {


    @POST("/v1/user/login")
    Call<ResponseBody> login(@Body RequestBody body);

    @POST("/v1/user/app/qrcodeLogin")
    Call<ResponseBody> login(@Field("userId") String userId,@Field("token") String token);

    @POST("http://ip.taobao.com/service/getIpInfo2.php")
    @FormUrlEncoded
    Call<ResponseBody> getIp(@Field("ip") String params);
    
    
}
