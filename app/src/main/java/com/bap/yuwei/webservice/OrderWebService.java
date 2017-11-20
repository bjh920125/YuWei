package com.bap.yuwei.webservice;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @POST("/v1/orders/topay")
    Call<ResponseBody> commitOrder(@Body RequestBody body);

    @POST("/v1/orders/app-alipay")
    Call<ResponseBody> pay(@Body RequestBody body);

    @POST("/v1/buyer/{userId}/orders")
    Call<ResponseBody> getOrderList(@Path("userId") String userId,@Body RequestBody body);

    @GET("/v1/orders/{orderId}")
    Call<ResponseBody> getOrderDetail(@Path("orderId") Long orderId);

    @GET("/v1/buyer/{userId}/orders/statistics")
    Call<ResponseBody> getOrderStatistics(@Path("userId") String userId);

    @POST("/v1/buyer/{userId}/orders/{orderId}/remind_shipping")
    Call<ResponseBody> remindSend(@Path("userId") String userId,@Path("orderId") Long orderId);

    @PUT("/v1/buyer/orders/cancel")
    Call<ResponseBody> cancelOrder(@Body RequestBody body);

    @POST("/v1/buyer/{userId}/orders/deleteorders")
    Call<ResponseBody> deleteOrder(@Path("userId") String userId,@Body RequestBody body);

    @PUT("/v1/buyer/orders/receive")
    Call<ResponseBody> receiveOrder(@Body RequestBody body);

    @GET("/v1/express/{orderId}")
    Call<ResponseBody> getExpress(@Path("orderId") Long orderId);

    @POST("v1/buyer/refund/refundlist")
    Call<ResponseBody> getRefundList(@Body RequestBody body);

    @POST("/v1/buyer/{userId}/orders/refund")
    Call<ResponseBody> addRefund(@Path("userId") String userId,@Body RequestBody body);

    @GET("/v1/buyer/refund/orderitemid/{orderItemId}")
    Call<ResponseBody> getRefundDetail(@Path("orderItemId") Long orderItemId);

    @GET("/v1/buyer/refund/{refundId}")
    Call<ResponseBody> getRefundDetailByRefundId(@Path("refundId") Long refundId);

    @GET("/v1/buyer/refund/{refundId}/platform")
    Call<ResponseBody> applyPlatformDeal(@Path("refundId") Long refundId);

    @GET("v1/buyer/refund/{refundId}/withdraw")
    Call<ResponseBody> cancelRefund(@Path("refundId") Long refundId);

    @POST("/v1/buyer/{userId}/refund/fillexpress")
    Call<ResponseBody> fillExpressInfo(@Path("userId") String userId,@Body RequestBody body);

    @POST("/v1/buyer/evaluations")
    Call<ResponseBody> comment(@Path("refundId") Long refundId);

}
