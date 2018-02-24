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
 * 购物车
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

    public static final Integer PAY=0;
    public static final Integer EDIT=1;
    public static Integer model=PAY;

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
                        if(model==PAY){
                            if(cartItem.getIsValid()) {
                                cartItem.setChecked(b);
                            }
                        }else {
                            cartItem.setChecked(b);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                calcMoney();
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
        updateCarts(event.cart,event.cartId,event.num);
    }


    /**
     * 计算总金额
     */
    private void calcMoney(){
        if(model==EDIT) return;
        int num=0;
        BigDecimal money=new BigDecimal(0);
        for(MyGoodsCart goodsCart:myGoodsCarts){
            for(GoodsCart cartItem:goodsCart.getCartItems()){
                if(cartItem.isChecked()){
                    money =money.add(cartItem.getPreferentialPrice().multiply(new BigDecimal(cartItem.getGoodsCount())));
                    num+=cartItem.getGoodsCount();
                }
            }
        }
        total=money;
        txtPay.setText("结算("+num+")");
        txtPrice.setText("￥"+total);

        if(num<=0){
            txtPay.setBackgroundColor(getResources().getColor(R.color.darkgrey));
        }else {
            txtPay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    /**
     * 获取选择的购物车id
     */
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

    /**
     * 更新购物车
     */
    private void updateCarts(final GoodsCart cart, Long cartId, final int goodsCount){
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
                        //getCarts();
                        cart.setGoodsCount(goodsCount);
                        mAdapter.notifyDataSetChanged();
                        calcMoney();
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
     * 删除购物车商品
     */
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


    /**
     * 获取购物车
     */
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
                            txtPrice.setText("￥0");
                            cbAll.setChecked(false);
                        }else{
                            rlBottom.setVisibility(View.GONE);
                        }
                        mAdapter.notifyDataSetChanged();
                        EventBus.getDefault().post(new UpdateCartNumEvent());
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
        if(model==PAY){
            txtPay.setText("结算(0)");
            txtPay.setBackgroundColor(getResources().getColor(R.color.darkgrey));
        }else {
            txtPay.setText("删除");
            txtPay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
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
            case R.id.txt_pay_or_del://提交订单、删除
                payOrDelete();
                break;
            case R.id.txt_edit://编辑
                changeModel();
                break;
            default:break;
        }
    }

    /**
     * 提交订单/删除
     */
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

    /**
     * 切换编辑和提交订单的模式
     */
    private void changeModel(){
        if(model==PAY){
            model=EDIT;
            txtPay.setText("删除");
            txtEdit.setText("完成");
            txtPay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }else {
            model=PAY;
            txtPay.setText("结算");
            txtEdit.setText("编辑");
            //从编辑切换到提交订单模式时要去除掉下架的商品
            for(MyGoodsCart goodsCart:myGoodsCarts) {
                for (GoodsCart cartItem : goodsCart.getCartItems()) {
                    if(!cartItem.getIsValid()) {
                        cartItem.setChecked(false);
                    }
                }
            }
            mAdapter.notifyDataSetChanged();
        }
        calcMoney();
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
