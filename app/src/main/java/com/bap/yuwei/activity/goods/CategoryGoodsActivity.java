package com.bap.yuwei.activity.goods;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.bap.yuwei.util.SoftInputUtil;
import com.bap.yuwei.util.StringUtils;
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

/**
 * 根据类目展示商品列表
 */
public class CategoryGoodsActivity extends BaseActivity  implements SwipeRefreshLayout.OnRefreshListener,PLALoadMoreListView.OnLoadMoreListener,View.OnClickListener {
    private SwipeRefreshLayout swipeRefresh;
    private PLALoadMoreListView mGvGoods;
    private TextView txtCategory1,txtCategory2,txtCategory3,txtCategory4;
    private TextView txtMult,txtSell,txtHot,txtPrice;
    private EditText etSearch;

    private Category mCategory;
    private List<Goods> mGoods;
    private List<Category> mCategories;
    private CommonAdapter<Goods> mGoodsAdapter;
    private GoodsWebService goodsWebService;

    private PopupWindow popCategory;
    private View popCategoryView;
    private ListView lvParent;
    private ListView lvChildren;
    private CommonAdapter<Category> parentAdapter,childrenAdapter;
    private List<Category> mParent,mChildren;
    private List<Category> mCategories1,mCategories2,mCategories3,mCategories4;
    private Map<Integer,Long> mSelectedCategories;

    private String querySort="";
    private int  pageIndex = 1;
    private boolean isPriceAsc=false;
    private String cid="";
    private String keywords="";

