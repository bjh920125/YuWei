package com.bap.yuwei.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.ScannerQRCodeActivity;
import com.bap.yuwei.activity.goods.CategoryGoodsActivity;
import com.bap.yuwei.activity.goods.GoodsDetailActivity;
import com.bap.yuwei.activity.goods.OverallSearchActivity;
import com.bap.yuwei.activity.news.NewsDetailActivity;
import com.bap.yuwei.activity.news.NewsListActivity;
import com.bap.yuwei.activity.sys.MsgMenusActivity;
import com.bap.yuwei.adapter.RotationMapAdapter;
import com.bap.yuwei.adapter.commonadapter.CommonAdapter;
import com.bap.yuwei.adapter.commonadapter.ViewHolder;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.event.UnreadEvent;
import com.bap.yuwei.entity.goods.Category;
import com.bap.yuwei.entity.goods.Goods;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.news.Banner;
import com.bap.yuwei.entity.news.News;
import com.bap.yuwei.fragment.base.BaseFragment;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.view.NoScrollGridView;
import com.bap.yuwei.view.UPMarqueeView;
import com.bap.yuwei.webservice.GoodsWebService;
import com.bap.yuwei.webservice.NewsWebService;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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

public class HomeFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{

    private Button btnScan;
    private TextView txtNews,txtSearch,btnMsg,txtMsgCount;
    private ConvenientBanner convenientBanner;
    private NoScrollGridView gvCategories,gvGoods;
    private UPMarqueeView upviewNews;
    private SwipeRefreshLayout swipeRefresh;

    private List<Banner> mBanners;
    private List<Category> mCategories;
    private List<Goods> mGoods;
    private List<News> mNews;
    private List<View> upviews;

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
        mNews=new ArrayList<>();
        upviews = new ArrayList<>();
        initRotationMaps();
        initCategoryGV();
        initHotRecommendGV();
        swipeRefresh.setRefreshing(true);
        onRefresh();
        getUnreadMsgCount();
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
                swipeRefresh.setRefreshing(false);
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

        gvCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Category category= (Category) gvCategories.getItemAtPosition(position);
                Intent intent=new Intent(mContext, CategoryGoodsActivity.class);
                intent.putExtra(Category.KEY,category);
                startActivity(intent);
            }
        });
    }


    private void initHotRecommendGV(){
        mGoodsAdapter = new CommonAdapter<Goods>(mContext, mGoods, R.layout.item_goods) {
            @Override
            public void convert(ViewHolder viewHolder, Goods item) {
                viewHolder.setText(R.id.txt_title, item.getTitle());
                viewHolder.setText(R.id.txt_price, "￥"+item.getPreferentialPrice());
                viewHolder.setText(R.id.txt_comment_num, item.getTotalComment()+"条评价");
                viewHolder.setText(R.id.txt_comment_percent, item.getGoodCommentPercent()+"%好评");
                viewHolder.setImageByUrl(R.id.img_goods, Constants.PICTURE_URL+item.getGoodsImage());
            }
        };
        gvGoods.setAdapter(mGoodsAdapter);

        gvGoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Goods goods= (Goods) gvGoods.getItemAtPosition(i);
                Intent intent=new Intent(mContext, GoodsDetailActivity.class);
                intent.putExtra(Goods.KEY,goods);
                startActivity(intent);
            }
        });
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
                swipeRefresh.setRefreshing(false);
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        mGoods.clear();
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

    private void getNews(){
        Map<String,Object> params=new HashMap<>();
        params.put("pageNumber",1);
        params.put("pageSize",12);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=newsWebService.getNews(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        mNews.clear();
                        JSONArray jo=new JSONObject(result).getJSONObject("result").getJSONArray("list");
                        List<News> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<News>>() {}.getType());
                        mNews.addAll(tempList);
                        initUpView();
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

    /**
     * 初始化界面程序
     */
    private void initUpView() {
        setUpView();
        upviewNews.setViews(upviews);
        /**
         * 设置item_view的监听
         */
        upviewNews.setOnItemClickListener(new UPMarqueeView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent=new Intent(mContext,NewsDetailActivity.class);
                intent.putExtra(News.KEY,mNews.get(position));
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化需要循环的View
     * 为了灵活的使用滚动的View，所以把滚动的内容让用户自定义
     * 假如滚动的是三条或者一条，或者是其他，只需要把对应的布局，和这个方法稍微改改就可以了，
     */
    private void setUpView() {
        for (int i = 0; i < mNews.size(); i = i + 2) {
            final int position = i;
            //设置滚动的单个布局
            LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_upmarquee_news, null);
            //初始化布局的控件
            TextView tv1 = (TextView) moreView.findViewById(R.id.txt_title);
            TextView tv2 = (TextView) moreView.findViewById(R.id.txt_title2);
            //进行对控件赋值
            tv1.setText(mNews.get(i).getTitle());
            if (mNews.size() > i + 1) {
                //因为淘宝那儿是两条数据，但是当数据是奇数时就不需要赋值第二个，所以加了一个判断，还应该把第二个布局给隐藏掉
                tv2.setText(mNews.get(i + 1).getTitle());
            } else {
                moreView.findViewById(R.id.rl2).setVisibility(View.GONE);
            }
            //添加到循环滚动数组里面去
            upviews.add(moreView);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_news:
                startActivity(new Intent(mContext, NewsListActivity.class));
                break;
            case R.id.btn_scan:
                if(isLogined())
                startActivity(new Intent(mContext, ScannerQRCodeActivity.class));
                break;
            case R.id.btn_msg:
                if(isLogined())
                    startActivity(new Intent(mContext, MsgMenusActivity.class));
                break;
            case R.id.txt_search:
                startActivity(new Intent(mContext, OverallSearchActivity.class));
                break;
            default:break;
        }
    }

    @Override
    public void onRefresh() {
        getBanner();
        getCategories();
        getHotRecommend();
        getNews();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        btnScan= (Button) fragmentView.findViewById(R.id.btn_scan);
        btnMsg=(TextView) fragmentView.findViewById(R.id.btn_msg);
        txtMsgCount=(TextView) fragmentView.findViewById(R.id.txt_msg_count);
        convenientBanner= (ConvenientBanner) fragmentView.findViewById(R.id.banner);
        gvCategories= (NoScrollGridView) fragmentView.findViewById(R.id.gv_menus);
        gvGoods= (NoScrollGridView) fragmentView.findViewById(R.id.gv_goods);
        upviewNews= (UPMarqueeView) fragmentView.findViewById(R.id.upview);
        txtNews= (TextView) fragmentView.findViewById(R.id.txt_news);
        txtSearch=(TextView) fragmentView.findViewById(R.id.txt_search);
        swipeRefresh= (SwipeRefreshLayout) fragmentView.findViewById(R.id.swipe);
        btnScan.setOnClickListener(this);
        btnMsg.setOnClickListener(this);
        txtNews.setOnClickListener(this);
        txtSearch.setOnClickListener(this);
        swipeRefresh.setOnRefreshListener(this);
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

