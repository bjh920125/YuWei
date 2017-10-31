package com.bap.yuwei.activity.goods;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;

import com.bap.pla.PLAAdapterView;
import com.bap.pla.PLALoadMoreListView;
import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.commonadapter.CommonAdapter;
import com.bap.yuwei.adapter.commonadapter.ViewHolder;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.goods.Category;
import com.bap.yuwei.entity.goods.Goods;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.GoodsWebService;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryGoodsActivity extends BaseActivity  implements SwipeRefreshLayout.OnRefreshListener,PLALoadMoreListView.OnLoadMoreListener,View.OnClickListener {


    private SwipeRefreshLayout swipeRefresh;
    private PLALoadMoreListView mGvGoods;
    private TextView txtCategory1,txtCategory2,txtCategory3,txtCategory4;
    private TextView txtMult,txtSell,txtHot,txtPrice;

    private Category mCategory;
    private List<Goods> mGoods;
    private List<Category> mCategories;
    private CommonAdapter<Goods> mGoodsAdapter;
    private GoodsWebService goodsWebService;

    private String querySort="";
    private String queryCategory="";
    private int  pageIndex = 1;
    private boolean isPriceAsc=false;

    private int color;
    private int selectColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsWebService= MyApplication.getInstance().getWebService(GoodsWebService.class);
        mCategories=new ArrayList<>();
        mGoods=new ArrayList<>();
        mCategory= (Category) getIntent().getSerializableExtra(Category.KEY);
        queryCategory=mCategory.getCategoryId()+",";
        txtCategory1.setText(mCategory.getAlias());
        initGoodsGV();
        getGoods();
        color=getResources().getColor(R.color.lightblack);
        selectColor=getResources().getColor(R.color.colorPrimary);
    }

    @Override
    public void onRefresh() {
        mGoods.clear();
        mGoodsAdapter.notifyDataSetChanged();
        pageIndex=1;
        mGvGoods.setCanLoadMore(true);
        getGoods();
    }

    @Override
    public void onLoadMore() {
        pageIndex++;
        getGoods();
    }


    private void initGoodsGV(){
        mGoodsAdapter = new CommonAdapter<Goods>(mContext, mGoods, R.layout.item_goods) {
            @Override
            public void convert(ViewHolder viewHolder, Goods item) {
                viewHolder.setText(R.id.txt_title, item.getTitle());
                viewHolder.setText(R.id.txt_price, "￥"+item.getPreferentialPrice());
                viewHolder.setText(R.id.txt_comment_num, item.getTotalComment()+"条评价");
                viewHolder.setText(R.id.txt_comment_percent, item.getGoodCommentPercent()+"%好评");
                viewHolder.setImageByUrl(R.id.img_goods, Constants.PICTURE_URL+item.getMainPic());
            }
        };
        mGvGoods.setAdapter(mGoodsAdapter);

        mGvGoods.setOnItemClickListener(new PLAAdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLAAdapterView<?> parent, View view, int position, long id) {
                Goods goods= (Goods) mGvGoods.getItemAtPosition(position);
                ToastUtil.showShort(mContext,goods.getTitle());
            }
        });
    }

    private void getGoods(){
        Map<String,Object> params=new HashMap<>();
        params.put("cid",queryCategory);
        params.put("sort",querySort);
        params.put("page",pageIndex);
        params.put("size",12);
        Call<ResponseBody> call=goodsWebService.categorysearch(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    swipeRefresh.setRefreshing(false);
                    mGvGoods.onLoadMoreComplete();
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONArray jo=new JSONObject(result).getJSONObject("result").getJSONArray("hitResults");
                        List<Goods> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<Goods>>() {}.getType());
                        if(tempList!=null && tempList.size()>0){
                            mGoods.addAll(tempList);
                            mGoodsAdapter.notifyDataSetChanged();
                        }else {
                            mGvGoods.setCanLoadMore(false);
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
    public void onResume() {
        super.onResume();
    }

    public void chooseSort(View view){
        txtMult.setTextColor(color);
        txtSell.setTextColor(color);
        txtHot.setTextColor(color);
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
            case R.id.txt_hot:
                querySort="popularity:desc";
                txtHot.setTextColor(selectColor);
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

        onRefresh();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_category1:
                break;
            default:
                break;
        }
    }

    public void onBackClick(View view){
        finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_category_goods;
    }

    @Override
    protected void initView() {
        mGvGoods= (PLALoadMoreListView) findViewById(R.id.gv_goods);
        swipeRefresh= (SwipeRefreshLayout) findViewById(R.id.swipe);
        txtCategory1= (TextView) findViewById(R.id.txt_category1);
        txtCategory2= (TextView) findViewById(R.id.txt_category2);
        txtCategory3= (TextView) findViewById(R.id.txt_category3);
        txtCategory4= (TextView) findViewById(R.id.txt_category4);
        txtMult= (TextView) findViewById(R.id.txt_mult);
        txtSell= (TextView) findViewById(R.id.txt_sell);
        txtHot= (TextView) findViewById(R.id.txt_hot);
        txtPrice= (TextView) findViewById(R.id.txt_price);
        mGvGoods.setOnLoadMoreListener(this);
        swipeRefresh.setOnRefreshListener(this);
        txtCategory1.setOnClickListener(this);
    }
}
