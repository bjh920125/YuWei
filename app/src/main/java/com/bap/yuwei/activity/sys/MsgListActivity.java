package com.bap.yuwei.activity.sys;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.MsgAdapter;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.sys.Msg;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.SysWebService;
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

/**
 * 消息列表
 */
public class MsgListActivity extends BaseActivity {

    private LRecyclerView rvMsgs;

    private List<Msg> msgs;
    private int messageType;//0系统消息 1物流消息 2订单消息 3通知消息
    protected LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private MsgAdapter msgAdapter;

    private Msg msg;

    public static final String MSG_TYPE_KEY="msg.type.key";

    protected int  pageIndex = 1;

    private SysWebService sysWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageType=getIntent().getIntExtra(MSG_TYPE_KEY,0);
        sysWebService= MyApplication.getInstance().getWebService(SysWebService.class);
        msgs=new ArrayList<>();
        msgAdapter = new MsgAdapter(mContext,messageType);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(msgAdapter);
        rvMsgs.setAdapter(mLRecyclerViewAdapter);

        rvMsgs.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                msgs.clear();
                msgAdapter.clear();
                pageIndex=1;
                setMethodByType();
            }
        });

        rvMsgs.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                pageIndex++;
                setMethodByType();
            }
        });
        rvMsgs.refresh();
    }

    /**
     * 根据类型获取列表
     */
    private void setMethodByType(){
        if(messageType==Constants.ORDER_MSG){
            getOrderMsgs();
        }else {
            getMsgs();
        }
    }

    /**
     * 获取消息
     */
    private void getMsgs(){
        Map<String,Object> params=new HashMap<>();
        params.put("messageType", messageType);
        //params.put("status", 0);//阅读状态 0未读 1已读，可选
        params.put("userType", 0);//用户类型 0作为买家 1作为卖家，可选
        params.put("page",pageIndex);
        params.put("size",15);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=sysWebService.getMsgsByType(mUser.getUserId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONArray jo=new JSONObject(result).getJSONObject("result").getJSONArray("list");
                        List<Msg> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<Msg>>() {}.getType());
                        if(tempList!=null && tempList.size()>0){
                            msgs.addAll(tempList);
                            msgAdapter.addAll(tempList);
                            msgAdapter.notifyDataSetChanged();
                        }else{
                            rvMsgs.setNoMore(true);
                        }
                        rvMsgs.refreshComplete(tempList.size());
                    }else{
                        ToastUtil.showShort(mContext,appResponse.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    rvMsgs.refreshComplete(0);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }

    /**
     * 获取订单消息
     */
    private void getOrderMsgs(){
        msg= (Msg) getIntent().getSerializableExtra(Msg.KEY);
        Call<ResponseBody> call=sysWebService.getOrderMsgs(mUser.getUserId(),msg.getShopId(), Constants.BUYER);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONArray jo=new JSONObject(result).getJSONArray("result");
                        List<Msg> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<Msg>>() {}.getType());
                        if(tempList!=null && tempList.size()>0){
                            msgs.addAll(tempList);
                            msgAdapter.addAll(tempList);
                            msgAdapter.notifyDataSetChanged();
                        }
                        rvMsgs.refreshComplete(tempList.size());
                        rvMsgs.setNoMore(true);
                    }else{
                        ToastUtil.showShort(mContext,appResponse.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    rvMsgs.refreshComplete(0);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_msg_list;
    }

    @Override
    protected void initView() {
        rvMsgs= (LRecyclerView) findViewById(R.id.rv_msg);
        rvMsgs.setHasFixedSize(true);
        rvMsgs.setLayoutManager(new LinearLayoutManager(this));
        rvMsgs.setHeaderViewColor(R.color.colorAccent, R.color.dark ,android.R.color.white);
        rvMsgs.setFooterViewColor(R.color.colorAccent, R.color.dark ,android.R.color.white);
        rvMsgs.setFooterViewHint("拼命加载中","已经全部为你呈现了","网络不给力啊，点击再试一次吧");
    }
}
