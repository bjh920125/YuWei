package com.bap.yuwei.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.activity.sys.ReceiveAddressListActivity;
import com.bap.yuwei.adapter.OrderAdapter;
import com.bap.yuwei.entity.event.ReceiverAddressEvent;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.GoodsCart;
import com.bap.yuwei.entity.order.OrderEnsure;
import com.bap.yuwei.entity.sys.ShippingAddress;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
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
    private OrderWebService orderWebService;

    private OrderEnsure mOrderEnsure;
    private GoodsCart mGoodsCart;

    private OrderAdapter mAdapter;

    private SysWebService sysWebService;

    public static final String CART_IDS_KEY="cart.ids.key";
    private ArrayList<String> cartIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        sysWebService= MyApplication.getInstance().getWebService(SysWebService.class);
        mGoodsCart= (GoodsCart) getIntent().getSerializableExtra(GoodsCart.KEY);
        cartIds=getIntent().getStringArrayListExtra(CART_IDS_KEY);
        if(null==cartIds){
            getOrderInfo();
        }else {
            getOrderInfoByCart();
        }
        getReceiveAddress();
    }


    private void resetUIWithValues(){
        mAdapter=new OrderAdapter(mOrderEnsure,mContext);
        listView.setAdapter(mAdapter);
        txtTotalNum.setText("共"+mOrderEnsure.getTotalRecord()+"件，");
        txtTotalPrice.setText("￥"+mOrderEnsure.getPayAmount());
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
    }

    private void setReceiverAddressUI(ShippingAddress address){
        txtReceiverAdress.setText(address.getProvince()+address.getCity()+address.getRegion()+address.getStreet());
        rlReceiverInfo.setVisibility(View.VISIBLE);
        txtAddAddress.setVisibility(View.GONE);
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
    }
}
