package com.bap.yuwei.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bap.yuwei.R;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.MyGoodsCart;
import com.bap.yuwei.fragment.base.BaseFragment;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.GoodsWebService;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/10/27.
 */

public class CartFragment extends BaseFragment implements View.OnClickListener{

    private GoodsWebService goodsWebService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsWebService= MyApplication.getInstance().getWebService(GoodsWebService.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getCarts();
    }


    private void getCarts(){
        Call<ResponseBody> call=goodsWebService.getCarts(mUser.getUserId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        JSONArray ja=jo.getJSONArray("shopItems");
                        List<MyGoodsCart> templist=mGson.fromJson(jo.toString(), new TypeToken<List<MyGoodsCart>>() {}.getType());
                    }else{
                        ToastUtil.showShort(mContext,appResponse.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_cart, container, false);
        return fragmentView;
    }


    @Override
    public void initView() {

    }
}
