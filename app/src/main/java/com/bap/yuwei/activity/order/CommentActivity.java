package com.bap.yuwei.activity.order;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.CommentCommitAdapter;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
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

    private Orders orders;
    private List<String> imgPathes;
    protected int uploadCount=0;//已经上传的文件计数器

    private OrderWebService orderWebService;
    private SysWebService sysWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgPathes=new ArrayList<>();
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        sysWebService=MyApplication.getInstance().getWebService(SysWebService.class);
        orders= (Orders) getIntent().getSerializableExtra(Orders.KEY);
        mAdapter=new CommentCommitAdapter(orders.getOrderItems(),mContext);
        lvOrderItem.setAdapter(mAdapter);
        rbSend.setStar(5.0f);
        rbService.setStar(5.0f);

    }

    private void updateFile(File file){
        mProgressDialog.show();
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
                        mProgressDialog.dismiss();
                        String path=new JSONObject(result).getString("result");
                        imgPathes.add(path);
                        uploadCount++;
                        //if(uploadCount==filePaths.size()){
                            //comment();
                       // }
                    }else{
                        ToastUtil.showShort(mContext,appResponse.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }

    private void comment(){

    }

    public void comment(View v){
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
