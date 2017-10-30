package com.bap.yuwei.webservice;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2017/10/30.
 */

public interface GoodsWebService {


    @GET("/v1/categories/top")
    Call<ResponseBody> getTopCategories();


    @POST("/v1/hotrecommend")
    Call<ResponseBody> getHotRecommend(@Body RequestBody body);
}
