package com.bap.yuwei.activity.goods;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.commonadapter.CommonAdapter;
import com.bap.yuwei.adapter.commonadapter.ViewHolder;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.StringUtils;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.GoodsWebService;
import com.google.gson.reflect.TypeToken;
import com.linearlistview.LinearListView;

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
 * 综合搜索
 */
public class OverallSearchActivity extends BaseActivity {

    private EditText etWords;
    private LinearListView lvHotwords,lvHistory;

    private CommonAdapter<String> mHotwordsAdapter;
    private CommonAdapter<String> mHistoryAdapter;

    private List<String> mHotwords;
    private List<String> mSearchHistories;

    private GoodsWebService goodsWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsWebService= MyApplication.getInstance().getWebService(GoodsWebService.class);
        mHotwords=new ArrayList<>();
        mSearchHistories=new ArrayList<>();
        initHotwordsListview();
        initHistoryListview();
        getHotWords();
        getDefaultHotWords();
        getSearchHistory();
    }

    public void search(View v){
        String words= StringUtils.getEditTextValue(etWords);
        toSearchPage(words);
    }

    /**
     * 获取默认搜索词
     */
    private void getDefaultHotWords(){
        Call<ResponseBody> call=goodsWebService.getDefaultHotWords();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result);
                        String words=jo.getString("result");
                        if(!TextUtils.isEmpty(words) && !"null".equals(words)){
                            etWords.setText(words);
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
     * 获取热门搜索词
     */
    private void getHotWords(){
        Map<String,Object> params=new HashMap<>();
        params.put("page",1);
        params.put("size",10);
        Call<ResponseBody> call=goodsWebService.getHotWords(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONArray ja=new JSONObject(result).getJSONArray("result");
                        List<String> tempList=mGson.fromJson(ja.toString(),new TypeToken<List<String>>() {}.getType());
                        mHotwords.addAll(tempList);
                        mHotwordsAdapter.notifyDataSetChanged();
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
     * 获取我的搜索记录
     */
    private void getSearchHistory(){
        if(null==mUser) return;
        Map<String,Object> params=new HashMap<>();
        params.put("page",1);
        params.put("size",10);
        Call<ResponseBody> call=goodsWebService.getSearchHistory(mUser.getUserId(),params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONArray ja=new JSONObject(result).getJSONArray("result");
                        List<String> tempList=mGson.fromJson(ja.toString(),new TypeToken<List<String>>() {}.getType());
                        mSearchHistories.addAll(tempList);
                        mHistoryAdapter.notifyDataSetChanged();
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
     * 删除我的搜索记录
     */
    public void deleteSearchHistory(View v){
        if(null==mUser) return;
        Call<ResponseBody> call=goodsWebService.deleteSearchHistory(mUser.getUserId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        mSearchHistories.clear();
                        mHistoryAdapter.notifyDataSetChanged();
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

    private void initHotwordsListview(){
        mHotwordsAdapter=new CommonAdapter<String>(mContext,mHotwords,R.layout.item_hot_words) {
            @Override
            public void convert(ViewHolder viewHolder, String item) {
                viewHolder.setText(R.id.txt_hot_words,item);
            }
        };
        lvHotwords.setAdapter(mHotwordsAdapter);

        lvHotwords.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView parent, View view, int position, long id) {
                toSearchPage(mHotwords.get(position));
            }
        });
    }

    private void initHistoryListview(){
        mHistoryAdapter=new CommonAdapter<String>(mContext,mSearchHistories,R.layout.item_text) {
            @Override
            public void convert(ViewHolder viewHolder, String item) {
                viewHolder.setText(R.id.txt,item);
            }
        };
        lvHistory.setAdapter(mHistoryAdapter);
        lvHistory.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView parent, View view, int position, long id) {
                toSearchPage(mSearchHistories.get(position));
            }
        });
    }

    private void toSearchPage(String words){
        Intent i=new Intent(mContext,SearchGoodsActivity.class);
        i.putExtra(SearchGoodsActivity.KEYWORDS_KEY,words);
        startActivity(i);
    }

    public void onBackClick(View v){
        super.onBackPressed();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_overall_search;
    }

    @Override
    protected void initView() {
        etWords= (EditText) findViewById(R.id.et_words);
        lvHotwords= (LinearListView) findViewById(R.id.lv_hot_words);
        lvHistory= (LinearListView) findViewById(R.id.lv_history);
    }
}
