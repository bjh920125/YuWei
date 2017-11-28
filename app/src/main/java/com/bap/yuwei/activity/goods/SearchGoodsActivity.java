package com.bap.yuwei.activity.goods;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.util.Base64;
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
import com.bap.yuwei.entity.event.CategoryEvent;
import com.bap.yuwei.entity.goods.Goods;
import com.bap.yuwei.entity.goods.Shop;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.SharedPreferencesUtil;
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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchGoodsActivity extends BaseActivity {

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
    protected ArrayList<String> categoryNames;

    protected int pageIndex = 1;
    private String querySort="";
    protected String queryTime;
    protected String q="";
    protected String cid="";
    protected boolean isPriceAsc=false;

    protected int color;
    protected int selectColor;

    public static final String KEYWORDS_KEY="keywords.key";

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
                    q= StringUtils.getEditTextValue(etSearch);
                    gvGoods.refresh();
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

        q=getIntent().getStringExtra(KEYWORDS_KEY);
        etSearch.setText(q);
        gvGoods.refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void searchByCategory(CategoryEvent event){
        cid=event.categoryNodes;
        gvGoods.refresh();
    }


    public void showCategory(View v){
        Intent i=new Intent(mContext,SearchGoodsCategoryActivity.class);
        i.putStringArrayListExtra(SearchGoodsCategoryActivity.CATEGORY_KEY,categoryNames);
        startActivity(i);
    }

    public void chooseSort(View view){
        txtMult.setTextColor(color);
        txtSell.setTextColor(color);
        txtTime.setTextColor(color);
        txtPrice.setTextColor(color);
        switch (view.getId()) {
            case R.id.txt_mult:
                querySort="";
                txtMult.setTextColor(selectColor);
                break;
            case R.id.txt_sell:
                querySort="sales:desc";
                txtSell.setTextColor(selectColor);
                break;
            case R.id.txt_time:
                querySort="popularity:desc";
                txtTime.setTextColor(selectColor);
                break;
            case R.id.txt_price:
                if(isPriceAsc){
                    isPriceAsc=false;
                    querySort="price:desc";
                    Drawable drawable= getResources().getDrawable(R.drawable.triangle_up);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    txtPrice.setCompoundDrawables(null,null,drawable,null);
                }else{
                    isPriceAsc=true;
                    querySort="price:asc";
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

    protected void getGoodsList(){
        Map<String,Object> params=new HashMap<>();
        params.put("brandId","");
        params.put("cid",cid);
        params.put("q",q);
        params.put("sort",querySort);
        params.put("page",pageIndex);
        params.put("size",12);
        params.put("ui", null!=mUser ? getXToken() : "");
        Call<ResponseBody> call=goodsWebService.goodssearch(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        JSONArray goodsStr=jo.getJSONArray("hitResults");
                        List<Goods> tempList = mGson.fromJson(goodsStr.toString(), new TypeToken<List<Goods>>() {}.getType());
                        if(tempList!=null && tempList.size()>0){
                            mGoods.addAll(tempList);
                            mGoodsAdapter.addAll(tempList);
                            mGoodsAdapter.notifyDataSetChanged();
                        }else{
                            gvGoods.setNoMore(true);
                        }
                        gvGoods.refreshComplete(tempList.size());
                        JSONArray categoryStr=jo.getJSONArray("categoryNames");
                        categoryNames = mGson.fromJson(URLDecoder.decode(categoryStr.toString(),"utf-8"), new TypeToken<List<String>>() {}.getType());
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

    @NonNull
    private String getXToken(){
        String str=SharedPreferencesUtil.getString(mContext,Constants.TOKEN_KEY)+":"+mUser.getUserId()+":1";
        return Base64.encodeToString(str.getBytes(), Base64.NO_WRAP).trim();
    }

    public void onBackClick(View v){
        super.onBackPressed();
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_goods;
    }

    @Override
    protected void initView() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE,true);
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
