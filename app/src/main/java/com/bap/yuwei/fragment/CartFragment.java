package com.bap.yuwei.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.order.EnsureOrderActivity;
import com.bap.yuwei.adapter.CartAdapter;
import com.bap.yuwei.entity.event.CartMoneyEvent;
import com.bap.yuwei.entity.event.UpdateCartEvent;
import com.bap.yuwei.entity.event.UpdateCartNumEvent;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.GoodsCart;
import com.bap.yuwei.entity.order.MyGoodsCart;
import com.bap.yuwei.fragment.base.BaseFragment;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.GoodsWebService;
import com.google.gson.reflect.TypeToken;
import com.linearlistview.LinearListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/10/27.
 */

public class CartFragment extends BaseFragment implements View.OnClickListener{

    private TextView txtPrice,txtPay,txtEdit;
    private LinearListView lvCarts;
    private RelativeLayout rlBottom;
    private CheckBox cbAll;
    private List<MyGoodsCart> myGoodsCarts;
    private CartAdapter mAdapter;
    private GoodsWebService goodsWebService;

    private BigDecimal total;

    public final int PAY=0;
    public final int EDIT=1;
    private int model=PAY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsWebService= MyApplication.getInstance().getWebService(GoodsWebService.class);
        total=new BigDecimal(0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myGoodsCarts=new ArrayList<>();
        mAdapter=new CartAdapter(myGoodsCarts,mContext);
        lvCarts.setAdapter(mAdapter);
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for(MyGoodsCart goodsCart:myGoodsCarts) {
                    goodsCart.setChecked(b);
                    for (GoodsCart cartItem : goodsCart.getCartItems()) {
                        cartItem.setChecked(b);
                    }
                }
                mAdapter.notifyDataSetChanged();
                calcMoney();
                txtPrice.setText("￥"+total);
            }
        });
        getCarts();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateCarts(CartMoneyEvent event){
        calcMoney();
        txtPrice.setText("￥"+total);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateCarts(UpdateCartEvent event){
        updateCarts(event.cartId,event.num);
    }



    private void calcMoney(){
        BigDecimal money=new BigDecimal(0);
        for(MyGoodsCart goodsCart:myGoodsCarts){
            for(GoodsCart cartItem:goodsCart.getCartItems()){
                if(cartItem.isChecked()){
                    money =money.add(cartItem.getPreferentialPrice().multiply(new BigDecimal(cartItem.getGoodsCount())));
                }
            }
        }
        total=money;
    }

    private List<Long> getSelectCartIds(){
        List<Long> cartIds=new ArrayList<>();
        for(MyGoodsCart goodsCart:myGoodsCarts) {
            for (GoodsCart cartItem : goodsCart.getCartItems()) {
                if (cartItem.isChecked()) {
                    cartIds.add(cartItem.getGoodsCartId());
                }
            }
        }
        return cartIds;
    }

    private void updateCarts(Long cartId,int goodsCount){
        showLoadingDialog();
        Map<String,Object> params=new HashMap<>();
        params.put("goodsCount", goodsCount);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=goodsWebService.updateCarts(mUser.getUserId(),cartId,body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        getCarts();
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

    private void deleteCarts(){
        showLoadingDialog();
        Map<String,Object> params=new HashMap<>();
        params.put("cartIds", getSelectCartIds());
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=goodsWebService.deleteCarts(mUser.getUserId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        getCarts();
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

    private void getCarts(){
        if(null==mUser) return;
        showLoadingDialog();
        Call<ResponseBody> call=goodsWebService.getCarts(mUser.getUserId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        myGoodsCarts.clear();
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        JSONArray ja=jo.getJSONArray("shopItems");
                        List<MyGoodsCart> templist=mGson.fromJson(ja.toString(), new TypeToken<List<MyGoodsCart>>() {}.getType());
                        if(null!=templist && templist.size()>0){
                            rlBottom.setVisibility(View.VISIBLE);
                            myGoodsCarts.addAll(templist);
                            mAdapter.notifyDataSetChanged();
                            EventBus.getDefault().post(new UpdateCartNumEvent());
                            txtPrice.setText("￥0");
                            cbAll.setChecked(false);
                        }else{
                            rlBottom.setVisibility(View.GONE);
                        }
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

    private void resetUI(){
        txtPrice.setText("￥0");
        cbAll.setChecked(false);
        getCarts();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            resetUI();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resetUI();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_pay_or_del:
                payOrDelete();
                break;
            case R.id.txt_edit:
                changeModel();
                break;
            default:break;
        }
    }

    private void payOrDelete(){
        ArrayList<String> cartIds=new ArrayList<>();
        for(MyGoodsCart goodsCart:myGoodsCarts) {
            for (GoodsCart cartItem : goodsCart.getCartItems()) {
                if(cartItem.isChecked()){
                    cartIds.add(cartItem.getGoodsCartId()+"");
                }
            }
        }
        if(cartIds.size()<=0){
            ToastUtil.showShort(mContext,"请先选择商品！");
            return;
        }

        if(model==PAY){
            Intent i=new Intent(mContext, EnsureOrderActivity.class);
            i.putStringArrayListExtra(EnsureOrderActivity.CART_IDS_KEY,cartIds);
            startActivity(i);
        }else {
            deleteCarts();
        }
    }

    private void changeModel(){
        if(model==PAY){
            model=EDIT;
            txtPay.setText("删除");
            txtEdit.setText("完成");
        }else {
            model=PAY;
            txtPay.setText("结算");
            txtEdit.setText("编辑");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_cart, container, false);
        lvCarts= (LinearListView) fragmentView.findViewById(R.id.lv_cart);
        txtPay= (TextView) fragmentView.findViewById(R.id.txt_pay_or_del);
        txtPrice=(TextView) fragmentView.findViewById(R.id.txt_price);
        txtEdit=(TextView) fragmentView.findViewById(R.id.txt_edit);
        cbAll= (CheckBox) fragmentView.findViewById(R.id.cb_all);
        rlBottom= (RelativeLayout) fragmentView.findViewById(R.id.rl_bottom);
        txtEdit.setOnClickListener(this);
        txtPay.setOnClickListener(this);
        LinearLayout emptyView= (LinearLayout) fragmentView.findViewById(R.id.view_empty_cart);
        lvCarts.setEmptyView(emptyView);
        return fragmentView;
    }


    @Override
    public void initView() {

    }
}
