package com.bap.yuwei.webservice;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Administrator on 2017/10/30.
 */

public interface NewsWebService {


    @GET("v1/banner/2")
    Call<ResponseBody> getBanner();

    @POST("v1/news/all")
    Call<ResponseBody> getNews(@Body RequestBody body);

    @GET("v1/news/{newsId}")
    Call<ResponseBody> getNewsDetail(@Path("newsId") Long newsId);

}
