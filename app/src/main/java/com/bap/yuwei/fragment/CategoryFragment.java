package com.bap.yuwei.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bap.pla.PLAAdapterView;
import com.bap.pla.PLALoadMoreListView;
import com.bap.pla.PLALoadMoreListView.OnLoadMoreListener;
import com.bap.yuwei.R;
import com.bap.yuwei.activity.goods.GoodsDetailActivity;
import com.bap.yuwei.activity.goods.OverallSearchActivity;
import com.bap.yuwei.activity.sys.MsgMenusActivity;
import com.bap.yuwei.adapter.commonadapter.CommonAdapter;
import com.bap.yuwei.adapter.commonadapter.ViewHolder;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.event.UnreadEvent;
import com.bap.yuwei.entity.goods.Category;
import com.bap.yuwei.entity.goods.Goods;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.fragment.base.BaseFragment;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.GoodsWebService;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
 * Created by Administrator on 2017/10/27.
 */
public class CategoryFragment extends BaseFragment  implements OnRefreshListener,OnLoadMoreListener,View.OnClickListener {

    private SwipeRefreshLayout swipeRefresh;
    private PLALoadMoreListView mGvGoods;
    private TextView btnMsg,txtMsgCount,txtSearch;
    private TextView txtCategory1,txtCategory2,txtCategory3,txtCategory4;

    private PopupWindow popCategory;
    private View popCategoryView;
    private ListView lvParent;
    private ListView lvChildren;
    private CommonAdapter<Category> parentAdapter,childrenAdapter;

    private List<Goods> mGoods;
    private List<Category> mParent,mChildren;
    private List<Category> mCategories1,mCategories2,mCategories3,mCategories4;
    private CommonAdapter<Goods> mGoodsAdapter;
    private GoodsWebService goodsWebService;

    private String cid="";

    private int  pageIndex = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsWebService= MyApplication.getInstance().getWebService(GoodsWebService.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCategories1=new ArrayList<>();
        mCategories2=new ArrayList<>();
        mCategories3=new ArrayList<>();
        mCategories4=new ArrayList<>();
        mParent=new ArrayList<>();
        mChildren=new ArrayList<>();
        mGoods=new ArrayList<>();
        initPopCategoryView();
        initGoodsGV();
        getTopCategory();
        getUnreadMsgCount();

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
                Intent intent=new Intent(mContext, GoodsDetailActivity.class);
                intent.putExtra(Goods.KEY,goods);
                startActivity(intent);
            }
        });
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
                            Category category=mParent.get(0);
                            if(null!=category){
                                setHeadText(0,category);
                                cid=category.getCategoryId()+",";
                                getGoods();
                                getChildrenCategory(category.getCategoryId(),0,false);
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
                        if(tempList!=null && tempList.size()>0){
                            if(isShowNextLevel){
                                mParent.clear();
                                mParent.addAll(mChildren);
                                parentAdapter.notifyDataSetChanged();
                            }
                            mChildren.clear();
                            mChildren.addAll(tempList);
                            childrenAdapter.notifyDataSetChanged();

                            if(parentLevel==0){
                                mCategories2.clear();
                                mCategories2.addAll(tempList);
                            }else if(parentLevel==1){
                                mCategories3.clear();
                                mCategories3.addAll(tempList);
                            }else if(parentLevel==2){
                                mCategories4.clear();
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
                dismissProgressDialog();
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }

    private void getGoods(){
        Map<String,Object> params=new HashMap<>();
        params.put("cid",cid);
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

    private void initPopCategoryView() {
        if (null == popCategory) {
            popCategoryView = LayoutInflater.from(mContext).inflate(R.layout.view_category_filter, null);
            lvParent= (ListView) popCategoryView.findViewById(R.id.lv_parent);
            lvChildren= (ListView) popCategoryView.findViewById(R.id.lv_children);
        }
        parentAdapter=new CommonAdapter<Category>(mContext,mParent,R.layout.item_category_text_white_bg) {
            @Override
            public void convert(ViewHolder viewHolder, Category item) {
                viewHolder.setText(R.id.txt_category,item.getCategoryName());
            }
        };
        lvParent.setAdapter(parentAdapter);

        childrenAdapter=new CommonAdapter<Category>(mContext,mChildren,R.layout.item_category_text_white_bg) {
            @Override
            public void convert(ViewHolder viewHolder, Category item) {
                viewHolder.setText(R.id.txt_category,item.getCategoryName());
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
            }
        });

        lvChildren.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Category category= (Category) lvChildren.getItemAtPosition(i);
                int index=category.getLevel();
                getChildrenCategory(category.getCategoryId(),index,true);
                setHeadText(index,category);
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_category1:
                mParent.clear();
                mParent.addAll(mCategories1);
                parentAdapter.notifyDataSetChanged();
                mChildren.clear();
                mChildren.addAll(mCategories2);
                childrenAdapter.notifyDataSetChanged();
                break;
            case R.id.txt_category2:
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
            case R.id.txt_category3:
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
            case R.id.txt_category4:
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
        popCategory.showAsDropDown(mActivity.findViewById(R.id.txt_category1));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setUnReadMsgCount(UnreadEvent event){
        int unreadNum=event.unreadNum;
        if(unreadNum>0){
            txtMsgCount.setVisibility(View.VISIBLE);
            txtMsgCount.setText(unreadNum+"");
        }else{
            txtMsgCount.setVisibility(View.GONE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_category, container, false);
        mGvGoods= (PLALoadMoreListView) fragmentView.findViewById(R.id.gv_goods);
        swipeRefresh= (SwipeRefreshLayout) fragmentView.findViewById(R.id.swipe);
        txtCategory1= (TextView) fragmentView.findViewById(R.id.txt_category1);
        txtCategory2= (TextView) fragmentView.findViewById(R.id.txt_category2);
        txtCategory3= (TextView) fragmentView.findViewById(R.id.txt_category3);
        txtCategory4= (TextView) fragmentView.findViewById(R.id.txt_category4);
        btnMsg=(TextView) fragmentView.findViewById(R.id.btn_msg);
        txtMsgCount=(TextView) fragmentView.findViewById(R.id.txt_msg_count);
        txtSearch=(TextView) fragmentView.findViewById(R.id.txt_search);
        mGvGoods.setOnLoadMoreListener(this);
        swipeRefresh.setOnRefreshListener(this);
        txtCategory1.setOnClickListener(this);
        txtCategory2.setOnClickListener(this);
        txtCategory3.setOnClickListener(this);
        txtCategory4.setOnClickListener(this);
        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, OverallSearchActivity.class));
            }
        });
        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLogined())
                    startActivity(new Intent(mContext, MsgMenusActivity.class));
            }
        });
        return fragmentView;
    }


    @Override
    public void initView() {

    }
}
