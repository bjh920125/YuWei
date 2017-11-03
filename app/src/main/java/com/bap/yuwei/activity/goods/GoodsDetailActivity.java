package com.bap.yuwei.activity.goods;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.bap.yuwei.adapter.GoodsImageAdapter;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.goods.Goods;
import com.bap.yuwei.entity.goods.GoodsModel;
import com.bap.yuwei.entity.goods.Shop;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
    private RelativeLayout rlPackage;
    private WebView mWebView;
    private StickyScrollView mScrollView;
    private LinearLayout llDetail;
    private View addview;
    private TextView txtNum;
    private Button btnCollect;
    private boolean hasCollected=false;

    private PopupWindow popModels;

    private int color;
    private int selectColor;

    private Goods mGoods;
    private Shop mShop;

    private int topDetail;

    private GoodsWebService goodsWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLoadingDialog();
        goodsWebService= MyApplication.getInstance().getWebService(GoodsWebService.class);
        mGoods= (Goods) getIntent().getSerializableExtra(Goods.KEY);
        getGoodsDetail();
        initRotationMaps();
        color=getResources().getColor(R.color.lightblack);
        selectColor=getResources().getColor(R.color.colorPrimary);
    }

    /**
     * 选择型号
     */
    public void chooseModel(View v){
        popModels.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.5f);
    }

    /**
     * 收藏
     */
    public void collectGoods(View v){
        if(hasCollected){
            cancelGoodsCollect();
        }else{
            addGoodsCollect();
        }
    }

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
                        initGoodsUIWithValues();
                    }else{
                        ToastUtil.showShort(mContext,appResponse.getMessage());
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
            case R.id.img_close:
                popModels.dismiss();
                break;
            case R.id.img_add:
                updateSelectNum(true);
                break;
            case R.id.img_less:
                updateSelectNum(false);
                break;
            default:break;
        }
    }

    public void showShop(View v){
        Intent intent=new Intent(mContext,ShopGoodsActivity.class);
        intent.putExtra(Shop.KEY,mShop);
        startActivity(intent);
    }

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
            case R.id.txt_introduce:
                txtIntroduce.setTextColor(selectColor);
                mWebView.setVisibility(View.VISIBLE);
                imgSpecification.setVisibility(View.GONE);
                rlPackage.setVisibility(View.GONE);
                addview.setVisibility(View.GONE);
                break;
            case R.id.txt_spefi:
                txtSpecification.setTextColor(selectColor);
                mWebView.setVisibility(View.GONE);
                imgSpecification.setVisibility(View.VISIBLE);
                rlPackage.setVisibility(View.GONE);
                addview.setVisibility(View.VISIBLE);
                break;
            case R.id.txt_package:
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
            case R.id.txt_product:
                mScrollView.smoothScrollTo(0,0);
                txtProduct.setTextColor(selectColor);
                break;
            case R.id.txt_detail:
                mScrollView.smoothScrollTo(0,topDetail);
                txtDetail.setTextColor(selectColor);
                break;
            case R.id.txt_comment:
                txtComment.setTextColor(selectColor);
                break;
            default:break;
        }
    }

    public void onBackClick(View v){
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        topDetail = llDetail.getTop()+55;  //滑动需要的距离
    }

    private void initChooseModelView(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_choose_model, null);
        ImageView imgGoods= (ImageView) view.findViewById(R.id.img_goods);
        TextView txtPrice= (TextView) view.findViewById(R.id.txt_price);
        TextView txtStock= (TextView) view.findViewById(R.id.txt_stock);
        final TagFlowLayout modelTfl= (TagFlowLayout) view.findViewById(R.id.flowlayout);
        ImageView imgMore= (ImageView) view.findViewById(R.id.img_add);
        ImageView imgLess= (ImageView) view.findViewById(R.id.img_less);
        ImageView imgClose= (ImageView) view.findViewById(R.id.img_close);
        txtNum= (TextView) view.findViewById(R.id.txt_num);
        imgMore.setOnClickListener(this);
        imgLess.setOnClickListener(this);
        imgClose.setOnClickListener(this);
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+mGoods.getGoodsImages().get(0).getGoodsImagePath(),imgGoods, DisplayImageOptionsUtil.getOptions());
        txtPrice.setText("￥"+mGoods.getPreferentialPrice());
        txtStock.setText("库存"+mGoods.getStockNum()+"件");
        modelTfl.setMaxSelectCount(1);
        modelTfl.setAdapter(new TagAdapter<GoodsModel>(mGoods.getGoodsModels()) {
            @Override
            public View getView(FlowLayout parent, int position, GoodsModel o) {
                TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_model, modelTfl, false);
                tv.setText(o.getGoodsModelName());
                return tv;
            }
        });
        popModels = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popModels.setAnimationStyle(R.style.popupwindow_anim);
        popModels.setOutsideTouchable(true);
        popModels.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
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
    }

   
}
