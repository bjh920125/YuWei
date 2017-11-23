package com.bap.yuwei.webservice;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/10/30.
 */

public interface SysWebService {


    @POST("v1/user/register")
    Call<ResponseBody> register(@Body RequestBody body);

    @POST("v1/user/login")
    Call<ResponseBody> login(@Body RequestBody body);

    @GET("v1/user/logout")
    Call<ResponseBody> loginout();

    @POST("v1/user/app/qrcodeLogin")
    @FormUrlEncoded
    Call<ResponseBody> login(@Field("userId") String userId,@Field("token") String token);

    @GET("v1/user/{userId}/profile")
    Call<ResponseBody> getUserInfo(@Path("userId") String userId);

    @PUT("v1/user/{userId}/profile")
    Call<ResponseBody> updateUserInfo(@Path("userId") String userId,@Body RequestBody body);

    @POST("v1/user/check_pwd")
    Call<ResponseBody> checkPassword(@Body RequestBody body);

    @PUT("v1/user/pass")
    Call<ResponseBody> updatePassword(@Body RequestBody body);

    @PUT("v1/user/phone")
    Call<ResponseBody> updatePhone(@Body RequestBody body);

    @POST("v1/user/password_reset")
    Call<ResponseBody> forgetPassword(@Body RequestBody body);

    @POST("http://ip.taobao.com/service/getIpInfo2.php")
    @FormUrlEncoded
    Call<ResponseBody> getIp(@Field("ip") String params);

    @Multipart
    @POST("v1/upload")
    Call<ResponseBody> uploadFile(@Part MultipartBody.Part file);

    @GET("v1/user/{userId}/addresses")
    Call<ResponseBody> getReceiveAddress(@Path("userId") String userId);

    @POST("v1/user/{userId}/addresses")
    Call<ResponseBody> addReceiveAddress(@Path("userId") String userId,@Body RequestBody body);

    @PUT("v1/user/{userId}/addresses/{addressId}/setdefaultaddress")
    Call<ResponseBody> setDefaultAddress(@Path("userId") String userId,@Path("addressId") Long addressId);

    @PUT("v1/user/{userId}/addresses/{addressId}")
    Call<ResponseBody> updateReceiveAddress(@Path("userId") String userId,@Path("addressId") Long addressId,@Body RequestBody body);

    @DELETE("v1/user/{userId}/addresses/{addressId}")
    Call<ResponseBody> deleteReceiveAddress(@Path("userId") String userId,@Path("addressId") Long addressId);

    @GET("v1/vat/{userId}")
    Call<ResponseBody> getVat(@Path("userId") String userId);

    @POST("v1/vat")
    Call<ResponseBody> updateVat(@Body RequestBody body);

    @POST("v1/user/smscode")
    @FormUrlEncoded
    Call<ResponseBody> getSmsCode(@Field("phone") String phone);

    @POST("v1/user/sms/valid")
    Call<ResponseBody> validSmsCode(@Body RequestBody body);

    @GET("v1/{userId}/app/messages")
    Call<ResponseBody> getMsgs(@Path("userId") String userId);

    @POST("v1/{userId}/messages")
    Call<ResponseBody> getMsgsByType(@Path("userId") String userId,@Body RequestBody body);

    @GET("v1/{userId}/messages")
    Call<ResponseBody> getOrderMsgs(@Path("userId") String userId,@Query("shopId") Long shopId,@Query("userType") int userType);

    @GET("v1/{userId}/messages/unread-count")
    Call<ResponseBody> getUnreadMsgsCount(@Path("userId") String userId, @Query("userType") int userType);

}
