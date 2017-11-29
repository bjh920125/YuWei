package com.bap.yuwei.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.ListBaseAdapter;
import com.bap.yuwei.adapter.RefundAdapter;
import com.bap.yuwei.entity.event.CancelRefundEvent;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.Refund;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.OrderWebService;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
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
 * 退款列表
 */
public class RefundListActivity extends BaseActivity {

    protected LRecyclerView rvRefund;

    private List<Refund> refunds;
    protected RefundAdapter adapter;
    protected LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    private OrderWebService orderWebService;

    private int pageIndex=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refunds=new ArrayList<>();
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        adapter = new RefundAdapter(this,refunds);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        rvRefund.setAdapter(mLRecyclerViewAdapter);

        adapter.setOnItemClickListener(new ListBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Refund refund=refunds.get(position);
                Intent i=new Intent(mContext, RefundDetailActivity.class);
                i.putExtra(Refund.KEY,refund);
                startActivity(i);
            }
        });

        rvRefund.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refunds.clear();
                adapter.clear();
                pageIndex=1;
                getRefundList();
            }
        });

        rvRefund.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                pageIndex++;
                getRefundList();
            }
        });

        rvRefund.refresh();
    }

    /**
     * 取消退款之后接收的event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshList(CancelRefundEvent event){
        rvRefund.refresh();
    }

    private void getRefundList(){
        Map<String,Object> params=new HashMap<>();
        params.put("userId",mUser.getUserId());
        params.put("pageNumber",pageIndex);
        params.put("pageSize",12);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=orderWebService.getRefundList(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONArray jo=new JSONObject(result).getJSONObject("result").getJSONArray("list");
                        List<Refund> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<Refund>>() {}.getType());
                        if(tempList!=null && tempList.size()>0){
                            refunds.addAll(tempList);
                            adapter.addAll(tempList);
                            adapter.notifyDataSetChanged();
                        }else{
                            rvRefund.setNoMore(true);
                        }
                        rvRefund.refreshComplete(tempList.size());
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
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refund_list;
    }

    @Override
    protected void initView() {
        rvRefund= (LRecyclerView) findViewById(R.id.rv_refund);
        rvRefund.setHasFixedSize(true);
        rvRefund.setLayoutManager(new LinearLayoutManager(mContext));
        rvRefund.setHeaderViewColor(R.color.colorAccent, R.color.dark ,android.R.color.white);
        rvRefund.setFooterViewColor(R.color.colorAccent, R.color.dark ,android.R.color.white);
        rvRefund.setFooterViewHint("拼命加载中","已经全部为你呈现了","网络不给力啊，点击再试一次吧");
    }
}
