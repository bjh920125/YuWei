package com.bap.yuwei.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.goods.Category;
import com.bap.yuwei.activity.goods.Goods;
import com.bap.yuwei.adapter.RotationMapAdapter;
import com.bap.yuwei.adapter.commonadapter.CommonAdapter;
import com.bap.yuwei.adapter.commonadapter.ViewHolder;
import com.bap.yuwei.entity.Banner;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.fragment.base.BaseFragment;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.view.NoScrollGridView;
import com.bap.yuwei.webservice.GoodsWebService;
import com.bap.yuwei.webservice.NewsWebService;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
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
 * Created by Administrator on 2017/10/27.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener{

    private Button btnScan,btnMsg;
    private ConvenientBanner convenientBanner;
    private NoScrollGridView gvCategories,gvGoods;

    private List<Banner> mBanners;
    private List<Category> mCategories;
    private List<Goods> mGoods;

    private CommonAdapter<Category> mCategoryAdapter;
    private CommonAdapter<Goods> mGoodsAdapter;

    private NewsWebService newsWebService;
    private GoodsWebService goodsWebService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newsWebService= MyApplication.getInstance().getWebService(NewsWebService.class);
        goodsWebService=MyApplication.getInstance().getWebService(GoodsWebService.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBanners=new ArrayList<>();
        mCategories=new ArrayList<>();
        mGoods=new ArrayList<>();
        initRotationMaps();
        getBanner();
        initCategoryGV();
        getCategories();
        initHotRecommendGV();
        getHotRecommend();
    }


    private void getBanner(){
        Call<ResponseBody> call=newsWebService.getBanner();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        mBanners.clear();
                        JSONArray jo=new JSONObject(result).getJSONArray("result");
                        List<Banner> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<Banner>>() {}.getType());
                        mBanners.addAll(tempList);
                        convenientBanner.notifyDataSetChanged();
                        convenientBanner.setcurrentitem(1);
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
     * 初始化轮播图
     */
    private void initRotationMaps(){
        convenientBanner.setPages(new CBViewHolderCreator<RotationMapAdapter>() {
            @Override
            public RotationMapAdapter createHolder() {
                return new RotationMapAdapter();
            }
        }, mBanners).setPageIndicator(new int[]{R.drawable.dot_blur, R.drawable.dot_focus})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        convenientBanner.setCanLoop(true);
        convenientBanner.setScrollDuration(800);
    }


    private void getCategories(){
        Call<ResponseBody> call=goodsWebService.getTopCategories();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        mCategories.clear();
                        JSONArray jo=new JSONObject(result).getJSONArray("result");
                        List<Category> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<Category>>() {}.getType());
                        mCategories.addAll(tempList);
                        mCategoryAdapter.notifyDataSetChanged();
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

    private void initCategoryGV(){
        mCategoryAdapter = new CommonAdapter<Category>(mContext, mCategories, R.layout.item_home_menu) {
            @Override
            public void convert(ViewHolder viewHolder, Category item) {
                viewHolder.setText(R.id.txt_name, item.getAlias());
                viewHolder.setImageByUrl(R.id.img_icon, Constants.PICTURE_URL+item.getAppIcon());
            }
        };
        gvCategories.setAdapter(mCategoryAdapter);
    }


    private void initHotRecommendGV(){
        mGoodsAdapter = new CommonAdapter<Goods>(mContext, mGoods, R.layout.item_goods) {
            @Override
            public void convert(ViewHolder viewHolder, Goods item) {
                viewHolder.setText(R.id.txt_title, item.getTitle());
                viewHolder.setText(R.id.txt_price, "￥"+item.getPreferentialPrice());
                viewHolder.setImageByUrl(R.id.img_goods, Constants.PICTURE_URL+item.getGoodsImage());
            }
        };
        gvGoods.setAdapter(mGoodsAdapter);
    }


    private void getHotRecommend(){
        Map<String,Object> params=new HashMap<>();
        params.put("pageNumber",1);
        params.put("pageSize",12);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=goodsWebService.getHotRecommend(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        mCategories.clear();
                        JSONArray jo=new JSONObject(result).getJSONArray("result");
                        List<Goods> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<Goods>>() {}.getType());
                        mGoods.addAll(tempList);
                        mGoodsAdapter.notifyDataSetChanged();
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

    @Override
    public void onClick(View view) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        btnScan= (Button) fragmentView.findViewById(R.id.btn_scan);
        btnMsg=(Button) fragmentView.findViewById(R.id.btn_msg);
        convenientBanner= (ConvenientBanner) fragmentView.findViewById(R.id.banner);
        gvCategories= (NoScrollGridView) fragmentView.findViewById(R.id.gv_menus);
        gvGoods= (NoScrollGridView) fragmentView.findViewById(R.id.gv_goods);
        return fragmentView;
    }


    @Override
    public void initView() {

    }

    // 开始自动翻页
    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
        convenientBanner.startTurning(5000);
    }

    // 停止自动翻页
    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
    }
}

