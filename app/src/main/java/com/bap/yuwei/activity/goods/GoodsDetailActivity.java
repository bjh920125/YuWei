package com.bap.yuwei.activity.goods;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.GoodsImageAdapter;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.goods.Goods;
import com.bap.yuwei.entity.goods.Shop;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.view.StickyScrollView;
import com.bap.yuwei.webservice.GoodsWebService;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoodsDetailActivity extends BaseActivity {

    private ConvenientBanner convenientBanner;
    private ImageView imgShop;
    private TextView txtGoodsTitle,txtPrice,txtOldPrice,txtExpressPrice,txtSellNum,txtModelName,txtSelectNum,txtShopName,txtShopDesc;
    private TextView txtShopCollectUserTotal,txtRecentGoodsTotal,txtGoodsTotal;
    private TextView txtProduct,txtDetail,txtComment;
    private WebView mWebView;
    private StickyScrollView mScrollView;
    private LinearLayout llDetail;

    private int color;
    private int selectColor;

    private Goods mGoods;
    private Shop mShop;

    private int topDetail;

    private GoodsWebService goodsWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsWebService= MyApplication.getInstance().getWebService(GoodsWebService.class);
        mGoods= (Goods) getIntent().getSerializableExtra(Goods.KEY);
        getGoodsDetail();
        initRotationMaps();
        color=getResources().getColor(R.color.lightblack);
        selectColor=getResources().getColor(R.color.colorPrimary);
    }


    private void getGoodsDetail(){
        Call<ResponseBody> call=goodsWebService.getGoodsDetail(mGoods.getGoodsId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        mGoods= mGson.fromJson(jo.toString(), Goods.class);
                        getShopDetail();
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

    private void initGoodsUIWithValues(){
        txtGoodsTitle.setText(mGoods.getTitle());
        txtPrice.setText("￥"+mGoods.getPreferentialPrice());
        txtOldPrice.setText("原价￥"+mGoods.getPrice()+"");
        txtExpressPrice.setText("快递："+mGoods.getFreight()+"元");
        txtModelName.setText(mGoods.getGoodsModelName());
        txtSellNum.setText("已卖出"+mGoods.getSellNum()+"件");
        initRotationMaps();
        mWebView.loadDataWithBaseURL(null, mGoods.getGoodsPhoneDesc(), "text/html", "GB2312", null);
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
        txtOldPrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG); //中划线
    }
}
