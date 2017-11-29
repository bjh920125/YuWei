package com.bap.yuwei.activity.sys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.commonadapter.CommonAdapter;
import com.bap.yuwei.adapter.commonadapter.ViewHolder;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.event.QueryUnreadCountEvent;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.sys.Msg;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.SysWebService;
import com.google.gson.reflect.TypeToken;
import com.linearlistview.LinearListView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 消息菜单
 */
public class MsgMenusActivity extends BaseActivity {

    private TextView txtSysMsg,txtExpressMsg,txtSysTime,txtExpressTime;
    private ImageView imgRedDotSys,imgRedDotExpress;
    private List<Msg> msgs;
    private LinearListView lvOrderMsg;
    private List<Msg> orderMsgs;
    private CommonAdapter<Msg> mAdapter;
    private int messageType;

    private SysWebService sysWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sysWebService= MyApplication.getInstance().getWebService(SysWebService.class);
        msgs=new ArrayList<>();
        orderMsgs=new ArrayList<>();
        mAdapter=new CommonAdapter<Msg>(mContext,orderMsgs,R.layout.item_order_msg) {
            @Override
            public void convert(ViewHolder viewHolder, Msg item) {
                viewHolder.setText(R.id.txt_order_msg_title,item.getShopName());
                viewHolder.setText(R.id.txt_order_summary,item.getContent());
                viewHolder.setText(R.id.txt_order_time,item.getCreateTime().substring(0,16));
                viewHolder.setImageByUrl(R.id.img_order_msg, Constants.PICTURE_URL+item.getShopIcon());
                if(item.getUnReadCount()>0){
                    viewHolder.setVisibility(R.id.img_red_dot,View.VISIBLE);
                }else {
                    viewHolder.setVisibility(R.id.img_red_dot,View.GONE);
                }
            }
        };
        lvOrderMsg.setAdapter(mAdapter);
        lvOrderMsg.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView parent, View view, int position, long id) {
                Msg msg=orderMsgs.get(position);
                msg.setUnReadCount(0);
                mAdapter.notifyDataSetChanged();
                Intent intent=new Intent(mContext,MsgListActivity.class);
                intent.putExtra(MsgListActivity.MSG_TYPE_KEY,Constants.ORDER_MSG);//0系统消息 1物流消息 2订单消息 3通知消息
                intent.putExtra(Msg.KEY,msg);
                startActivity(intent);
            }
        });
        getMsgs();
    }

    /**
     * 转跳消息列表界面
     */
    public void showMsgsList(View v){
        switch (v.getId()){
            case R.id.rl_sys://系统消息
                messageType=0;
                setUnreadCount(msgs.get(0));
                break;
            case R.id.rl_express://快递消息
                messageType=1;
                setUnreadCount(msgs.get(1));
                break;
            default:break;
        }
        mAdapter.notifyDataSetChanged();
        Intent intent=new Intent(mContext,MsgListActivity.class);
        intent.putExtra(MsgListActivity.MSG_TYPE_KEY,messageType);//0系统消息 1物流消息 2订单消息 3通知消息
        startActivity(intent);
    }

    /**
     * 设置未读数量
     */
    private void setUnreadCount(Msg msg){
        if(null!=msg){
            msg.setUnReadCount(0);
            initUIWithValues();
        }
    }

    /**
     * 获取消息列表
     */
    private void getMsgs(){
        showLoadingDialog();
        Call<ResponseBody> call=sysWebService.getMsgs(mUser.getUserId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        msgs.clear();
                        JSONArray jo=new JSONObject(result).getJSONArray("result");
                        List<Msg> tempList = mGson.fromJson(jo.toString(), new TypeToken<List<Msg>>() {}.getType());
                        msgs.addAll(tempList);
                        initUIWithValues();
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

    /**
     * 初始化UI
     */
    private void initUIWithValues(){
        try{
            Msg sysMsg=msgs.get(0);//系统消息
            if(null!=sysMsg){
                txtSysMsg.setText(sysMsg.getContent());
                txtSysTime.setText(sysMsg.getCreateTime().substring(0,16));
                if(sysMsg.getUnReadCount()>0){
                    imgRedDotSys.setVisibility(View.VISIBLE);
                }
            }

            Msg expressMsg=msgs.get(1);//物流消息
            if(null!=expressMsg){
                txtExpressMsg.setText(expressMsg.getContent());
                txtExpressTime.setText(sysMsg.getCreateTime().substring(0,16));
                if(sysMsg.getUnReadCount()>0){
                    imgRedDotExpress.setVisibility(View.VISIBLE);
                }
            }

            if(orderMsgs.size()==0){//订单消息
                for(int i=2;i<msgs.size();i++){
                    orderMsgs.add(msgs.get(i));
                }
                mAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(new QueryUnreadCountEvent());
        super.onBackPressed();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_msg_menus;
    }

    @Override
    protected void initView() {
        txtSysMsg= (TextView) findViewById(R.id.txt_sys_summary);
        txtExpressMsg= (TextView) findViewById(R.id.txt_express_summary);
        lvOrderMsg= (LinearListView) findViewById(R.id.lv_order_msg);
        txtSysTime= (TextView) findViewById(R.id.txt_time);
        txtExpressTime= (TextView) findViewById(R.id.txt_express_time);
        imgRedDotSys= (ImageView) findViewById(R.id.img_sys_dot);
        imgRedDotExpress= (ImageView) findViewById(R.id.img_express_dot);
    }
}
