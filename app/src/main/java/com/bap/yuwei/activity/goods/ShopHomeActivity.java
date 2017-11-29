package com.bap.yuwei.activity.goods;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.GoodsAdapter;
import com.bap.yuwei.adapter.ListBaseAdapter;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.goods.Goods;
import com.bap.yuwei.entity.goods.Shop;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.util.DateTimeUtil;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.SoftInputUtil;
import com.bap.yuwei.util.StringUtils;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.GoodsWebService;
import com.githang.statusbar.StatusBarCompat;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

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

/**
 * 店铺详情
 */
public class ShopHomeActivity extends BaseActivity {

    protected ImageView imgShop,imgHead;
    protected TextView txtShopName,txtGoodComment,txtCollectNum,txtGoodsTotal,txtGoodsNews;
    protected LRecyclerView gvGoods;
    protected Button btnHome;
    protected TextView txtAllTitle,txtAllNum,txtNewTitle,txtNewNum;
    protected View viewHome,viewAll,viewNew;
    protected LinearLayout llFilter;
    protected TextView txtMult,txtSell,txtTime,txtPrice;
    protected EditText etSearch;


    protected List<Goods> mGoods;
    protected Shop mShop;
    protected GoodsAdapter mGoodsAdapter;
    protected LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    protected GoodsWebService goodsWebService;

    protected int pageIndex = 1;
    protected int orderType=1;
    protected String queryTime;
    protected String goodsTitle;
    protected String categoryNodes;
    protected boolean isPriceAsc=false;

    protected boolean hasCollected=false;

