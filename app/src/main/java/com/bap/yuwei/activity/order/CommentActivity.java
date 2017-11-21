package com.bap.yuwei.activity.order;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.CommentCommitAdapter;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.EvaluateItemForm;
import com.bap.yuwei.entity.order.EvaluationForm;
import com.bap.yuwei.entity.order.OrderItem;
import com.bap.yuwei.entity.order.Orders;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.view.RatingBar;
import com.bap.yuwei.webservice.OrderWebService;
import com.bap.yuwei.webservice.SysWebService;
import com.linearlistview.LinearListView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends BaseActivity {

    private RatingBar rbSend,rbService;
    private CheckBox cbHideName;
    private LinearListView lvOrderItem;

    private CommentCommitAdapter mAdapter;
    private EvaluationForm evaluationForm;
    private List<EvaluateItemForm> itemForms;

    private Orders orders;
    protected int uploadCount=0;//已经上传的文件计数器
    private int totalImageCount=0;

    private OrderWebService orderWebService;
    private SysWebService sysWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        sysWebService=MyApplication.getInstance().getWebService(SysWebService.class);
        orders= (Orders) getIntent().getSerializableExtra(Orders.KEY);
        initForm();
        mAdapter=new CommentCommitAdapter(orders.getOrderItems(),itemForms,mContext);
        lvOrderItem.setAdapter(mAdapter);
        rbSend.setStar(5.0f);
        rbService.setStar(5.0f);

        rbSend.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {
                evaluationForm.setLogisticsScore((int)ratingCount);
            }
        });
        rbService.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {
                evaluationForm.setServiceScore((int)ratingCount);
            }
        });

        cbHideName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                evaluationForm.setIsAllAnonymous(b);
                for(EvaluateItemForm form:itemForms){
                    form.setIsAnonymous(b);
                }
            }
        });

    }

    private void initForm(){
        evaluationForm=new EvaluationForm();
        evaluationForm.setEvaluateFrom(0);//买家评价
        evaluationForm.setIsAllAnonymous(false);
        evaluationForm.setOrderId(orders.getOrderId());
        evaluationForm.setShopId(orders.getShopId());
        evaluationForm.setUserId(Long.valueOf(mUser.getUserId()));
        evaluationForm.setUsername(mUser.getUsername());
        evaluationForm.setShopName(orders.getShopName());
        evaluationForm.setLogisticsScore(5);
        evaluationForm.setServiceScore(5);
        itemForms=new ArrayList<>();
        for(OrderItem item:orders.getOrderItems()){
            EvaluateItemForm form=new EvaluateItemForm();
            form.setGoodsId(item.getGoodsId());
            form.setGoodsTitle(item.getTitle());
            form.setModel(item.getModel());
            form.setPrice(item.getPreferentialPrice());
            itemForms.add(form);
        }
        evaluationForm.setItems(itemForms);
    }

    private void updateFile(final File file, final EvaluateItemForm item,final int i){
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Call<ResponseBody> call = sysWebService.uploadFile(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        String path=new JSONObject(result).getString("result");
                        String[] ps=item.getEvaluationImages();
                        ps[i]=path;
                        uploadCount++;
                        if(uploadCount==totalImageCount){
                            comment();
                        }
                    }else{
                        dismissProgressDialog();
                        ToastUtil.showShort(mContext,appResponse.getMessage());
                    }
                } catch (Exception e) {
                    dismissProgressDialog();
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

    private void comment(){
        showLoadingDialog();
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(evaluationForm));
        Call<ResponseBody> call=orderWebService.comment(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        ToastUtil.showShort(mContext,"评价成功！");
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

    public void comment(View v){
        showLoadingDialog();
        for(EvaluateItemForm item:itemForms){
            if(null != item.getFilePathes()){
                totalImageCount+=item.getFilePathes().size()-1;
            }
        }

        for(EvaluateItemForm item:itemForms){
            if(null != item.getFilePathes()){
                for(int i=0;i<item.getFilePathes().size()-1;i++){
                    updateFile(new File(item.getFilePathes().get(i)),item,i);
                }
            }
        }


        /**
        if(filePaths.size()>1){
            uploadCount=0;
            for(String ps:getUsefulImagePathes()){
                updateFile(new File(ps));
            }
        }else {
            comment();
        }*/
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment;
    }

    @Override
    public void initView() {
        rbSend= (RatingBar) findViewById(R.id.star_send);
        rbService= (RatingBar) findViewById(R.id.star_service);
        cbHideName= (CheckBox) findViewById(R.id.cb_hide_name);
        lvOrderItem= (LinearListView) findViewById(R.id.lv_order_item);
    }
}
