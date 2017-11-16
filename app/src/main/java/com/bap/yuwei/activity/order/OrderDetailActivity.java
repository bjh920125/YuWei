package com.bap.yuwei.activity.order;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.OrderItemDetailAdapter;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.event.CancelOrderEvent;
import com.bap.yuwei.entity.event.DeleteOrderEvent;
import com.bap.yuwei.entity.event.ReceiveOrderEvent;
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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bap.yuwei.entity.Constants.ORDER_STATUS_CLOSED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_HAS_CANCELED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_HAS_COMPLETED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_HAS_SENDED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_PENDING_PAY;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_PRE_DELIVERED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_PRE_EVALUATED;

public class OrderDetailActivity extends BaseActivity {

    private TextView txtOrderStauts,txtReceiverName,txtReceiverAddress,txtReceiverTel,txtShopName;
    private TextView txtExpress,txtExpressTime;
    private RelativeLayout rlExpress;
    private ImageView imgShop;
    private TextView txtGoodsTotalPrice,txtExpressPrice,txtOrderTotalPrice,txtActualPrice;
    private TextView txtOrderId,txtAlitradeNo,txtCreateTime,txtPayTime;
    private TextView txtPay,txtAlertSend,txtShowExpress,txtReceive,txtComment,txtCancel,txtDelete;

    private LinearListView lvOrderItems;
    private OrderItemDetailAdapter mAdapter;

    private Long orderId;
    public static final String ORDER_ID_KEY="order.id.key";
    private static final int SDK_PAY_FLAG = 1;

