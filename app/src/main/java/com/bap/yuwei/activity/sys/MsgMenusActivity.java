package com.bap.yuwei.activity.sys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.sys.Msg;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.SysWebService;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MsgMenusActivity extends BaseActivity {


    private TextView txtSysMsg,txtExpressMsg,txtOrderMsg;
    private TextView txtOrderMsgTitle;
    private ImageView imgOrder;
    private List<Msg> msgs;
    private int messageType;

    private SysWebService sysWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sysWebService= MyApplication.getInstance().getWebService(SysWebService.class);
        msgs=new ArrayList<>();
        getMsgs();
    }

    public void showMsgsList(View v){
        switch (v.getId()){
            case R.id.rl_sys:
                messageType=0;
                break;
            case R.id.rl_express:
                messageType=1;
                break;
            case R.id.rl_order:
                messageType=2;
                break;
            default:break;
        }
        Intent intent=new Intent(mContext,MsgListActivity.class);
        intent.putExtra(MsgListActivity.MSG_TYPE_KEY,messageType);//0系统消息 1物流消息 2订单消息 3通知消息
        startActivity(intent);
    }

    private void getMsgs(){
        Call<ResponseBody> call=sysWebService.getMsgs(mUser.getUserId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }

    private void initUIWithValues(){
        try{
            Msg sysMsg=msgs.get(0);
            if(null!=sysMsg){
                txtSysMsg.setText(sysMsg.getContent());
            }

            Msg expressMsg=msgs.get(1);
            if(null!=expressMsg){
                txtExpressMsg.setText(expressMsg.getContent());
            }

            Msg orderMsg=msgs.get(2);
            if(null!=orderMsg){
                txtOrderMsgTitle.setText(orderMsg.getShopName());
                txtOrderMsg.setText(orderMsg.getContent());
                ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+orderMsg.getShopIcon(),imgOrder, DisplayImageOptionsUtil.getOptions());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_msg_menus;
    }

    @Override
    protected void initView() {
        txtSysMsg= (TextView) findViewById(R.id.txt_sys_summary);
        txtExpressMsg= (TextView) findViewById(R.id.txt_express_summary);
        txtOrderMsg= (TextView) findViewById(R.id.txt_order_summary);
        txtOrderMsgTitle= (TextView) findViewById(R.id.txt_order_msg_title);
        imgOrder= (ImageView) findViewById(R.id.img_order_msg);
    }
}
