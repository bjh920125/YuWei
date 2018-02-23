package com.bap.yuwei.activity.goods;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.activity.order.EnsureOrderActivity;
import com.bap.yuwei.adapter.CommentAdapter;
import com.bap.yuwei.adapter.GoodsImageAdapter;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.goods.Goods;
import com.bap.yuwei.entity.goods.GoodsModel;
import com.bap.yuwei.entity.goods.Shop;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.Evaluation;
import com.bap.yuwei.entity.order.GoodsCart;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.StringUtils;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.view.StickyScrollView;
import com.bap.yuwei.webservice.GoodsWebService;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoodsDetailActivity extends BaseActivity implements View.OnClickListener{

    private ConvenientBanner convenientBanner;
    private ImageView imgShop,imgSpecification;
    private TextView txtGoodsTitle,txtPrice,txtOldPrice,txtExpressPrice,txtSellNum,txtModelName,txtSelectNum,txtShopName,txtShopDesc;
    private TextView txtShopCollectUserTotal,txtRecentGoodsTotal,txtGoodsTotal;
    private TextView txtProduct,txtDetail,txtComment;
    private TextView txtIntroduce,txtSpecification,txtPackage;
    private LinearLayout llComment,llCommentDetail;
    private TextView txtTotalComment,txtUserName,txtDesc,txtModel;
    private TextView txtAllCommentTitle,txtAllComment,txtAdditionCommentTitle,txtAdditionComment,txtHasPicCommentTitle,txtHasPicComment;
    private TextView txtSellOut;
    private LRecyclerView rvComment;
    private ImageView imgHead;
    private TextView txtAddCarts,txtBuy;
    private RelativeLayout rlPackage;
    private WebView mWebView;
    private StickyScrollView mScrollView;
    private LinearLayout llDetail;
    private View addview;
    private TextView txtNum;
    private Button btnCollect;
    private boolean hasCollected=false;

    private PopupWindow popModels;
    private View popModelsView;
    private int preSelectPoistion;
    private int selectNum=1;

    private int color;
    private int selectColor;

    private Goods mGoods;
    private Shop mShop;

    private int queryCommentType=0;
    private final int ALL_COMMENT=0;//全部
    private final int ADDITION_COMMENT=1;//追评
    private final int HAS_PIC_COMMENT=2;//有图片
    private boolean isInitCommentUI=false;
    private List<Evaluation> evaluations;
    private CommentAdapter mCommentAdapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    private int pageSize=10;
    private int pageIndex=1;

    private int topDetail;

    private GoodsWebService goodsWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLoadingDialog();
        goodsWebService= MyApplication.getInstance().getWebService(GoodsWebService.class);
        mGoods= (Goods) getIntent().getSerializableExtra(Goods.KEY);
        evaluations=new ArrayList<>();
        addFootmark();
        getGoodsDetail();
        initRotationMaps();
        color=getResources().getColor(R.color.lightblack);
        selectColor=getResources().getColor(R.color.colorPrimary);

        mCommentAdapter=new CommentAdapter(mContext);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mCommentAdapter);
        rvComment.setAdapter(mLRecyclerViewAdapter);

        rvComment.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                evaluations.clear();
                mCommentAdapter.clear();
                pageIndex=1;
                getEvaluations();
            }
        });

        rvComment.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                pageIndex++;
                getEvaluations();
            }
        });
    }

    /**
     * 选择型号
     */
    public void chooseModel(View v){
        if(null==popModels) return;
        popModels.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.5f);
    }

    /**
     * 收藏
     */
    public void collectGoods(View v){
        if(isLogined())
        if(hasCollected){
            cancelGoodsCollect();
        }else{
            addGoodsCollect();
        }
    }

    /**
     * 添加商品到购物车
     */
    private void addCarts(){
        Map<String,Object> params=new HashMap<>();
        params.put("goodsId", mGoods.getGoodsId());
        params.put("goodsCount",selectNum);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=goodsWebService.addCarts(mUser.getUserId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        popModels.dismiss();
                        ToastUtil.showShort(mContext,"添加成功！");
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
     * 获取商品详情
     */
    private void getGoodsDetail(){
        Call<ResponseBody> call=goodsWebService.getGoodsDetail(mGoods.getGoodsId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        mGoods= mGson.fromJson(jo.toString(), Goods.class);
                        getShopDetail();
                        getGoodsCollect();
                        getEvaluations();
                        initGoodsUIWithValues();
                    }else{
                        if(appResponse.getCode()==ResponseCode.GOODS_HAS_WITHDRAW) {
                            setCannotBuy();
                        }else{
                            ToastUtil.showShort(mContext,appResponse.getMessage());
                        }
                    }
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

    /**
     * 根据型号获取商品详情
     */
    private void getGoodsDetailByModel(Long modelId){
        Map<String,Object> params=new HashMap<>();
        params.put("deviceType", Constants.DEVICE_TYPE);
        params.put("goodsModelId",modelId);
        params.put("goodsName",mGoods.getGoodsName());
        params.put("shopId",mShop.getShopId());
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=goodsWebService.getGoodsDetail(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        mGoods= mGson.fromJson(jo.toString(), Goods.class);
                        getGoodsCollect();
                        initGoodsUIWithValues();
                    }else{
                        if(appResponse.getCode()==ResponseCode.GOODS_HAS_WITHDRAW) {
                            setCannotBuy();
                        }else{
                            ToastUtil.showShort(mContext,appResponse.getMessage());
                        }
                    }
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

    /**
     * 设置按钮为不可购买状态
     */
    private void setCannotBuy(){
        int color=getResources().getColor(R.color.darkgrey);
        txtSellOut.setVisibility(View.VISIBLE);
        txtAddCarts.setEnabled(false);
        txtBuy.setEnabled(false);
        txtAddCarts.setTextColor(color);
        txtBuy.setTextColor(color);
    }

    /**
     * 获取店铺详情
     */
    private void getShopDetail(){
        Call<ResponseBody> call=goodsWebService.getShopDetail(mGoods.getShopId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        mShop= mGson.fromJson(jo.toString(), Shop.class);
                        initShopUIWithValues();
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
     * 获取收藏商品详情
     */
    private void getGoodsCollect(){
        if(null==mUser) return;
        Call<ResponseBody> call=goodsWebService.getGoodsCollect(mUser.getUserId(),mGoods.getGoodsId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        if(null != jo){//已收藏
                            hasCollected=true;
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

    /**
     * 收藏商品
     */
    private void addGoodsCollect(){
        if(null==mShop || null==mUser) return;
        Map<String,Object> params=new HashMap<>();
        params.put("goodsId", mGoods.getGoodsId());
        params.put("goodsTitle", mGoods.getTitle());
        params.put("shopId",mShop.getShopId());
        params.put("userId",mUser.getUserId());
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=goodsWebService.addGoodsCollect(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        hasCollected=true;
                        setCollectImage(true);
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
     * 取消收藏商品
     */
    private void cancelGoodsCollect(){
        if(null==mUser) return;
        Map<String,Object> params=new HashMap<>();
        params.put("goodsId", mGoods.getGoodsId());
        params.put("userId",mUser.getUserId());
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=goodsWebService.cancelGoodsCollect(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        hasCollected=false;
                        setCollectImage(false);
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
     * 新增足迹
     */
    private void addFootmark(){
        if(null==mUser) return;
        Map<String,Object> params=new HashMap<>();
        params.put("goodsId", mGoods.getGoodsId());
        params.put("userId",mUser.getUserId());
        params.put("goodsTitle",mGoods.getTitle());
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=goodsWebService.addFoormark(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){

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
     * 获取评价列表
     */
    private void getEvaluations(){
        Map<String,Object> params=new HashMap<>();
        params.put("page",pageIndex);
        params.put("size",pageSize);
        params.put("type",queryCommentType);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=goodsWebService.getEvaluations(mGoods.getGoodsId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONArray jo=new JSONObject(result).getJSONObject("result").getJSONArray("list");
                        List<Evaluation> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<Evaluation>>() {}.getType());
                        if(tempList!=null && tempList.size()>0){
                            evaluations.addAll(tempList);
                            mCommentAdapter.addAll(tempList);
                            mCommentAdapter.notifyDataSetChanged();
                            initCommentUIWithValues();
                        }else{
                            rvComment.setNoMore(true);
                        }
                        rvComment.refreshComplete(tempList.size());
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
     * 设置收藏图片显示
     */
    private void setCollectImage(boolean hasCollected){
        if(hasCollected){
            Drawable drawable= getResources().getDrawable(R.drawable.favourite_fill);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btnCollect.setCompoundDrawables(null,drawable,null,null);
        }else {
            Drawable drawable= getResources().getDrawable(R.drawable.shoucang);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btnCollect.setCompoundDrawables(null,drawable,null,null);
        }
    }

    private void initGoodsUIWithValues(){
        txtGoodsTitle.setText(mGoods.getTitle());
        txtPrice.setText("￥"+mGoods.getPreferentialPrice());
        txtOldPrice.setText("原价￥"+mGoods.getPrice()+"");
        txtExpressPrice.setText("快递："+mGoods.getFreight()+"元");
        txtModelName.setText(mGoods.getGoodsModelName());
        txtSellNum.setText("已卖出"+mGoods.getSellNum()+"件");
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+mGoods.getSpecification(),imgSpecification, DisplayImageOptionsUtil.getOptions());
        initRotationMaps();
        mWebView.loadDataWithBaseURL(null, mGoods.getGoodsPhoneDesc(), "text/html", "GB2312", null);
        initChooseModelView();
    }

    private void initCommentUIWithValues(){
        if(isInitCommentUI) return;
        txtAllComment.setText(mGoods.getTotalComment()+"");
        txtAdditionComment.setText(mGoods.getHasAdditionalCommentCount()+"");
        txtHasPicComment.setText(mGoods.getHasImageCount()+"");

        int totalComment=mGoods.getTotalComment();
        if(totalComment>0){
            Evaluation evaluation=evaluations.get(0);
            if(null==evaluation) return;
            llComment.setVisibility(View.VISIBLE);
            txtTotalComment.setText("商品评价（"+totalComment+"）");
            String userName=evaluation.getEvaluaterUsername();
            StringBuilder sb=new StringBuilder(userName);
            txtUserName.setText(sb.replace(1,userName.length()-1,"***"));
            txtDesc.setText(evaluation.getEvaluationComment());
            txtModel.setText("型号："+evaluation.getGoodsModel());
            ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+evaluation.getAvatar(),imgHead);
        }else {
            llComment.setVisibility(View.GONE);
        }
        isInitCommentUI=true;
    }

    private void initShopUIWithValues(){
        txtShopName.setText(mShop.getShopName());
        txtShopDesc.setText(mShop.getSummary());
        txtShopCollectUserTotal.setText(mShop.getShopCollectUserTotal()+"");
        txtGoodsTotal.setText(mShop.getGoodsTotal()+"");
        txtRecentGoodsTotal.setText(mShop.getRecentGoodsTotal()+"");
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+mShop.getShopIcon(),imgShop, DisplayImageOptionsUtil.getOptions());
    }

    /**
     * 初始化轮播图
     */
    private void initRotationMaps(){
        convenientBanner.setPages(new CBViewHolderCreator<GoodsImageAdapter>() {
            @Override
            public GoodsImageAdapter createHolder() {
                return new GoodsImageAdapter();
            }
        }, mGoods.getGoodsImages()).setPageIndicator(new int[]{R.drawable.dot_blur, R.drawable.dot_focus})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_add_cart://加入购物车
                if(isLogined())
                    addCarts();
                break;
            case R.id.txt_buy://立即购买
                if(isLogined())
                    buyNow();
                break;
            case R.id.txt_open_model_view://选择型号
                if(isLogined())
                    chooseModel(null);
                break;
            case R.id.img_close://关闭选择型号的view
                popModels.dismiss();
                break;
            case R.id.img_add://增加数量
                updateSelectNum(true);
                break;
            case R.id.img_less://减少数量
                updateSelectNum(false);
                break;
            default:break;
        }
    }

    /**
     * 立即购买
     */
    private void buyNow(){
        GoodsCart cart=new GoodsCart();
        cart.setGoodsId(mGoods.getGoodsId());
        cart.setGoodsCount(selectNum);
        Intent i=new Intent(mContext, EnsureOrderActivity.class);
        i.putExtra(GoodsCart.KEY,cart);
        startActivity(i);
    }

    /**
     * 展示店铺详情
     */
    public void showShop(View v){
        if(null==mShop) return;
        Intent intent=new Intent(mContext,ShopHomeActivity.class);
        intent.putExtra(Shop.KEY,mShop);
        startActivity(intent);
    }

    /**
     * 更改选择的数量
     */
    private void updateSelectNum(boolean isAdd){
        int num=Integer.parseInt(StringUtils.getTextViewValue(txtNum));
        if(isAdd){
            if(num<mGoods.getStockNum()){
                txtNum.setText(++num+"");
            }else{
                ToastUtil.showShort(mContext,"数量不能超过库存！");
            }
        }else{
            if(num>=2){
                txtNum.setText(--num+"");
            }
        }
    }

    /**
     * 商品介绍、规格、评价按钮
     */
    public void onDetailMenuClick(View v){
        txtIntroduce.setTextColor(color);
        txtSpecification.setTextColor(color);
        txtPackage.setTextColor(color);
        switch (v.getId()){
            case R.id.txt_introduce://商品介绍
                txtIntroduce.setTextColor(selectColor);
                mWebView.setVisibility(View.VISIBLE);
                imgSpecification.setVisibility(View.GONE);
                rlPackage.setVisibility(View.GONE);
                addview.setVisibility(View.GONE);
                break;
            case R.id.txt_spefi://规格参数
                txtSpecification.setTextColor(selectColor);
                mWebView.setVisibility(View.GONE);
                imgSpecification.setVisibility(View.VISIBLE);
                rlPackage.setVisibility(View.GONE);
                addview.setVisibility(View.VISIBLE);
                break;
            case R.id.txt_package://包装售后
                txtPackage.setTextColor(selectColor);
                mWebView.setVisibility(View.GONE);
                imgSpecification.setVisibility(View.GONE);
                rlPackage.setVisibility(View.VISIBLE);
                addview.setVisibility(View.VISIBLE);
                break;
            default:break;
        }
        mScrollView.smoothScrollTo(0,topDetail);
    }

    /**
     * 标题上的商品、详情、评价按钮
     */
    public void toPosition(View v){
        txtProduct.setTextColor(color);
        txtDetail.setTextColor(color);
        txtComment.setTextColor(color);
        switch (v.getId()){
            case R.id.txt_product://商品
                mScrollView.smoothScrollTo(0,0);
                txtProduct.setTextColor(selectColor);
                showDetail();
                break;
            case R.id.txt_detail://详情
                mScrollView.smoothScrollTo(0,topDetail);
                txtDetail.setTextColor(selectColor);
                showDetail();
                break;
            case R.id.txt_comment://评论
                txtComment.setTextColor(selectColor);
                mScrollView.setVisibility(View.GONE);
                llCommentDetail.setVisibility(View.VISIBLE);
                rvComment.setVisibility(View.VISIBLE);
                break;
            default:break;
        }
    }

    /**
     * 根据类型显示评论
     */
    public void getCommentByType(View v){
        txtAllComment.setTextColor(color);
        txtAllCommentTitle.setTextColor(color);
        txtAdditionCommentTitle.setTextColor(color);
        txtAdditionComment.setTextColor(color);
        txtHasPicCommentTitle.setTextColor(color);
        txtHasPicComment.setTextColor(color);
        switch (v.getId()){
            case R.id.ll_all_comment://所有评论
                queryCommentType=ALL_COMMENT;
                txtAllComment.setTextColor(selectColor);
                txtAllCommentTitle.setTextColor(selectColor);
                break;
            case R.id.ll_addtional_comment://追加评论
                queryCommentType=ADDITION_COMMENT;
                txtAdditionCommentTitle.setTextColor(selectColor);
                txtAdditionComment.setTextColor(selectColor);
                break;
            case R.id.ll_pic_comment://有图的评论
                queryCommentType=HAS_PIC_COMMENT;
                txtHasPicCommentTitle.setTextColor(selectColor);
                txtHasPicComment.setTextColor(selectColor);
                break;
            default:break;
        }
        rvComment.refresh();
    }

    /**
     * 显示评论
     */
    public void showAllComment(View v){
        mScrollView.setVisibility(View.GONE);
        llCommentDetail.setVisibility(View.VISIBLE);
        rvComment.setVisibility(View.VISIBLE);
        txtProduct.setTextColor(color);
        txtDetail.setTextColor(color);
        txtComment.setTextColor(color);
        txtComment.setTextColor(selectColor);
    }

    /**
     * 联系卖家
     */
    public void contactSeller(View v){
        if(null==mShop) return;
        String url="mqqwpa://im/chat?chat_type=wpa&uin="+mShop.getQq();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    /**
     * 显示详情
     */
    private void showDetail(){
        mScrollView.setVisibility(View.VISIBLE);
        llCommentDetail.setVisibility(View.GONE);
        rvComment.setVisibility(View.GONE);
    }

    public void onBackClick(View v){
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        topDetail = llDetail.getTop()+55;  //滑动需要的距离
    }

    /**
     * 初始化选择型号view
     */
    private void initChooseModelView(){
        ImageView imgGoods=null;
        TextView txtPrice=null;
        TextView txtStock=null;
        final TagFlowLayout modelTfl;
        ImageView imgMore=null;
        ImageView imgLess=null;
        ImageView imgClose=null;
        TextView txtAddCart=null;
        if(null==popModelsView){
            popModelsView = LayoutInflater.from(mContext).inflate(R.layout.view_choose_model, null);
            imgGoods= (ImageView) popModelsView.findViewById(R.id.img_goods);
            txtPrice= (TextView) popModelsView.findViewById(R.id.txt_price);
            txtStock= (TextView) popModelsView.findViewById(R.id.txt_stock);
            modelTfl= (TagFlowLayout) popModelsView.findViewById(R.id.flowlayout);
            imgMore= (ImageView) popModelsView.findViewById(R.id.img_add);
            imgLess= (ImageView) popModelsView.findViewById(R.id.img_less);
            imgClose= (ImageView) popModelsView.findViewById(R.id.img_close);
            txtAddCart=(TextView) popModelsView.findViewById(R.id.txt_add_cart);
            txtNum= (TextView) popModelsView.findViewById(R.id.txt_num);
            imgMore.setOnClickListener(this);
            imgLess.setOnClickListener(this);
            imgClose.setOnClickListener(this);

            modelTfl.setMaxSelectCount(1);

           final TagAdapter adapter=new TagAdapter<GoodsModel>(mGoods.getGoodsModels()) {
                @Override
                public View getView(FlowLayout parent, int position, GoodsModel o) {
                    TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_model, modelTfl, false);
                    tv.setText(o.getGoodsModelName());
                    if(mGoods.getGoodsModelName().equals(o.getGoodsModelName())){
                        preSelectPoistion=position;
                    }
                    return tv;
                }
            };
            modelTfl.setAdapter(adapter);
            adapter.setSelectedList(preSelectPoistion);

            modelTfl.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                @Override
                public boolean onTagClick(View view, int position, FlowLayout parent) {
                    adapter.setSelectedList(position);
                    return true;
                }
            });

            modelTfl.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
                @Override
                public void onSelected(Set<Integer> selectPosSet) {
                    for (Integer position : selectPosSet) {
                        GoodsModel goodsModel = mGoods.getGoodsModels().get(position);
                        getGoodsDetailByModel(goodsModel.getGoodsModelId());
                    }
                }
            });
        }

        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+mGoods.getGoodsImages().get(0).getGoodsImagePath(),imgGoods, DisplayImageOptionsUtil.getOptionsRounded());
        txtPrice.setText("￥"+mGoods.getPreferentialPrice());
        txtStock.setText("库存"+mGoods.getStockNum()+"件");
        txtAddCart.setOnClickListener(this);
        popModels = new PopupWindow(popModelsView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popModels.setAnimationStyle(R.style.popupwindow_anim);
        popModels.setOutsideTouchable(true);
        popModels.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
                txtModelName.setText(mGoods.getGoodsModelName());
                txtSelectNum.setText(StringUtils.getTextViewValue(txtNum)+"件");
            }
        });
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
    protected int getLayoutId() {
        return R.layout.activity_goods_detail;
    }


    @Override
    protected void initView() {
        convenientBanner=(ConvenientBanner) findViewById(R.id.banner);
        txtGoodsTitle= (TextView) findViewById(R.id.txt_goods_title);
        txtPrice= (TextView) findViewById(R.id.txt_price);
        txtOldPrice= (TextView) findViewById(R.id.txt_old_price);
        txtExpressPrice= (TextView) findViewById(R.id.txt_express_price);
        txtSellNum= (TextView) findViewById(R.id.txt_sell_num);
        txtModelName= (TextView) findViewById(R.id.txt_model_name);
        txtSelectNum= (TextView) findViewById(R.id.txt_select_num);
        txtShopName= (TextView) findViewById(R.id.txt_shop_name);
        txtShopDesc= (TextView) findViewById(R.id.txt_shop_desc);
        imgShop= (ImageView) findViewById(R.id.img_shop);
        txtShopCollectUserTotal= (TextView) findViewById(R.id.txt_shop_collect_num);
        txtRecentGoodsTotal= (TextView) findViewById(R.id.txt_new_num);
        txtGoodsTotal= (TextView) findViewById(R.id.txt_goods_num);
        mWebView= (WebView) findViewById(R.id.webview_content);
        mScrollView= (StickyScrollView) findViewById(R.id.scrollview);
        llDetail= (LinearLayout) findViewById(R.id.ll_detail);
        txtProduct=(TextView) findViewById(R.id.txt_product);
        txtDetail=(TextView) findViewById(R.id.txt_detail);
        txtComment=(TextView) findViewById(R.id.txt_comment);
        txtIntroduce=(TextView) findViewById(R.id.txt_introduce);
        txtSpecification=(TextView) findViewById(R.id.txt_spefi);
        txtPackage=(TextView) findViewById(R.id.txt_package);
        imgSpecification= (ImageView) findViewById(R.id.img_specification);
        rlPackage= (RelativeLayout) findViewById(R.id.rl_package);
        addview=findViewById(R.id.addview);
        btnCollect= (Button) findViewById(R.id.btn_collect);
        txtOldPrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG); //中划线
        txtAddCarts= (TextView) findViewById(R.id.txt_open_model_view);
        txtBuy= (TextView) findViewById(R.id.txt_buy);
        llComment= (LinearLayout) findViewById(R.id.ll_comment);
        llCommentDetail= (LinearLayout) findViewById(R.id.ll_comment_detail);
        txtTotalComment= (TextView) findViewById(R.id.txt_total_comment);
        txtUserName= (TextView) findViewById(R.id.txt_user_name);
        txtDesc= (TextView) findViewById(R.id.txt_desc);
        txtModel= (TextView) findViewById(R.id.txt_model);
        imgHead= (ImageView) findViewById(R.id.img_head);
        rvComment= (LRecyclerView) findViewById(R.id.rv_comment);
        txtAllCommentTitle= (TextView) findViewById(R.id.txt_all_comment_title);
        txtAllComment= (TextView) findViewById(R.id.txt_all_comment);
        txtAdditionCommentTitle= (TextView) findViewById(R.id.txt_addition_comment_title);
        txtAdditionComment= (TextView) findViewById(R.id.txt_addition_comment);
        txtHasPicCommentTitle= (TextView) findViewById(R.id.txt_has_pic_comment_title);
        txtHasPicComment= (TextView) findViewById(R.id.txt_has_pic_comment);
        txtSellOut= (TextView) findViewById(R.id.txt_sell_out);
        rvComment.setHasFixedSize(true);
        rvComment.setLayoutManager(new LinearLayoutManager(mContext));
        rvComment.setHeaderViewColor(R.color.colorAccent, R.color.dark ,android.R.color.white);
        rvComment.setFooterViewColor(R.color.colorAccent, R.color.dark ,android.R.color.white);
        rvComment.setFooterViewHint("拼命加载中","已经全部为你呈现了","网络不给力啊，点击再试一次吧");
        txtAddCarts.setOnClickListener(this);
        txtBuy.setOnClickListener(this);
    }

   
}