    protected int color;
    protected int selectColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsWebService= MyApplication.getInstance().getWebService(GoodsWebService.class);
        mGoods=new ArrayList<>();
        mShop= (Shop) getIntent().getSerializableExtra(Shop.KEY);
        color=getResources().getColor(R.color.lightblack);
        selectColor=getResources().getColor(R.color.colorPrimary);
        mGoodsAdapter = new GoodsAdapter(mContext);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mGoodsAdapter);
        gvGoods.setAdapter(mLRecyclerViewAdapter);

        mGoodsAdapter.setOnItemClickListener(new ListBaseAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
                Goods goods= mGoods.get(position);
                Intent intent=new Intent(mContext, GoodsDetailActivity.class);
                intent.putExtra(Goods.KEY,goods);
                startActivity(intent);
            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    SoftInputUtil.hideKeyboard(mContext);
                    goodsTitle= StringUtils.getEditTextValue(etSearch);
                    Intent i=new Intent(mContext,ShopGoodsListActivity.class);
                    i.putExtra(ShopGoodsListActivity.KEYWORDS_KEY,goodsTitle);
                    i.putExtra(Shop.KEY,mShop);
                    startActivity(i);
                }
                return true;
            }
        });

        gvGoods.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mGoods.clear();
                mGoodsAdapter.clear();
                pageIndex=1;
                getGoodsList();
            }
        });

        gvGoods.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                pageIndex++;
                getGoodsList();
            }
        });
        getShopCollect();
        initUIWithValue();
    }


    /**
     * 店铺收藏、取消收藏
     */
    public void shopCollect(View v){
        if(hasCollected){
            cancelShopCollect();
        }else{
            addShopCollect();
        }
    }

    /**
     * 店铺首页、全部商品、新品 切换
     */
    public void onTabClicked(View v){
        viewHome.setVisibility(View.INVISIBLE);
        viewAll.setVisibility(View.INVISIBLE);
        viewNew.setVisibility(View.INVISIBLE);
        llFilter.setVisibility(View.GONE);
        btnHome.setTextColor(color);
        txtAllNum.setTextColor(color);
        txtAllTitle.setTextColor(color);
        txtNewNum.setTextColor(color);
        txtNewTitle.setTextColor(color);
        Drawable drawable= getResources().getDrawable(R.drawable.dianpu);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        btnHome.setCompoundDrawables(null,drawable,null,null);
        goodsTitle=null;
        switch (v.getId()){
            case R.id.btn_home://店铺首页
                viewHome.setVisibility(View.VISIBLE);
                Drawable drawablefill= getResources().getDrawable(R.drawable.dianpu_fill);
                drawablefill.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                btnHome.setCompoundDrawables(null,drawablefill,null,null);
                btnHome.setTextColor(selectColor);
                queryTime=null;
                break;
            case R.id.rl_all://全部商品
                viewAll.setVisibility(View.VISIBLE);
                txtAllNum.setTextColor(selectColor);
                txtAllTitle.setTextColor(selectColor);
                llFilter.setVisibility(View.VISIBLE);
                queryTime=null;
                break;
            case R.id.rl_new://新品
                viewNew.setVisibility(View.VISIBLE);
                txtNewNum.setTextColor(selectColor);
                txtNewTitle.setTextColor(selectColor);
                queryTime= DateTimeUtil.getNowTimeStr(DateTimeUtil.DATE_MONTH_FORMAT);
                break;
            default:break;
        }
        gvGoods.refresh();
    }

    /**
     * 选择排序方式
     */
    public void chooseSort(View view){
        txtMult.setTextColor(color);
        txtSell.setTextColor(color);
        txtTime.setTextColor(color);
        txtPrice.setTextColor(color);
        switch (view.getId()) {
            case R.id.txt_mult://综合排序
                orderType=1;
                txtMult.setTextColor(selectColor);
                break;
            case R.id.txt_sell://销量排序
                orderType=2;
                txtSell.setTextColor(selectColor);
                break;
            case R.id.txt_time://时间排序
                orderType=3;
                txtTime.setTextColor(selectColor);
                break;
            case R.id.txt_price:
                if(isPriceAsc){//价格递减
                    isPriceAsc=false;
                    orderType=4;
                    Drawable drawable= getResources().getDrawable(R.drawable.triangle_up);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    txtPrice.setCompoundDrawables(null,null,drawable,null);
                }else{//价格递增
                    isPriceAsc=true;
                    orderType=5;
                    Drawable drawable= getResources().getDrawable(R.drawable.triangle_down);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    txtPrice.setCompoundDrawables(null,null,drawable,null);
                }
                txtPrice.setTextColor(selectColor);
                break;
            default:
                break;
        }
        gvGoods.refresh();
    }

    /**
     * 获取商品列表
     */
    protected void getGoodsList(){
        Map<String,Object> params=new HashMap<>();
        params.put("categoryNodes",categoryNodes);
        params.put("discountStatus",0);
        params.put("goodsTitle",goodsTitle);
        params.put("orderType",orderType);//1:综合 2：销量 3：时间 4：价格从低到高 5：价格从高到低 6：好评量 7:人气
        params.put("queryTime",queryTime);
        params.put("shopId",mShop.getShopId());
        params.put("stockStatus",0);
        params.put("pageNumber",pageIndex);
        params.put("pageSize",12);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=goodsWebService.getShopGoods(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONArray jo=new JSONObject(result).getJSONObject("result").getJSONArray("list");
                        List<Goods> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<Goods>>() {}.getType());
                        if(tempList!=null && tempList.size()>0){
                            mGoods.addAll(tempList);
                            mGoodsAdapter.addAll(tempList);
                            mGoodsAdapter.notifyDataSetChanged();
                        }else{
                            gvGoods.setNoMore(true);
                        }
                        gvGoods.refreshComplete(tempList.size());
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
     * 获取收藏店铺详情
     */
    protected void getShopCollect(){
        if(null==mUser || null==mShop) return;
        Call<ResponseBody> call=goodsWebService.getShopCollect(mUser.getUserId(),mShop.getShopId());
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
                            txtCollectNum.setText("取消 "+mShop.getShopCollectUserTotal()+"人");
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
     * 收藏店铺
     */
    protected void addShopCollect(){
        if(null==mUser || null==mShop) return;
        Map<String,Object> params=new HashMap<>();
        params.put("shopId",mShop.getShopId());
        params.put("shopName", mShop.getShopName());
        params.put("userId",mUser.getUserId());
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=goodsWebService.addShopCollect(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        int num=new JSONObject(result).getInt("result");
                        txtCollectNum.setText("取消 "+num+"人");
                        hasCollected=true;
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
     * 取消收藏店铺
     */
    protected void cancelShopCollect(){
        if(null==mUser || null==mShop) return;
        Map<String,Object> params=new HashMap<>();
        params.put("shopId",mShop.getShopId());
        params.put("userId",mUser.getUserId());
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=goodsWebService.cancelShopCollect(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        int num=new JSONObject(result).getInt("result");
                        txtCollectNum.setText("收藏 "+num+"人");
                        hasCollected=false;
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

    public void onBackClick(View v){
        super.onBackPressed();
    }

    /**
     * 转跳商品类目界面
     */
    public void showCategory(View v){
        Intent i=new Intent(mContext,ShopCategoryActivity.class);
        i.putExtra(Shop.KEY,mShop);
        startActivity(i);
    }

    /**
     * 初始化UI
     */
    protected void initUIWithValue(){
        gvGoods.refresh();
        txtShopName.setText(mShop.getShopName());
        txtCollectNum.setText("收藏 "+mShop.getShopCollectUserTotal()+"人");
        txtGoodComment.setText("好评率"+mShop.getGoodCommentPercent()+"%");
        txtGoodsNews.setText(mShop.getRecentGoodsTotal()+"");
        txtGoodsTotal.setText(mShop.getGoodsTotal()+"");
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+mShop.getShopIcon(),imgShop, DisplayImageOptionsUtil.getOptions());
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+mShop.getHeadImage(),imgHead, DisplayImageOptionsUtil.getOptions());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shop_home;
    }

    @Override
    protected void initView() {
        StatusBarCompat.setStatusBarColor(this, Color.TRANSPARENT,false);
        imgShop= (ImageView) findViewById(R.id.img_shop);
        imgHead=(ImageView) findViewById(R.id.img_head);
        txtShopName= (TextView) findViewById(R.id.txt_shop_name);
        txtGoodComment= (TextView) findViewById(R.id.txt_comment_num);
        txtCollectNum= (TextView) findViewById(R.id.txt_shop_collect_num);
        txtGoodsTotal= (TextView) findViewById(R.id.txt_goods_total);
        txtGoodsNews= (TextView) findViewById(R.id.txt_goods_new_num);
        gvGoods= (LRecyclerView) findViewById(R.id.rv_goods);
        viewHome=findViewById(R.id.view_home);
        viewAll=findViewById(R.id.view_all);
        viewNew=findViewById(R.id.view_new);
        btnHome= (Button) findViewById(R.id.btn_home);
        txtAllTitle= (TextView) findViewById(R.id.txt_goods_total_title);
        txtAllNum= (TextView) findViewById(R.id.txt_goods_total);
        txtNewTitle= (TextView) findViewById(R.id.txt_goods_new_num_title);
        txtNewNum= (TextView) findViewById(R.id.txt_goods_new_num);
        llFilter=(LinearLayout)findViewById(R.id.ll_filter);
        txtMult= (TextView) findViewById(R.id.txt_mult);
        txtSell= (TextView) findViewById(R.id.txt_sell);
        txtTime= (TextView) findViewById(R.id.txt_time);
        txtPrice= (TextView) findViewById(R.id.txt_price);
        etSearch= (EditText) findViewById(R.id.et_words);
        gvGoods.setHasFixedSize(true);
        gvGoods.setLayoutManager(new GridLayoutManager(this,2));
        gvGoods.setHeaderViewColor(R.color.colorAccent, R.color.dark ,android.R.color.white);
        gvGoods.setFooterViewColor(R.color.colorAccent, R.color.dark ,android.R.color.white);
        gvGoods.setFooterViewHint("拼命加载中","已经全部为你呈现了","网络不给力啊，点击再试一次吧");
    }
}
