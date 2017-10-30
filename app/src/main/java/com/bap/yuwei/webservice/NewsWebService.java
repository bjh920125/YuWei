package com.bap.yuwei.webservice;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Administrator on 2017/10/30.
 */

public interface NewsWebService {


    @GET("/v1/banner/2")
    Call<ResponseBody> getBanner();



}
