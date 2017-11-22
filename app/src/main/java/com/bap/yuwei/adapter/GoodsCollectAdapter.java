package com.bap.yuwei.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.goods.GoodsCollect;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.GoodsWebService;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/11/22.
 */

public class GoodsCollectAdapter extends ListBaseAdapter<GoodsCollect>{

    private String userId;
    private MediaType jsonMediaType= MediaType.parse("application/json; charset=utf-8");
    private GoodsWebService goodsWebService;
    private Gson mGson;

    public GoodsCollectAdapter(Context context, String userId) {
        super(context);
        this.goodsWebService= MyApplication.getInstance().getWebService(GoodsWebService.class);
        this.mGson=new Gson();
        this.userId=userId;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_goods_collect;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        ImageView imgGoods = holder.getView(R.id.img_goods);
        TextView txtTitle=holder.getView(R.id.txt_goods_title);
        TextView txtPrice=holder.getView(R.id.txt_price);
        ImageView imgAddCart=holder.getView(R.id.img_add_cart);

        final GoodsCollect goodsCollect=mDataList.get(position);
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+goodsCollect.getGoodsImage(),imgGoods);
        txtTitle.setText(goodsCollect.getTitle());
        txtPrice.setText("￥"+goodsCollect.getPreferentialPrice());
        imgAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCarts(goodsCollect);
            }
        });
    }

    /**
     * 添加商品到购物车
     */
    private void addCarts(GoodsCollect goodsCollect){
        Map<String,Object> params=new HashMap<>();
        params.put("goodsId", goodsCollect.getGoodsId());
        params.put("goodsCount",1);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=goodsWebService.addCarts(userId,body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        ToastUtil.showShort(mContext,"已添加到购物车！");
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
}
