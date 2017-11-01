package com.bap.yuwei.activity.sys;

import android.os.Bundle;
import android.view.View;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.StringUtils;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.SysWebService;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddReceiveAddressActivity extends BaseActivity {

    private EditText etName,etTel,etAddress;
    private TextView txtArea;
    private Checkable cbDefault;

    private String province,city,region;

    private SysWebService sysWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sysWebService= MyApplication.getInstance().getWebService(SysWebService.class);
    }

    public void addAddress(View v){
        Map<String,Object> params=new HashMap<>();
        params.put("cellphone", StringUtils.getEditTextValue(etTel));
        params.put("city", city);
        params.put("consignee",StringUtils.getEditTextValue(etName));
        params.put("isDefault",cbDefault.isChecked()? true:false);
        params.put("province",province);
        params.put("region",region);
        params.put("street",StringUtils.getEditTextValue(etAddress));
        params.put("userId",mUser.getUserId());
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=sysWebService.addReceiveAddress(mUser.getUserId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        finish();
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
    protected int getLayoutId() {
        return R.layout.activity_add_receive_address;
    }

    @Override
    protected void initView() {
        etName= (EditText) findViewById(R.id.et_name);
        etTel= (EditText) findViewById(R.id.et_tel);
        etAddress= (EditText) findViewById(R.id.et_address);
        txtArea= (TextView) findViewById(R.id.et_area);
        cbDefault= (Checkable) findViewById(R.id.cb_default);
    }
}
