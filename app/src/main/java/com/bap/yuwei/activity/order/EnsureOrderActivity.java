package com.bap.yuwei.activity.order;

import android.os.Bundle;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.GoodsCart;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.OrderWebService;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnsureOrderActivity extends BaseActivity {


    private GoodsCart mGoodsCart;
    private OrderWebService orderWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        mGoodsCart= (GoodsCart) getIntent().getSerializableExtra(GoodsCart.KEY);
        getOrderInfo();
    }


    private void getOrderInfo(){
        showLoadingDialog();
        Map<String,Object> params=new HashMap<>();
        params.put("goodsCount", mGoodsCart.getGoodsCount());
        params.put("goodsId", mGoodsCart.getGoodsId());
        params.put("userId", mUser.getUserId());
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=orderWebService.getOrderInfo(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){

                    }else{
                        ToastUtil.showShort(mContext,appResponse.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dismissProgressDialog();
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ensure_order;
    }

    @Override
    protected void initView() {

    }
}
