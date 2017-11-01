package com.bap.yuwei.activity.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.bap.pla.PLAAdapterView;
import com.bap.pla.PLALoadMoreListView;
import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.commonadapter.CommonAdapter;
import com.bap.yuwei.adapter.commonadapter.ViewHolder;
import com.bap.yuwei.entity.news.News;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.NewsWebService;
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

public class NewsListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,PLALoadMoreListView.OnLoadMoreListener{

    private SwipeRefreshLayout swipeRefresh;
    private PLALoadMoreListView mListview;

    private List<News> mNews;
    private CommonAdapter<News> mAdapter;

    private NewsWebService newsWebService;

    private int pageIndex=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsWebService= MyApplication.getInstance().getWebService(NewsWebService.class);
        mNews=new ArrayList<>();
        mAdapter=new CommonAdapter<News>(mContext,mNews,R.layout.item_news) {
            @Override
            public void convert(ViewHolder viewHolder, News item) {
                viewHolder.setText(R.id.txt_title,item.getTitle());
                viewHolder.setText(R.id.txt_time,item.getCreateTime());
            }
        };
        mListview.setAdapter(mAdapter);

        mListview.setOnItemClickListener(new PLAAdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLAAdapterView<?> parent, View view, int position, long id) {
                News news= (News) mListview.getItemAtPosition(position);
                Intent intent=new Intent(mContext,NewsDetailActivity.class);
                intent.putExtra(News.KEY,news);
                startActivity(intent);
            }
        });
        getNews();
    }

    private void getNews(){
        Map<String,Object> params=new HashMap<>();
        params.put("pageNumber",pageIndex);
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
                        JSONArray jo=new JSONObject(result).getJSONObject("result").getJSONArray("list");
                        List<News> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<News>>() {}.getType());
                        mNews.addAll(tempList);
                        mAdapter.notifyDataSetChanged();
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
    public void onRefresh() {
        mNews.clear();
        mAdapter.notifyDataSetChanged();
        pageIndex=1;
        mListview.setCanLoadMore(true);
        getNews();
    }

    @Override
    public void onLoadMore() {
        pageIndex++;
        getNews();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_news_list;
    }

    @Override
    protected void initView() {
        mListview= (PLALoadMoreListView) findViewById(R.id.lv_news);
        swipeRefresh= (SwipeRefreshLayout) findViewById(R.id.swipe);
        mListview.setOnLoadMoreListener(this);
        swipeRefresh.setOnRefreshListener(this);
    }
}
