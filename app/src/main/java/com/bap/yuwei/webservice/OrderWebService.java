package com.bap.yuwei.webservice;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Administrator on 2017/11/9.
 */

public interface OrderWebService {

    @POST("/v1/goods/buynow")
    Call<ResponseBody> getOrderInfo(@Body RequestBody body);

    @POST("/v1/user/{userId}/carts/tobill")
    Call<ResponseBody> getOrderInfoByCart(@Path("userId") String userId,@Body RequestBody body);

    @POST("/v1/user/{userId}/adduserinvoice")
    Call<ResponseBody> addUserInvoice(@Path("userId") String userId,@Body RequestBody body);

    @GET("/v1/user/{userId}/type/{type}/headertype/{headerType}")
    Call<ResponseBody> getInvoiceByType(@Path("userId") String userId,@Path("type") int type,@Path("headerType") int headerType);
}
