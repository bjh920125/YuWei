package com.bap.yuwei.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.order.RefundFillExpressActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.event.CancelRefundEvent;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.Refund;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.OrderWebService;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bap.yuwei.entity.Constants.ORDER_ITEM_STATUS_PRE_BUYER_SEND;

/**
 * Created by Administrator on 2017/11/17.
 */

public class RefundAdapter extends ListBaseAdapter<Refund> {

    private List<Refund> refunds;

    private OrderWebService orderWebService;
    private Gson mGson;


    public RefundAdapter(Context context,List<Refund> refunds) {
        super(context);
        this.refunds=refunds;
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        mGson=new Gson();
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_refund;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        TextView txtShopName=holder.getView(R.id.txt_shop_name);
        ImageView imgGoods=holder.getView(R.id.img_goods);
        TextView txtGoodsTitle=holder.getView(R.id.txt_goods_name);
        TextView txtModel=holder.getView(R.id.txt_model);
        TextView txtNum=holder.getView(R.id.txt_num);
        TextView txtRefundMoney=holder.getView(R.id.txt_refund_money);
        TextView txtType=holder.getView(R.id.txt_type);
        TextView txtStatus=holder.getView(R.id.txt_refund_status);
        TextView txtCancel=holder.getView(R.id.txt_cancel);
        TextView txtExpress=holder.getView(R.id.txt_express);
        LinearLayout llOperate=holder.getView(R.id.ll_refund_item_operate);


        final Refund refund=refunds.get(position);
        txtShopName.setText(refund.getShopName());
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+refund.getGoodsImage(),imgGoods);
        txtGoodsTitle.setText(refund.getTitle());
        txtModel.setText("类别："+refund.getModel());
        txtNum.setText("x"+refund.getQuantity());
        txtRefundMoney.setText("退款金额：￥"+refund.getRefundMoney());
        txtType.setText(refund.getRefundType()==Constants.REFUND_MONEY ? "仅退款":"退货退款");
        txtStatus.setText(refund.getStatusText());
        if(refund.getStatus()==Constants.ORDER_ITEM_STATUS_REFUND_SUCCESS){
            llOperate.setVisibility(View.GONE);
        }else {
            llOperate.setVisibility(View.VISIBLE);
            if(refund.getStatus()==ORDER_ITEM_STATUS_PRE_BUYER_SEND){
                txtExpress.setVisibility(View.VISIBLE);
            }else {
                txtExpress.setVisibility(View.GONE);
            }
        }

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRefund(refund.getRefundId());
            }
        });

        txtExpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(mContext, RefundFillExpressActivity.class);
                i.putExtra(Refund.KEY,refund);
                mContext.startActivity(i);
            }
        });

    }

    private void cancelRefund(Long refundId){
        Call<ResponseBody> call = orderWebService.cancelRefund(refundId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        ToastUtil.showShort(mContext,"退款申请已取消！");
                        EventBus.getDefault().post(new CancelRefundEvent());
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
