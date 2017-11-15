package com.bap.yuwei.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.order.ExpressDetailActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.event.CancelOrderEvent;
import com.bap.yuwei.entity.event.DeleteOrderEvent;
import com.bap.yuwei.entity.event.ReceiveOrderEvent;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.OrderItem;
import com.bap.yuwei.entity.order.Orders;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.OrderWebService;
import com.google.gson.Gson;
import com.linearlistview.LinearListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
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

/**
 * Created by Administrator on 2017/11/14.
 */

public class OrderListAdapter extends ListBaseAdapter<Orders> {

    private List<Orders> orderses;
    private OrderWebService orderWebService;
    private String userId;
    private Gson mGson;
    private MediaType jsonMediaType= MediaType.parse("application/json; charset=utf-8");

    public OrderListAdapter(Context context,List<Orders> orderses,String userId) {
        super(context);
        this.orderses=orderses;
        this.userId=userId;
        this.orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        this.mGson=new Gson();
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_order_list_module;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        ImageView imgshop=holder.getView(R.id.img_shop);
        TextView txtShopName=holder.getView(R.id.txt_shop_name);
        TextView txtStatus=holder.getView(R.id.txt_status);
        LinearListView lvOrderItem=holder.getView(R.id.lv_order_item);
        TextView txtTotalNum=holder.getView(R.id.txt_total_num);
        TextView txtTotalPrice=holder.getView(R.id.txt_price);
        TextView txtPay=holder.getView(R.id.txt_pay);
        TextView txtAlertSend=holder.getView(R.id.txt_alert_send);
        TextView txtShowExpress=holder.getView(R.id.txt_show_express);
        TextView txtReceive=holder.getView(R.id.txt_receive);
        TextView txtComment=holder.getView(R.id.txt_comment);
        TextView txtCancel=holder.getView(R.id.txt_cancel);
        TextView txtDelete=holder.getView(R.id.txt_delete);

        final Orders order=orderses.get(position);
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+order.getShopIcon(),imgshop);
        txtShopName.setText(order.getShopName());
        txtStatus.setText(order.getStatusText());
        txtTotalNum.setText("共"+order.getTotalGoodsCount()+"件商品");
        txtTotalPrice.setText("合计：￥"+order.getRealPayAmount()+"（含运费￥"+order.getFreight()+"）");

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

        List<OrderItem> items=order.getOrderItems();
        OrderListItemAdapter adapter=new OrderListItemAdapter(mContext,items);
        lvOrderItem.setAdapter(adapter);

        txtAlertSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindSend(order.getOrderId());
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseCancelReason(order.getOrderId());
            }
        });

        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comfirmDelete(order.getOrderId());
            }
        });

        txtShowExpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(mContext, ExpressDetailActivity.class);
                i.putExtra(Orders.KEY,order);
                mContext.startActivity(i);
            }
        });

        txtReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comfirmReceive(order.getOrderId());
            }
        });


    }


    /**
     * 提醒发货
     */
    private void remindSend(Long orderId){
        Call<ResponseBody> call=orderWebService.remindSend(userId,orderId);
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
        params.put("status",Constants.ORDER_STATUS_HAS_CANCELED);
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
        Call<ResponseBody> call=orderWebService.deleteOrder(userId,body);
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

}
