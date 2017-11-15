package com.bap.yuwei.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.ListBaseAdapter;
import com.bap.yuwei.adapter.OrderListAdapter;
import com.bap.yuwei.entity.event.CancelOrderEvent;
import com.bap.yuwei.entity.event.DeleteOrderEvent;
import com.bap.yuwei.entity.event.ReceiveOrderEvent;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.Orders;
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

import static com.bap.yuwei.entity.Constants.ORDER_STATUS_HAS_SENDED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_PENDING_PAY;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_PRE_DELIVERED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_PRE_EVALUATED;

public class OrderListActivity extends BaseActivity {

    private RadioGroup rgStatus;
    private LRecyclerView rvOrder;

    private List<Orders> orderses;
    private OrderListAdapter adapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private Integer orderStatus=null;

    private OrderWebService orderWebService;
    private int pageIndex=1;

    public static final String STATUS_INDEX_KEY="status.index.key";
    private int defaultIndex=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        defaultIndex=getIntent().getIntExtra(STATUS_INDEX_KEY,0);
        orderses=new ArrayList<>();
        adapter = new OrderListAdapter(mContext,orderses,mUser.getUserId());
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        rvOrder.setAdapter(mLRecyclerViewAdapter);

        adapter.setOnItemClickListener(new ListBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Orders order=orderses.get(position);
                Intent i=new Intent(mContext,OrderDetailActivity.class);
                i.putExtra(OrderDetailActivity.ORDER_ID_KEY,order.getOrderId());
                startActivity(i);
            }
        });

        rgStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.rb_all:
                        orderStatus=null;
                        break;
                    case R.id.rb_pay:
                        orderStatus=ORDER_STATUS_PENDING_PAY;
                        break;
                    case R.id.rb_send:
                        orderStatus=ORDER_STATUS_PRE_DELIVERED;
                        break;
                    case R.id.rb_receive:
                        orderStatus=ORDER_STATUS_HAS_SENDED;
                        break;
                    case R.id.rb_comment:
                        orderStatus=ORDER_STATUS_PRE_EVALUATED;
                        break;
                    default:break;
                }
                rvOrder.refresh();
            }
        });

        rvOrder.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                orderses.clear();
                adapter.clear();
                pageIndex=1;
                getOrderList();
            }
        });

        rvOrder.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                pageIndex++;
                getOrderList();
            }
        });
        RadioButton rb= (RadioButton) rgStatus.getChildAt(defaultIndex);
        rb.setChecked(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteOrderEvent(DeleteOrderEvent event){
        rvOrder.refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cancelOrderEvent(CancelOrderEvent event){
        rvOrder.refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveOrderEvent(ReceiveOrderEvent event){
        rvOrder.refresh();
    }

    private void getOrderList(){
        Map<String,Object> params=new HashMap<>();
        //params.put("dateBegin",categoryNodes);
        //params.put("dateEnd",0);
       // params.put("keyword",goodsTitle);
        //params.put("shopName","");
        params.put("orderStatus",orderStatus);
        params.put("page",pageIndex);
        params.put("size",12);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=orderWebService.getOrderList(mUser.getUserId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONArray jo=new JSONObject(result).getJSONObject("result").getJSONArray("list");
                        List<Orders> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<Orders>>() {}.getType());
                        if(tempList!=null && tempList.size()>0){
                            orderses.addAll(tempList);
                            adapter.addAll(tempList);
                            adapter.notifyDataSetChanged();
                        }else{
                            rvOrder.setNoMore(true);
                        }
                        rvOrder.refreshComplete(tempList.size());
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
        return R.layout.activity_order_list;
    }

    @Override
    protected void initView() {
        rgStatus= (RadioGroup) findViewById(R.id.rg_staus);
        rvOrder= (LRecyclerView) findViewById(R.id.rv_order);

        rvOrder.setHasFixedSize(true);
        rvOrder.setLayoutManager(new LinearLayoutManager(mContext));
        rvOrder.setHeaderViewColor(R.color.colorAccent, R.color.dark ,android.R.color.white);
        rvOrder.setFooterViewColor(R.color.colorAccent, R.color.dark ,android.R.color.white);
        rvOrder.setFooterViewHint("拼命加载中","已经全部为你呈现了","网络不给力啊，点击再试一次吧");
    }
}
