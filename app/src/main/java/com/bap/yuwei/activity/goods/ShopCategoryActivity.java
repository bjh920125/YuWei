package com.bap.yuwei.activity.goods;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.CategoryAdapter;
import com.bap.yuwei.entity.goods.Category;
import com.bap.yuwei.entity.goods.Shop;
import com.bap.yuwei.entity.goods.ShopCategory;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.GoodsWebService;
import com.linearlistview.LinearListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopCategoryActivity extends BaseActivity {

    private LinearListView lvCategory;

    private ShopCategory mShopCategory;
    private List<Category> mCategories;
    private CategoryAdapter mAdapter;
    private GoodsWebService goodsWebService;

    private Shop mShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsWebService= MyApplication.getInstance().getWebService(GoodsWebService.class);
        mShop= (Shop) getIntent().getSerializableExtra(Shop.KEY);
        mCategories=new ArrayList<>();
        mShopCategory=new ShopCategory();

        getShopCategory();
    }

    public void showAll(View v){
        Intent i=new Intent(mContext, ShopGoodsListActivity.class);
        i.putExtra(Shop.KEY,mShop);
        i.putExtra(ShopGoodsListActivity.CATEGORY_KEY,"");
        i.putExtra(ShopGoodsListActivity.KEYWORDS_KEY,"");
        startActivity(i);
    }

    private void getShopCategory(){
        Call<ResponseBody> call=goodsWebService.getShopCategory(mShop.getShopId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        mShopCategory=mGson.fromJson(jo.toString(),ShopCategory.class);
                        List<Category> tempList = mShopCategory.getCategories();
                        if(tempList!=null && tempList.size()>0){
                            mCategories.addAll(tempList);
                            mAdapter=new CategoryAdapter(mShop,mShopCategory,mCategories,mContext);
                            lvCategory.setAdapter(mAdapter);
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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shop_category;
    }

    @Override
    protected void initView() {
        lvCategory= (LinearListView) findViewById(R.id.lv_category);
    }
}