    private OrderDetail orderDetail;
    private OrderWebService orderWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        orderId=getIntent().getLongExtra(ORDER_ID_KEY,-1);
        getOrderDetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        setOperateBtns(orders);
    }


    private void setOperateBtns(Orders order){
        int status=order.getStatus();
        if(status==ORDER_STATUS_PENDING_PAY){//待付款
            txtPay.setVisibility(View.VISIBLE);
            txtAlertSend.setVisibility(View.GONE);
            txtShowExpress.setVisibility(View.GONE);
            txtReceive.setVisibility(View.GONE);
            txtComment.setVisibility(View.GONE);
            txtCancel.setVisibility(View.VISIBLE);
            txtDelete.setVisibility(View.GONE);
        }else if(status==ORDER_STATUS_PRE_DELIVERED){//待发货
            txtPay.setVisibility(View.GONE);
            txtAlertSend.setVisibility(View.VISIBLE);
            txtShowExpress.setVisibility(View.GONE);
            txtReceive.setVisibility(View.GONE);
            txtComment.setVisibility(View.GONE);
            txtCancel.setVisibility(View.GONE);
            txtDelete.setVisibility(View.GONE);
        }else if(status==ORDER_STATUS_HAS_SENDED){//待收货
            txtPay.setVisibility(View.GONE);
            txtAlertSend.setVisibility(View.GONE);
            txtShowExpress.setVisibility(View.VISIBLE);
            txtReceive.setVisibility(View.VISIBLE);
            txtComment.setVisibility(View.GONE);
            txtCancel.setVisibility(View.GONE);
            txtDelete.setVisibility(View.GONE);
        }else if(status==ORDER_STATUS_PRE_EVALUATED){//待评价
            txtPay.setVisibility(View.GONE);
            txtAlertSend.setVisibility(View.GONE);
            txtShowExpress.setVisibility(View.VISIBLE);
            txtReceive.setVisibility(View.GONE);
            txtComment.setVisibility(View.VISIBLE);
            txtCancel.setVisibility(View.GONE);
            txtDelete.setVisibility(View.VISIBLE);
        }else if(status==ORDER_STATUS_HAS_COMPLETED){//已完成
            txtPay.setVisibility(View.GONE);
            txtAlertSend.setVisibility(View.GONE);
            txtShowExpress.setVisibility(View.VISIBLE);
            txtReceive.setVisibility(View.GONE);
            txtComment.setVisibility(View.GONE);
            txtCancel.setVisibility(View.GONE);
            txtDelete.setVisibility(View.VISIBLE);
        }else if(status==ORDER_STATUS_HAS_CANCELED){//已取消
            txtPay.setVisibility(View.GONE);
            txtAlertSend.setVisibility(View.GONE);
            txtShowExpress.setVisibility(View.GONE);
            txtReceive.setVisibility(View.GONE);
            txtComment.setVisibility(View.GONE);
            txtCancel.setVisibility(View.GONE);
            txtDelete.setVisibility(View.VISIBLE);
        }else if(status==ORDER_STATUS_CLOSED){//已关闭
            txtPay.setVisibility(View.GONE);
            txtAlertSend.setVisibility(View.GONE);
            txtShowExpress.setVisibility(View.GONE);
            txtReceive.setVisibility(View.GONE);
            txtComment.setVisibility(View.GONE);
            txtCancel.setVisibility(View.GONE);
            txtDelete.setVisibility(View.VISIBLE);
        }
    }


    public void showExpressDetail(View view){
        Intent i=new Intent(mContext,ExpressDetailActivity.class);
        i.putExtra(Orders.KEY,orderDetail.getOrders());
        startActivity(i);
    }

    /**
     * 提醒发货
     */
    private void remindSend(Long orderId){
        Call<ResponseBody> call=orderWebService.remindSend(mUser.getUserId(),orderId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        ToastUtil.showShort(mContext,"卖家已收到提醒！");
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


    /**
     * 取消订单
     */
    private void cancelOrder(Long orderId,String reason){
        Map<String,Object> params=new HashMap<>();
        params.put("cancelReason",reason);
        params.put("orderId",orderId);
        params.put("status", ORDER_STATUS_HAS_CANCELED);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=orderWebService.cancelOrder(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        EventBus.getDefault().post(new CancelOrderEvent());
                        ToastUtil.showShort(mContext,"订单已取消！");
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

    /**
     * 删除订单
     */
    private void deleteOrder(Long orderId){
        Map<String,Object> params=new HashMap<>();
        params.put("orderIds",new Long[]{orderId});
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=orderWebService.deleteOrder(mUser.getUserId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        EventBus.getDefault().post(new DeleteOrderEvent());
                        ToastUtil.showShort(mContext,"订单已删除！");
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

    /**
     * 订单收货
     */
    private void receive(Long orderId){
        Map<String,Object> params=new HashMap<>();
        params.put("orderId",orderId);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=orderWebService.receiveOrder(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        EventBus.getDefault().post(new ReceiveOrderEvent());
                        ToastUtil.showShort(mContext,"已收货！");
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

    private void getPayBody(final Long orderId){
        Map<String,Object> params=new HashMap<>();
        params.put("orderIds",orderId);
        //params.put("payAmount",mOrderEnsure.getPayAmount());
        params.put("payAmount",0.01);
        params.put("userId",mUser.getUserId());
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=orderWebService.pay(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        String body=new JSONObject(result).getString("result");
                        pay(body,orderId);
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


    private void pay(final String payBody,final Long orderId){
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask((Activity) mContext);
                Map<String, String> result = alipay.payV2(payBody, true);
                result.put("orderId",orderId+"");
                Log.i("msp", result.toString());
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    //PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    Map<String, String> result= (Map<String, String>) msg.obj;
                    toOrderDetailPage(Long.valueOf(result.get("orderId")));
                    break;
                }
            }
        }
    };


    private void toOrderDetailPage(Long orderId){
        Intent i=new Intent(mContext, OrderDetailActivity.class);
        i.putExtra(OrderDetailActivity.ORDER_ID_KEY,orderId);
        mContext.startActivity(i);
    }

    /**
     * 选择取消理由
     */
    int selectIndex=0;
    private void chooseCancelReason(final Long orderId){
        final String[] reason=new String[]{"我不想买了","信息填写错误，重新拍","卖家缺货","拍错了","其他原因"};
        new AlertDialog.Builder(mContext)
                .setTitle("请选择取消理由")
                .setSingleChoiceItems(reason, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
                                selectIndex=i;
                            }
                        }
                )
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancelOrder(orderId,reason[selectIndex]);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }


    /**
     * 确认删除
     */
    private void comfirmDelete(final Long orderId){
        new AlertDialog.Builder(mContext)
                .setTitle("删除提醒")
                .setMessage("您确定删除该订单吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteOrder(orderId);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }


    /**
     * 确认收货
     */
    private void comfirmReceive(final Long orderId){
        new AlertDialog.Builder(mContext)
                .setTitle("收货提醒")
                .setMessage("您确定收货吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        receive(orderId);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public void operateOrder(View v){
        switch (v.getId()){
            case R.id.txt_pay:

                break;
            case R.id.txt_alert_send:
                remindSend(orderId);
                break;
            case R.id.txt_show_express:
                break;
            case R.id.txt_receive:
                comfirmReceive(orderId);
                break;
            case R.id.txt_comment:
                break;
            case R.id.txt_cancel:
                chooseCancelReason(orderId);
                break;
            case R.id.txt_delete:
                comfirmDelete(orderId);
                break;
            default:break;
        }
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
        txtPay= (TextView) findViewById(R.id.txt_pay);
        txtAlertSend= (TextView) findViewById(R.id.txt_alert_send);
        txtShowExpress= (TextView) findViewById(R.id.txt_show_express);
        txtReceive= (TextView) findViewById(R.id.txt_receive);
        txtComment= (TextView) findViewById(R.id.txt_comment);
        txtCancel= (TextView) findViewById(R.id.txt_cancel);
        txtDelete= (TextView) findViewById(R.id.txt_delete);
    }
}
