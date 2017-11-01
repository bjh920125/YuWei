package com.bap.yuwei.webservice;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by Administrator on 2017/10/30.
 */

public interface GoodsWebService {


    @GET("/v1/categories/top")
    Call<ResponseBody> getTopCategories();

    @POST("/v1/hotrecommend")
    Call<ResponseBody> getHotRecommend(@Body RequestBody body);

    @GET("categorysearch")
    Call<ResponseBody> categorysearch(@QueryMap Map<String,Object> params);

    @GET("/v1/goods/{goodsId}/deviceType/1")
    Call<ResponseBody> getGoodsDetail(@Path("goodsId") Long goodsId);

    @GET("/v1/shop/{shopId}/detail")
    Call<ResponseBody> getShopDetail(@Path("shopId") Long shopId);

    @GET("/search/hotkeywords")
    Call<ResponseBody> getHotWords(@QueryMap Map<String,Object> params);

    @GET("/search/keywords")
    Call<ResponseBody> getDefaultHotWords();

    @GET("/search/{userId}/records")
    Call<ResponseBody> getSearchHistory(@Path("userId") String userId,@QueryMap Map<String,Object> params);

    @DELETE("/search/{userId}/records")
    Call<ResponseBody> deleteSearchHistory(@Path("userId") String userId);

    @GET("/v1/collectnum/user/{userId}")
    Call<ResponseBody> getCollectNum(@Path("userId") String userId);


}
