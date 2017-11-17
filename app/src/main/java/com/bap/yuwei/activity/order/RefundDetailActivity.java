package com.bap.yuwei.activity.order;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.OrderItem;
import com.bap.yuwei.entity.order.Refund;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.OrderWebService;
import com.githang.statusbar.StatusBarCompat;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RefundDetailActivity extends BaseActivity {

    private TextView txtStatus,txtTotalPrice,txtGoodsTitle,txtModel,txtPrice,txtNum;
    private TextView txtReason,txtRefundMoney,txtApplyTime,txtRefundNo;
    private ImageView imgGoods;
    private TextView txtPlatform;

    private OrderItem orderItem;
    private Refund refund;

    private OrderWebService orderWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        orderItem= (OrderItem) getIntent().getSerializableExtra(OrderItem.KEY);
        if(null==orderItem){
            refund= (Refund) getIntent().getSerializableExtra(Refund.KEY);
        }
        getRefundDetail();
    }

    private void getRefundDetail(){
        showLoadingDialog();
        Call<ResponseBody> call;
        if(null !=orderItem){
            call = orderWebService.getRefundDetail(orderItem.getOrderItemId());
        }else {
            call = orderWebService.getRefundDetailByRefundId(refund.getRefundId());
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        refund=mGson.fromJson(jo.toString(),Refund.class);
                        initUIWithValues();
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

    private void initUIWithValues(){
        txtTotalPrice.setText("￥"+refund.getRefundMoney());
        txtGoodsTitle.setText(refund.getTitle()+"");
        txtModel.setText("类别"+refund.getModel());
        txtPrice.setText("￥"+refund.getPayMoney());
        txtNum.setText("x"+refund.getQuantity());
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+refund.getGoodsImage(),imgGoods);
        txtReason.setText("退款原因："+refund.getRefundReason());
        txtRefundMoney.setText("退款金额：￥"+refund.getRefundMoney());
        txtApplyTime.setText("申请时间："+refund.getCreateTime());
        txtRefundNo.setText("退款编号："+refund.getRefundId());
        txtStatus.setText(refund.getStatusText());

        if(refund.getStatus()==Constants.ORDER_ITEM_STATUS_REFUND_SUCCESS){
            txtPlatform.setEnabled(false);
            txtPlatform.setTextColor(getResources().getColor(R.color.darkgrey));
        }
    }

    public void showHistory(View v){
        Intent i=new Intent(mContext,RefundConsultHistoryActivity.class);
        i.putExtra(Refund.KEY,refund);
        startActivity(i);
    }

    public void applyPlatformDeal(View v){
        showLoadingDialog();
        Call<ResponseBody> call=orderWebService.applyPlatformDeal(refund.getRefundId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        ToastUtil.showShort(mContext,"平台已介入处理！");
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

    public void onBackClick(View v){
        finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refund_detail;
    }

    @Override
    protected void initView() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE,true);
        txtStatus= (TextView) findViewById(R.id.txt_status);
        txtTotalPrice= (TextView) findViewById(R.id.txt_actual_total_amount);
        txtGoodsTitle= (TextView) findViewById(R.id.txt_goods_name);
        txtModel= (TextView) findViewById(R.id.txt_model);
        txtPrice= (TextView) findViewById(R.id.txt_price);
        txtNum= (TextView) findViewById(R.id.txt_num);
        txtReason= (TextView) findViewById(R.id.txt_refund_reason);
        txtRefundMoney= (TextView) findViewById(R.id.txt_refund_money);
        txtApplyTime= (TextView) findViewById(R.id.txt_apply_time);
        txtRefundNo= (TextView) findViewById(R.id.txt_refund_no);
        imgGoods= (ImageView) findViewById(R.id.img_goods);
        txtPlatform= (TextView) findViewById(R.id.txt_platform);
    }
}