    private int color;
    private int selectColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsWebService= MyApplication.getInstance().getWebService(GoodsWebService.class);
        mCategories=new ArrayList<>();
        mCategories1=new ArrayList<>();
        mCategories2=new ArrayList<>();
        mCategories3=new ArrayList<>();
        mCategories4=new ArrayList<>();
        mParent=new ArrayList<>();
        mChildren=new ArrayList<>();
        mGoods=new ArrayList<>();
        mSelectedCategories=new HashMap<>();
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    SoftInputUtil.hideKeyboard(mContext);
                    keywords= StringUtils.getEditTextValue(etSearch);
                    onRefresh();
                }
                return true;
            }
        });

        mCategory= (Category) getIntent().getSerializableExtra(Category.KEY);
        cid=mCategory.getCategoryId()+",";
        txtCategory1.setText(mCategory.getAlias());
        swipeRefresh.setRefreshing(true);
        initPopCategoryView();
        initGoodsGV();
        getGoods();
        getTopCategory();
        color=getResources().getColor(R.color.lightblack);
        selectColor=getResources().getColor(R.color.colorPrimary);
    }

    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(true);
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
                Intent intent=new Intent(mContext, GoodsDetailActivity.class);
                intent.putExtra(Goods.KEY,goods);
                startActivity(intent);
            }
        });
    }

    /**
     * 获取商品列表
     */
    private void getGoods(){
        Map<String,Object> params=new HashMap<>();
        params.put("cid",cid);
        params.put("sort",querySort);
        params.put("sq",keywords);
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
                swipeRefresh.setRefreshing(false);
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 选择排序方式
     */
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

    /**
     * 获取顶级分类
     */
    private void getTopCategory(){
        Call<ResponseBody> call=goodsWebService.getTopCategories();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONArray jo=new JSONObject(result).getJSONArray("result");
                        List<Category> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<Category>>() {}.getType());
                        if(tempList!=null && tempList.size()>0){
                            mCategories1.addAll(tempList);
                            mParent.clear();
                            mParent.addAll(tempList);
                            parentAdapter.notifyDataSetChanged();
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
     * 获取子分类
     */
    private void getChildrenCategory(Long parentId, final int parentLevel, final boolean isShowNextLevel){
        showLoadingDialog();
        Call<ResponseBody> call=goodsWebService.getChildrenCategories(parentId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONArray jo=new JSONObject(result).getJSONArray("result");
                        List<Category> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<Category>>() {}.getType());
                        mChildren.clear();
                        mChildren.addAll(tempList);
                        childrenAdapter.notifyDataSetChanged();
                        if(parentLevel==0){
                            mCategories2.clear();
                        }else if(parentLevel==1){
                            mCategories3.clear();
                        }else if(parentLevel==2){
                            mCategories4.clear();
                        }
                        if(tempList!=null && tempList.size()>0){
                            if(isShowNextLevel){
                                mParent.clear();
                                mParent.addAll(mChildren);
                                parentAdapter.notifyDataSetChanged();
                            }

                            if(parentLevel==0){
                                mCategories2.addAll(tempList);
                            }else if(parentLevel==1){
                                mCategories3.addAll(tempList);
                            }else if(parentLevel==2){
                                mCategories4.addAll(tempList);
                            }
                        }else{
                            if(popCategory.isShowing()){
                                popCategory.dismiss();
                            }
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
                dismissProgressDialog();
            }
        });
    }

    /**
     * 初始化类目选择view
     */
    private void initPopCategoryView() {
        if (null == popCategory) {
            popCategoryView = LayoutInflater.from(mContext).inflate(R.layout.view_category_filter, null);
            lvParent= (ListView) popCategoryView.findViewById(R.id.lv_parent);
            lvChildren= (ListView) popCategoryView.findViewById(R.id.lv_children);
        }
        parentAdapter=new CommonAdapter<Category>(mContext,mParent,R.layout.item_category_text_white_bg) {
            @Override
            public void convert(ViewHolder viewHolder, Category item) {
                if(item.isChecked()){
                    viewHolder.setTextWithColor(R.id.txt_category,item.getCategoryName(),selectColor);
                }else {
                    viewHolder.setTextWithColor(R.id.txt_category,item.getCategoryName(),color);
                }
            }
        };
        lvParent.setAdapter(parentAdapter);

        childrenAdapter=new CommonAdapter<Category>(mContext,mChildren,R.layout.item_category_text_white_bg) {
            @Override
            public void convert(ViewHolder viewHolder, Category item) {
                if(item.isChecked()){
                    viewHolder.setTextWithColor(R.id.txt_category,item.getCategoryName(),selectColor);
                }else {
                    viewHolder.setTextWithColor(R.id.txt_category,item.getCategoryName(),color);
                }
            }
        };
        lvChildren.setAdapter(childrenAdapter);

        lvParent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Category category= (Category) lvParent.getItemAtPosition(i);
                int index=category.getLevel();
                getChildrenCategory(category.getCategoryId(),index,false);
                setHeadText(index,category);
                mSelectedCategories.put(index,category.getCategoryId());
                for(Category c:mParent){
                    c.setChecked(false);
                }
                category.setChecked(true);
                parentAdapter.notifyDataSetChanged();
            }
        });

        lvChildren.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Category category= (Category) lvChildren.getItemAtPosition(i);
                int index=category.getLevel();
                getChildrenCategory(category.getCategoryId(),index,true);
                setHeadText(index,category);
                mSelectedCategories.put(index,category.getCategoryId());
                for(Category c:mChildren){
                    c.setChecked(false);
                }
                category.setChecked(true);
                childrenAdapter.notifyDataSetChanged();
            }
        });

        popCategory = new PopupWindow(popCategoryView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //popCategory.setAnimationStyle(R.style.popupwindow_anim_up_down);
        popCategory.setOutsideTouchable(true);
        popCategory.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                cid="";
                TextView[] txt=new  TextView[]{txtCategory1,txtCategory2,txtCategory3,txtCategory4};
                for(TextView t:txt){
                    Category category= (Category) t.getTag();
                    if(null!=category){
                        cid=cid+category.getCategoryId()+",";
                    }
                }
                onRefresh();
            }
        });
    }

    /**
     * 显示已选的类目
     */
    private void setHeadText(int index,Category category){
        if(index==0){
            txtCategory1.setText(category.getCategoryName());
            txtCategory1.setTag(category);
            txtCategory2.setTag(null);
            txtCategory3.setTag(null);
            txtCategory4.setTag(null);
            txtCategory2.setVisibility(View.GONE);
            txtCategory3.setVisibility(View.GONE);
            txtCategory4.setVisibility(View.GONE);
        }else if(index==1){
            txtCategory2.setText(category.getCategoryName());
            txtCategory2.setTag(category);
            txtCategory3.setTag(null);
            txtCategory4.setTag(null);
            txtCategory2.setVisibility(View.VISIBLE);
            txtCategory3.setVisibility(View.GONE);
            txtCategory4.setVisibility(View.GONE);
        } else if(index==2){
            txtCategory3.setText(category.getCategoryName());
            txtCategory4.setTag(null);
            txtCategory3.setTag(category);
            txtCategory3.setVisibility(View.VISIBLE);
            txtCategory4.setVisibility(View.GONE);
        }else if(index==3){
            txtCategory4.setText(category.getCategoryName());
            txtCategory4.setTag(category);
            txtCategory4.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_category1://点击一级类目
                mParent.clear();
                mParent.addAll(mCategories1);
                parentAdapter.notifyDataSetChanged();
                mChildren.clear();
                mChildren.addAll(mCategories2);
                childrenAdapter.notifyDataSetChanged();
                break;
            case R.id.txt_category2://点击二级类目
                if(mCategories3.size()>0){
                    mParent.clear();
                    mParent.addAll(mCategories2);
                    parentAdapter.notifyDataSetChanged();
                    mChildren.clear();
                    mChildren.addAll(mCategories3);
                    childrenAdapter.notifyDataSetChanged();
                }else{
                    onClick(txtCategory1);
                }
                break;
            case R.id.txt_category3://点击三级类目
                if(mCategories4.size()>0){
                    mParent.clear();
                    mParent.addAll(mCategories3);
                    parentAdapter.notifyDataSetChanged();
                    mChildren.clear();
                    mChildren.addAll(mCategories4);
                    childrenAdapter.notifyDataSetChanged();
                }else{
                    onClick(txtCategory2);
                }
                break;
            case R.id.txt_category4://点击四级类目
                mParent.clear();
                mParent.addAll(mCategories3);
                parentAdapter.notifyDataSetChanged();
                mChildren.clear();
                mChildren.addAll(mCategories4);
                childrenAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        chooseCategory();
    }

    public void chooseCategory(){
        popCategory.showAsDropDown(findViewById(R.id.txt_category1));
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
        etSearch= (EditText) findViewById(R.id.et_search);
        mGvGoods.setOnLoadMoreListener(this);
        swipeRefresh.setOnRefreshListener(this);
        txtCategory1.setOnClickListener(this);
        txtCategory2.setOnClickListener(this);
        txtCategory3.setOnClickListener(this);
        txtCategory4.setOnClickListener(this);
    }
}
