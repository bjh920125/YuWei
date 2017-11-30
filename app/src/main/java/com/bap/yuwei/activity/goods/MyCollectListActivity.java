package com.bap.yuwei.activity.goods;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.GoodsCollectAdapter;
import com.bap.yuwei.adapter.ListBaseAdapter;
import com.bap.yuwei.adapter.ShopCollectAdapter;
import com.bap.yuwei.entity.goods.Goods;
import com.bap.yuwei.entity.goods.GoodsCollect;
import com.bap.yuwei.entity.goods.Shop;
import com.bap.yuwei.entity.goods.ShopCollect;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.GoodsWebService;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.google.gson.reflect.TypeToken;

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
 * 我的收藏列表
 */
public class MyCollectListActivity extends BaseActivity {

    private LRecyclerView rvCollect;
    private RadioGroup rgTypes;
    private RadioButton rbGoods,rbShop;


    private List<GoodsCollect> goodsCollects;
    private List<ShopCollect> shopCollects;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private GoodsCollectAdapter goodsCollectAdapter;
    private ShopCollectAdapter shopCollectAdapter;

    private int pageIndex = 1;
    private int pageSize=12;


    public static final String SHOW_TYPE_INDEX="show.type.index";
    private int showType=0;//0商品收藏   1店铺收藏

