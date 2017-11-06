package com.bap.yuwei.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bap.pla.PLAAdapterView;
import com.bap.pla.PLALoadMoreListView;
import com.bap.pla.PLALoadMoreListView.OnLoadMoreListener;
import com.bap.yuwei.R;
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
    private TextView btnMsg,txtMsgCount;
    private TextView txtCategory1,txtCategory2,txtCategory3,txtCategory4;

    private List<Goods> mGoods;
    private List<Category> mCategories;
    private CommonAdapter<Goods> mGoodsAdapter;
    private GoodsWebService goodsWebService;

    private int  pageIndex = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsWebService= MyApplication.getInstance().getWebService(GoodsWebService.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCategories=new ArrayList<>();
        mGoods=new ArrayList<>();
        initGoodsGV();
        getGoods();
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
                ToastUtil.showShort(mContext,goods.getTitle());
            }
        });
    }

    private void getGoods(){
        Map<String,Object> params=new HashMap<>();
        params.put("cid","10000,");
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_category1:
                break;
            case R.id.btn_msg:
                if(isLogined())
                    startActivity(new Intent(mContext, MsgMenusActivity.class));
                break;
            default:
                break;
        }
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
        mGvGoods.setOnLoadMoreListener(this);
        swipeRefresh.setOnRefreshListener(this);
        txtCategory1.setOnClickListener(this);
        btnMsg.setOnClickListener(this);
        return fragmentView;
    }


    @Override
    public void initView() {

    }
}
