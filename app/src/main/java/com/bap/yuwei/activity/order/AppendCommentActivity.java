package com.bap.yuwei.activity.order;

import android.os.Bundle;
import android.view.View;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.CommentAppendAdapter;
import com.bap.yuwei.entity.event.CommentOrderEvent;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.CommentAppendForm;
import com.bap.yuwei.entity.order.CommentAppendItemForm;
import com.bap.yuwei.entity.order.OrderItem;
import com.bap.yuwei.entity.order.Orders;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.OrderWebService;
import com.linearlistview.LinearListView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 追加评价
 */
public class AppendCommentActivity extends BaseActivity {

    private LinearListView lvOrderItem;

    private CommentAppendAdapter adapter;

    private OrderWebService orderWebService;
    private Orders orders;

    private CommentAppendForm form;
    private List<CommentAppendItemForm> itemForms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        orders= (Orders) getIntent().getSerializableExtra(Orders.KEY);
        initForm();
        adapter=new CommentAppendAdapter(orders.getOrderItems(),itemForms,mContext);
        lvOrderItem.setAdapter(adapter);
    }

    /**
     * 追加评价
     */
    public void appendComment(View v){
        showLoadingDialog();
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(form));
        Call<ResponseBody> call=orderWebService.appendComment(mUser.getUserId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        ToastUtil.showShort(mContext,"追评成功！");
                        EventBus.getDefault().post(new CommentOrderEvent());
                        finish();
                    }else{
                        ToastUtil.showShort(mContext,appResponse.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dismissProgressDialog();
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }

    /**
     * 初始化参数
     */
    private void initForm(){
        form=new CommentAppendForm();
        form.setOrderId(orders.getOrderId());
        itemForms=new ArrayList<>();
        for(OrderItem item:orders.getOrderItems()){
            CommentAppendItemForm form=new CommentAppendItemForm();
            form.setGoodsId(item.getGoodsId());
            form.setOrderItemId(item.getOrderItemId());
            itemForms.add(form);
        }
        form.setEvaluations(itemForms);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_append_comment;
    }

    @Override
    protected void initView() {
        lvOrderItem= (LinearListView) findViewById(R.id.lv_order_item);
    }
}
