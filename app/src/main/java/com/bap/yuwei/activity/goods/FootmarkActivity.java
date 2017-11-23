package com.bap.yuwei.activity.goods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.FootmarkAdapter;
import com.bap.yuwei.adapter.ListBaseAdapter;
import com.bap.yuwei.entity.goods.Footmark;
import com.bap.yuwei.entity.goods.Goods;
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

public class FootmarkActivity extends BaseActivity {

    private LRecyclerView rvFootmark;
    private RelativeLayout rlDelete;

    private List<Footmark> footmarks;
    private FootmarkAdapter adapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private GoodsWebService goodsWebService;

    private int pageIndex = 1;
    private int pageSize = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTxtRightMenu.setText("编辑");
        goodsWebService = MyApplication.getInstance().getWebService(GoodsWebService.class);
        footmarks = new ArrayList<>();
        adapter = new FootmarkAdapter(mContext);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        rvFootmark.setAdapter(mLRecyclerViewAdapter);

        adapter.setOnItemClickListener(new ListBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Footmark footmark=footmarks.get(position);
                Intent i=new Intent(mContext,GoodsDetailActivity.class);
                Goods goods=new Goods();
                goods.setGoodsId(footmark.getGoodsId());
                i.putExtra(Goods.KEY,goods);
                startActivity(i);
            }
        });

        rvFootmark.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                footmarks.clear();
                adapter.clear();
                pageIndex = 1;
                getFootmark();
            }
        });

        rvFootmark.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                pageIndex++;
                getFootmark();
            }
        });
        rvFootmark.refresh();
    }

    private List<Long> getNeedDeleteIds(){
        List<Long> ids=new ArrayList<>();
        for(Footmark footmark:footmarks){
            if(footmark.isChecked()){
                ids.add(footmark.getHistoryId());
            }
        }
        return ids;
    }

    public void delete(View v){
        showLoadingDialog();
        Map<String, Object> params = new HashMap<>();
        params.put("historyIds", getNeedDeleteIds());
        RequestBody body = RequestBody.create(jsonMediaType, mGson.toJson(params));
        Call<ResponseBody> call = goodsWebService.deleteFootmark(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                try {
                    String result = response.body().string();
                    LogUtil.print("result", result);
                    AppResponse appResponse = mGson.fromJson(result, AppResponse.class);
                    if (appResponse.getCode() == ResponseCode.SUCCESS) {
                        rvFootmark.refresh();
                    } else {
                        ToastUtil.showShort(mContext, appResponse.getMessage());
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

    private void getFootmark() {
        Map<String, Object> params = new HashMap<>();
        params.put("pageNumber", pageIndex);
        params.put("pageSize", pageSize);
        params.put("userId", mUser.getUserId());
        RequestBody body = RequestBody.create(jsonMediaType, mGson.toJson(params));
        Call<ResponseBody> call = goodsWebService.getFootmark(mUser.getUserId(), body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    LogUtil.print("result", result);
                    AppResponse appResponse = mGson.fromJson(result, AppResponse.class);
                    if (appResponse.getCode() == ResponseCode.SUCCESS) {
                        JSONArray jo = new JSONObject(result).getJSONObject("result").getJSONArray("list");
                        List<Footmark> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<Footmark>>() {
                        }.getType());
                        if (tempList != null && tempList.size() > 0) {
                            footmarks.addAll(tempList);
                            adapter.addAll(tempList);
                            adapter.notifyDataSetChanged();
                        } else {
                            rvFootmark.setNoMore(true);
                        }
                        rvFootmark.refreshComplete(tempList.size());
                    } else {
                        ToastUtil.showShort(mContext, appResponse.getMessage());
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
    public void onRightBtnClick(View v) {
        String txt = mTxtRightMenu.getText().toString();
        if (txt.equals("编辑")) {
            mTxtRightMenu.setText("完成");
            adapter.isEditMode = true;
            rlDelete.setVisibility(View.VISIBLE);
        } else {
            mTxtRightMenu.setText("编辑");
            adapter.isEditMode = false;
            rlDelete.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_footmark;
    }

    @Override
    protected void initView() {
        rlDelete = (RelativeLayout) findViewById(R.id.rl_bottom);
        rvFootmark = (LRecyclerView) findViewById(R.id.rv_footmark);
        rvFootmark.setHasFixedSize(true);
        rvFootmark.setLayoutManager(new LinearLayoutManager(mContext));
        rvFootmark.setHeaderViewColor(R.color.colorAccent, R.color.dark, android.R.color.white);
        rvFootmark.setFooterViewColor(R.color.colorAccent, R.color.dark, android.R.color.white);
        rvFootmark.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧");
    }
}
