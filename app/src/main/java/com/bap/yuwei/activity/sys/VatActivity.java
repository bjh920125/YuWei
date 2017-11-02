package com.bap.yuwei.activity.sys;

import android.os.Bundle;
import android.view.View;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.sys.Vat;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.StringUtils;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.view.ClearableEditText;
import com.bap.yuwei.webservice.SysWebService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VatActivity extends BaseActivity {

    private ClearableEditText etUnitName,etPayNo,etAddress,etPhone,etBank,etBankAccount;

    private Vat mVat;

    private SysWebService webService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webService = MyApplication.getInstance().getWebService(SysWebService.class);
        getVat();
    }

    public void updateVat(View v){
        Map<String,Object> params=new HashMap<>();
        params.put("address", StringUtils.getEditTextValue(etAddress));
        params.put("bankAccount", StringUtils.getEditTextValue(etBankAccount));
        params.put("bankName",StringUtils.getEditTextValue(etBank));
        params.put("cellphone",StringUtils.getEditTextValue(etPhone));
        params.put("companyName",StringUtils.getEditTextValue(etUnitName));
        params.put("taxpayerNo",StringUtils.getEditTextValue(etPayNo));
        params.put("userId",mUser.getUserId());
        params.put("vatId",null!=mVat ? mVat.getVatId():null);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=webService.updateVat(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        ToastUtil.showShort(mContext,"操作成功！");
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

    private void getVat(){
        if(null==mUser) return;
        Call<ResponseBody> call=webService.getVat(mUser.getUserId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        mVat=mGson.fromJson(jo.toString(),Vat.class);
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
        if(null==mVat) return;
        etUnitName.setText(mVat.getCompanyName());
        etPayNo.setText(mVat.getTaxpayerNo());
        etAddress.setText(mVat.getAddress());
        etPhone.setText(mVat.getCellphone());
        etBank.setText(mVat.getBankName());
        etBankAccount.setText(mVat.getBankAccount());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vat;
    }

    @Override
    protected void initView() {
        etUnitName= (ClearableEditText) findViewById(R.id.et_unit);
        etPayNo= (ClearableEditText) findViewById(R.id.et_pay_no);
        etAddress= (ClearableEditText) findViewById(R.id.et_address);
        etPhone= (ClearableEditText) findViewById(R.id.et_phone);
        etBank= (ClearableEditText) findViewById(R.id.et_bank);
        etBankAccount= (ClearableEditText) findViewById(R.id.et_bank_account);
    }
}
