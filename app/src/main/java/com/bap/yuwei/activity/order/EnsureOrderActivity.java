package com.bap.yuwei.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.activity.sys.ReceiveAddressListActivity;
import com.bap.yuwei.adapter.OrderAdapter;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.event.ChooseInvoiceEvent;
import com.bap.yuwei.entity.event.ReceiverAddressEvent;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.GoodsCart;
import com.bap.yuwei.entity.order.GoodsItemForm;
import com.bap.yuwei.entity.order.OrderDetail;
import com.bap.yuwei.entity.order.OrderEnsure;
import com.bap.yuwei.entity.order.OrderItemForm;
import com.bap.yuwei.entity.order.OrderShop;
import com.bap.yuwei.entity.order.PayOrderForm;
import com.bap.yuwei.entity.order.UserInvoice;
import com.bap.yuwei.entity.sys.ShippingAddress;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.util.alipay.PayResult;
import com.bap.yuwei.webservice.OrderWebService;
import com.bap.yuwei.webservice.SysWebService;
import com.google.gson.reflect.TypeToken;
import com.linearlistview.LinearListView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnsureOrderActivity extends BaseActivity {

    private TextView txtReceiver,txtTel,txtReceiverAdress;
    private LinearListView listView;
    private RelativeLayout rlReceiverInfo;
    private TextView txtAddAddress;
    private TextView txtTotalNum,txtTotalPrice;
    private TextView txtInvoiceType,txtInvoiceHeader;
    private PopupWindow popToPay;

    private OrderEnsure mOrderEnsure;
    private GoodsCart mGoodsCart;
    private Long userInvoiceId;
    private ShippingAddress mSelectedAddress;
    private int orderResource;
    private OrderAdapter mAdapter;

    public static final String CART_IDS_KEY="cart.ids.key";
    private ArrayList<String> cartIds;
    private String orderIds;

    private SysWebService sysWebService;
    private OrderWebService orderWebService;

    private static final int SDK_PAY_FLAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        sysWebService= MyApplication.getInstance().getWebService(SysWebService.class);
        mGoodsCart= (GoodsCart) getIntent().getSerializableExtra(GoodsCart.KEY);
        cartIds=getIntent().getStringArrayListExtra(CART_IDS_KEY);
        if(null==cartIds){
            orderResource=Constants.ORDER_RESUORCE_BUY;
            getOrderInfo();
        }else {
            orderResource=Constants.ORDER_RESUORCE_CART;
            getOrderInfoByCart();
        }
        getReceiveAddress();
    }


    private void pay(final String payBody){
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(EnsureOrderActivity.this);
                Map<String, String> result = alipay.payV2(payBody, true);
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

    private void getPayBody(){
        showLoadingDialog();
        Map<String,Object> params=new HashMap<>();
        params.put("orderIds",orderIds);
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
                        pay(body);
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


    private PayOrderForm getCommitOrderEntity(){
        PayOrderForm payOrderForm=new PayOrderForm();
        payOrderForm.setBuyerName(mSelectedAddress.getConsignee());
        payOrderForm.setBuyerPhone(mSelectedAddress.getCellphone());
        payOrderForm.setBuyerAddress(mSelectedAddress.getProvince()+mSelectedAddress.getCity()+mSelectedAddress.getRegion()+mSelectedAddress.getStreet());
        payOrderForm.setUserId(Long.valueOf(mUser.getUserId()));
        payOrderForm.setInvoiceId(userInvoiceId);
        payOrderForm.setPayType(0);
        if(orderResource==Constants.ORDER_RESUORCE_BUY){
            payOrderForm.setFrom(Constants.ORDER_RESUORCE_BUY);
            List<GoodsItemForm> goodsItemForms=new ArrayList<>();
            GoodsItemForm goodsItemForm=new GoodsItemForm();
            OrderShop orderShop=mOrderEnsure.getShopItems().get(0);
            GoodsCart goodsCart=orderShop.getCartItems().get(0);
            goodsItemForm.setGoodsId(goodsCart.getGoodsId());
            goodsItemForm.setGoodsCount(goodsCart.getGoodsCount());
            goodsItemForms.add(goodsItemForm);
            List<OrderItemForm> orderItemForms=new ArrayList<>();
            OrderItemForm orderItemForm=new OrderItemForm();
            orderItemForm.setShopId(goodsCart.getShopId());
            orderItemForm.setDeliveryType(0);
            orderItemForm.setGoodsItems(goodsItemForms);
            orderItemForm.setBuyerMessage(orderShop.getBuyerMsg());
            orderItemForms.add(orderItemForm);
            payOrderForm.setShopItems(orderItemForms);
        }else {
            payOrderForm.setFrom(Constants.ORDER_RESUORCE_CART);
        }
        return payOrderForm;
    }

    public void commitOrder(View v){
        showLoadingDialog();
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(getCommitOrderEntity()));
        Call<ResponseBody> call=orderWebService.commitOrder(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        orderIds=new JSONObject(result).getString("result");
                        initPayView();
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


    private void resetUIWithValues(){
        mAdapter=new OrderAdapter(mOrderEnsure,mContext);
        listView.setAdapter(mAdapter);
        txtTotalNum.setText("共"+mOrderEnsure.getTotalRecord()+"件，");
        txtTotalPrice.setText("￥"+mOrderEnsure.getPayAmount());

        UserInvoice userInvoice=mOrderEnsure.getInvoice();
        userInvoiceId=userInvoice.getUserInvoiceId();
        int type=userInvoice.getType();
        if(type== Constants.INVOICE_COMMON){
            txtInvoiceType.setText("纸质");
        }else if(type== Constants.INVOICE_ELEC){
            txtInvoiceType.setText("电子");
        }else if(type== Constants.INVOICE_VAT){
            txtInvoiceType.setText("增值");
        }

        StringBuilder sb= new StringBuilder();
        sb.append("（"+userInvoice.getContent()+"-");
        if(userInvoice.getHeaderType()==Constants.INVOICE_HEADER_PERSONAL){
            sb.append("个人）");
        }else {
         sb.append("单位）");
        }
        txtInvoiceHeader.setText(sb.toString());
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
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        mOrderEnsure=mGson.fromJson(jo.toString(),OrderEnsure.class);
                        resetUIWithValues();
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

    private void getOrderInfoByCart(){
        showLoadingDialog();
        Map<String,Object> params=new HashMap<>();
        params.put("cartIds",cartIds);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=orderWebService.getOrderInfoByCart(mUser.getUserId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        mOrderEnsure=mGson.fromJson(jo.toString(),OrderEnsure.class);
                        resetUIWithValues();
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

    /**
     * 获取收获地址
     */
    private void getReceiveAddress(){
        Call<ResponseBody> call=sysWebService.getReceiveAddress(mUser.getUserId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONArray ja=new JSONObject(result).getJSONArray("result");
                        List<ShippingAddress> tempList=mGson.fromJson(ja.toString(),new TypeToken<List<ShippingAddress>>() {}.getType());
                        if(null!=tempList && tempList.size()>0){
                            ShippingAddress address=tempList.get(0);
                            mSelectedAddress=address;
                            txtReceiver.setText(address.getConsignee());
                            txtTel.setText(address.getCellphone());
                            setReceiverAddressUI(address);
                        }else {
                            rlReceiverInfo.setVisibility(View.GONE);
                            txtAddAddress.setVisibility(View.VISIBLE);
                        }
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


    public void chooseAddress(View view){
        Intent i=new Intent(mContext, ReceiveAddressListActivity.class);
        startActivity(i);
    }

    public void setInvoice(View view){
        Intent i=new Intent(mContext, InvoiceSetActivity.class);
        i.putExtra(OrderEnsure.KEY,mOrderEnsure);
        startActivity(i);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void chooseAddressEvent(ReceiverAddressEvent event){
        setReceiverAddressUI(event.shippingAddress);
        mSelectedAddress=event.shippingAddress;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void chooseInvoiceEvent(ChooseInvoiceEvent event){
        mOrderEnsure.setInvoice(event.userInvoice);
        resetUIWithValues();
    }

    private void setReceiverAddressUI(ShippingAddress address){
        txtReceiverAdress.setText(address.getProvince()+address.getCity()+address.getRegion()+address.getStreet());
        rlReceiverInfo.setVisibility(View.VISIBLE);
        txtAddAddress.setVisibility(View.GONE);
    }

    private void initPayView(){
        View popToPayview = LayoutInflater.from(mContext).inflate(R.layout.view_ensure_pay, null);
        TextView txtName= (TextView) popToPayview.findViewById(R.id.txt_order_name);
        TextView txtPrice= (TextView) popToPayview.findViewById(R.id.txt_price);
        ImageView imgclose= (ImageView) popToPayview.findViewById(R.id.img_close);
        TextView txtPay=(TextView) popToPayview.findViewById(R.id.txt_pay);
        txtName.setText(mOrderEnsure.getShopItems().get(0).getCartItems().get(0).getTitle());
        txtPrice.setText("￥"+mOrderEnsure.getPayAmount());
        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popToPay.dismiss();
            }
        });
        txtPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPayBody();
            }
        });
        popToPay = new PopupWindow(popToPayview, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popToPay.setAnimationStyle(R.style.popupwindow_anim);
        popToPay.setOutsideTouchable(true);
        popToPay.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });

        popToPay.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.5f);
    }


    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ensure_order;
    }

    @Override
    protected void initView() {
        listView= (LinearListView) findViewById(R.id.lv_order);
        txtReceiver= (TextView) findViewById(R.id.txt_receiver);
        txtTel= (TextView) findViewById(R.id.txt_tel);
        txtReceiverAdress= (TextView) findViewById(R.id.txt_address);
        rlReceiverInfo= (RelativeLayout) findViewById(R.id.rl_receiver_info);
        txtAddAddress=(TextView) findViewById(R.id.txt_add_address);
        txtTotalNum=(TextView) findViewById(R.id.txt_total_num);
        txtTotalPrice=(TextView) findViewById(R.id.txt_price);
        txtInvoiceType=(TextView) findViewById(R.id.txt_invoice_type);
        txtInvoiceHeader=(TextView) findViewById(R.id.txt_invoice_header);
    }

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    //String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    //String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    /**
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        //Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();

                        toOrderDetailPage();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();
                        toOrderDetailPage();
                    }*/
                    toOrderDetailPage();
                    break;
                }
            }
        }
    };

    private void toOrderDetailPage(){
        popToPay.dismiss();
        Intent i=new Intent(mContext, OrderDetailActivity.class);
        i.putExtra(OrderDetailActivity.ORDER_ID_KEY,orderIds.split(",")[0]);
        startActivity(i);
    }
}