    private GoodsWebService goodsWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsWebService= MyApplication.getInstance().getWebService(GoodsWebService.class);
        goodsCollects=new ArrayList<>();
        shopCollects=new ArrayList<>();
        showType=getIntent().getIntExtra(SHOW_TYPE_INDEX,0);
        RadioButton rb= (RadioButton) rgTypes.getChildAt(showType);
        rb.setChecked(true);
        rgTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.rb_goods){
                    showType=0;
                }else {
                    showType=1;
                }
                rvCollect.refresh();
            }
        });
        goodsCollectAdapter = new GoodsCollectAdapter(mContext,mUser.getUserId());
        shopCollectAdapter = new ShopCollectAdapter(mContext);

        goodsCollectAdapter.setOnItemClickListener(new ListBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GoodsCollect goodsCollect=goodsCollects.get(position);
                Intent i=new Intent(mContext,GoodsDetailActivity.class);
                Goods goods=new Goods();
                goods.setGoodsId(goodsCollect.getGoodsId());
                i.putExtra(Goods.KEY,goods);
                startActivity(i);
            }
        });

        goodsCollectAdapter.setOnItemLongClickListener(new ListBaseAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                comfirmCancelGoodsCollect(goodsCollects.get(position),position);
            }
        });

        shopCollectAdapter.setOnItemClickListener(new ListBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ShopCollect shopCollect=shopCollects.get(position);
                getShopDetail(shopCollect.getShopId());
            }
        });

        shopCollectAdapter.setOnItemLongClickListener(new ListBaseAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                comfirmCancelShopCollect(shopCollects.get(position),position);
            }
        });

        rvCollect.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                goodsCollects.clear();
                goodsCollectAdapter.clear();
                shopCollects.clear();
                shopCollectAdapter.clear();
                pageIndex=1;
                getListByType();
            }
        });

        rvCollect.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                pageIndex++;
                getListByType();
            }
        });

        rvCollect.refresh();
    }

    /**
     * 根据类型获取列表
     */
    private void getListByType(){
        if(showType==0){
            mLRecyclerViewAdapter = new LRecyclerViewAdapter(goodsCollectAdapter);
            rvCollect.setAdapter(mLRecyclerViewAdapter);
            getGoodsCollect();
        }else{
            mLRecyclerViewAdapter = new LRecyclerViewAdapter(shopCollectAdapter);
            rvCollect.setAdapter(mLRecyclerViewAdapter);
            getShopCollect();
        }
    }


    /**
     * 获取商品收藏列表
     */
    private void getGoodsCollect(){
        Map<String,Object> params=new HashMap<>();
        params.put("userId",mUser.getUserId());
        params.put("pageNumber",pageIndex);
        params.put("pageSize",pageSize);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=goodsWebService.getGoodsCollect(mUser.getUserId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONArray jo=new JSONObject(result).getJSONObject("result").getJSONArray("list");
                        List<GoodsCollect> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<GoodsCollect>>() {}.getType());
                        if(tempList!=null && tempList.size()>0){
                            goodsCollects.addAll(tempList);
                            goodsCollectAdapter.addAll(tempList);
                            goodsCollectAdapter.notifyDataSetChanged();
                        }else{
                            rvCollect.setNoMore(true);
                        }
                        rvCollect.refreshComplete(tempList.size());
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
     * 确认删除商品收藏
     */
    private void comfirmCancelGoodsCollect(final GoodsCollect goodsCollect,final int position){
            new AlertDialog.Builder(mContext)
                    .setTitle("操作提醒")
                    .setMessage("您确定取消收藏吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cancelGoodsCollect(goodsCollect,position);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
    }

    /**
     * 确认删除店铺收藏
     */
    private void comfirmCancelShopCollect(final ShopCollect shopCollect,final int position){
        new AlertDialog.Builder(mContext)
                .setTitle("操作提醒")
                .setMessage("您确定取消收藏吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancelShopCollect(shopCollect,position);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }


    /**
     * 取消收藏商品
     */
    private void cancelGoodsCollect(GoodsCollect goodsCollect, final int position){
        Map<String,Object> params=new HashMap<>();
        params.put("goodsId", goodsCollect.getGoodsId());
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
                        goodsCollectAdapter.remove(position);
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
     * 获取店铺收藏列表
     */
    private void getShopCollect(){
        Map<String,Object> params=new HashMap<>();
        params.put("userId",mUser.getUserId());
        params.put("pageNumber",pageIndex);
        params.put("pageSize",pageSize);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=goodsWebService.getShopCollect(mUser.getUserId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONArray jo=new JSONObject(result).getJSONObject("result").getJSONArray("list");
                        List<ShopCollect> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<ShopCollect>>() {}.getType());
                        if(tempList!=null && tempList.size()>0){
                            shopCollects.addAll(tempList);
                            shopCollectAdapter.addAll(tempList);
                            shopCollectAdapter.notifyDataSetChanged();
                        }else{
                            rvCollect.setNoMore(true);
                        }
                        rvCollect.refreshComplete(tempList.size());
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
    protected void cancelShopCollect(final ShopCollect shopCollect,final int position){
        Map<String,Object> params=new HashMap<>();
        params.put("shopId",shopCollect.getShopId());
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
                        shopCollectAdapter.remove(position);
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
     *获取店铺详情
     */
    private void getShopDetail(Long shopId){
        showLoadingDialog();
        Call<ResponseBody> call=goodsWebService.getShopDetail(shopId);
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
                        Shop shop= mGson.fromJson(jo.toString(), Shop.class);
                        Intent i=new Intent(mContext,ShopHomeActivity.class);
                        i.putExtra(Shop.KEY,shop);
                        startActivity(i);
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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_collect_list;
    }

    @Override
    protected void initView() {
        rgTypes= (RadioGroup) findViewById(R.id.rg_type);
        rbGoods= (RadioButton) findViewById(R.id.rb_goods);
        rbShop= (RadioButton) findViewById(R.id.rb_shop);
        rvCollect= (LRecyclerView) findViewById(R.id.rv_collect);
        rvCollect.setHasFixedSize(true);
        rvCollect.setLayoutManager(new LinearLayoutManager(mContext));
        rvCollect.setHeaderViewColor(R.color.colorAccent, R.color.dark ,android.R.color.white);
        rvCollect.setFooterViewColor(R.color.colorAccent, R.color.dark ,android.R.color.white);
        rvCollect.setFooterViewHint("拼命加载中","已经全部为你呈现了","网络不给力啊，点击再试一次吧");

    }
}
