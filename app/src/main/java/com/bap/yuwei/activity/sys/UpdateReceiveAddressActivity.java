package com.bap.yuwei.activity.sys;

import android.os.Bundle;
import android.view.View;

import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.StringUtils;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 更新收货地址
 */
public class UpdateReceiveAddressActivity extends AddReceiveAddressActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 更新收货地址
     */
    @Override
    public void addAddress(View v){
        showLoadingDialog();
        Map<String,Object> params=new HashMap<>();
        params.put("cellphone", StringUtils.getEditTextValue(etTel));
        params.put("city", city);
        params.put("consignee",StringUtils.getEditTextValue(etName));
        params.put("isDefault",cbDefault.isChecked()? true:false);
        params.put("province",province);
        params.put("region",region);
        params.put("street",StringUtils.getEditTextValue(etAddress));
        params.put("userId",mUser.getUserId());
        params.put("shippingAddressId",address.getShippingAddressId());
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=sysWebService.updateReceiveAddress(mUser.getUserId(),address.getShippingAddressId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
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
                dismissProgressDialog();
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }

    /**
     * 初始化UI
     */
    @Override
    protected void initUiWithValue() {
        province=address.getProvince();
        city=address.getCity();
        region=address.getRegion();
        etName.setText(address.getConsignee());
        etTel.setText(address.getCellphone());
        txtArea.setText(address.getProvince()+address.getCity()+address.getRegion());
        etAddress.setText(address.getStreet());
        if(address.getIsDefault()){
            cbDefault.setChecked(true);
        }else {
            cbDefault.setChecked(false);
        }
    }
}
