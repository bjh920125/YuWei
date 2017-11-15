package com.bap.yuwei.activity.order;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.OrderItemDetailAdapter;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.Express;
import com.bap.yuwei.entity.order.ExpressItem;
import com.bap.yuwei.entity.order.OrderDetail;
import com.bap.yuwei.entity.order.Orders;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.OrderWebService;
import com.githang.statusbar.StatusBarCompat;
import com.linearlistview.LinearListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends BaseActivity {

    private TextView txtOrderStauts,txtReceiverName,txtReceiverAddress,txtReceiverTel,txtShopName;
    private TextView txtExpress,txtExpressTime;
    private RelativeLayout rlExpress;
    private ImageView imgShop;
    private TextView txtGoodsTotalPrice,txtExpressPrice,txtOrderTotalPrice,txtActualPrice;
    private TextView txtOrderId,txtAlitradeNo,txtCreateTime,txtPayTime;
    private LinearListView lvOrderItems;
    private OrderItemDetailAdapter mAdapter;

    private Long orderId;
    public static final String ORDER_ID_KEY="order.id.key";

    private OrderDetail orderDetail;
    private OrderWebService orderWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        orderId=getIntent().getLongExtra(ORDER_ID_KEY,-1);
        getOrderDetail();
    }

    private void getOrderDetail(){
        showLoadingDialog();
        Call<ResponseBody> call=orderWebService.getOrderDetail(orderId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        orderDetail=mGson.fromJson(jo.toString(),OrderDetail.class);
                        initUIWithValues();
                        getExpress();
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

    private void getExpress(){
        showLoadingDialog();
        Call<ResponseBody> call=orderWebService.getExpress(orderId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    Express express=mGson.fromJson(result,Express.class);
                    if(null!=express && null!=express.getData() && express.getData().size()>0){
                        ExpressItem expressItem=express.getData().get(0);
                        rlExpress.setVisibility(View.VISIBLE);
                        txtExpress.setText(expressItem.getContext());
                        txtExpressTime.setText(expressItem.getFtime());
                    }
                    initUIWithValues();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dismissProgressDialog();
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }

    private void initUIWithValues(){
        if(null == orderDetail) return;
        Orders orders=orderDetail.getOrders();
        txtOrderStauts.setText(orders.getStatusText());
        txtShopName.setText(orders.getShopName());
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+orders.getShopIcon(),imgShop);
        txtReceiverName.setText(orders.getBuyerName());
        txtReceiverTel.setText(orders.getBuyerPhone());
        txtReceiverAddress.setText(orders.getBuyerAddress());
        txtGoodsTotalPrice.setText("￥"+orders.getPayAmount());
        txtExpressPrice.setText("￥"+orders.getFreight());
        txtOrderTotalPrice.setText("￥"+orders.getPayAmount());
        txtActualPrice.setText("￥"+orders.getRealPayAmount());
        txtOrderId.setText("订单编号："+orders.getOrderId());
        txtAlitradeNo.setText("支付宝交易号："+orders.getAlipayTradeNo());
        txtCreateTime.setText("创建时间："+orders.getCreateTime());
        txtPayTime.setText("付款时间："+orders.getPayTime());
        if (TextUtils.isEmpty(orders.getAlipayTradeNo())){
            txtAlitradeNo.setVisibility(View.GONE);
            txtPayTime.setVisibility(View.GONE);
        }
        mAdapter=new OrderItemDetailAdapter(orderDetail.getOrders().getOrderItems(),mContext);
        lvOrderItems.setAdapter(mAdapter);
    }

    public void showExpressDetail(View view){
        Intent i=new Intent(mContext,ExpressDetailActivity.class);
        i.putExtra(Orders.KEY,orderDetail.getOrders());
        startActivity(i);
    }

    public void onBackClick(View v){
        finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected void initView() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE,true);
        txtOrderStauts= (TextView) findViewById(R.id.txt_status);
        txtReceiverName= (TextView) findViewById(R.id.txt_receiver);
        txtReceiverAddress= (TextView) findViewById(R.id.txt_address);
        txtReceiverTel= (TextView) findViewById(R.id.txt_tel);
        txtShopName= (TextView) findViewById(R.id.txt_shop_name);
        imgShop= (ImageView) findViewById(R.id.img_shop);
        txtGoodsTotalPrice= (TextView) findViewById(R.id.txt_goods_amount);
        txtExpressPrice= (TextView) findViewById(R.id.txt_express_amount);
        txtOrderTotalPrice= (TextView) findViewById(R.id.txt_total_amount);
        txtActualPrice= (TextView) findViewById(R.id.txt_actual_total_amount);
        txtOrderId= (TextView) findViewById(R.id.txt_order_id);
        txtAlitradeNo= (TextView) findViewById(R.id.txt_alitrade_no);
        txtCreateTime= (TextView) findViewById(R.id.txt_create_time);
        txtPayTime= (TextView) findViewById(R.id.txt_pay_time);
        lvOrderItems= (LinearListView) findViewById(R.id.lv_order);
        txtExpress= (TextView) findViewById(R.id.txt_express);
        txtExpressTime= (TextView) findViewById(R.id.txt_time);
        rlExpress= (RelativeLayout) findViewById(R.id.rl_express);
    }
}
