package com.bap.yuwei.webservice;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2017/11/9.
 */

public interface OrderWebService {

    @POST("/v1/goods/buynow")
    Call<ResponseBody> getOrderInfo(@Body RequestBody body);

}
