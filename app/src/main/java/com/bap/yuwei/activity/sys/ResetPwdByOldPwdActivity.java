package com.bap.yuwei.activity.sys;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MD5Utils;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.SharedPreferencesUtil;
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

public class ResetPwdByOldPwdActivity extends BaseActivity {

    private ClearableEditText etOldPwd,etPwd,etConfirmPwd;

    private SysWebService webService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webService = MyApplication.getInstance().getWebService(SysWebService.class);
    }

    public void changePwd(View v){
        if(validateForm()){
            checkPwd();

        }
    }

    /**
     * 更新用户信息
     */
    private void updateUser(){
        Map<String,Object> params=new HashMap<>();
        String password= MD5Utils.encode(StringUtils.getEditTextValue(etPwd)).toLowerCase();
        params.put("password", password);
        params.put("deviceType",Constants.DEVICE_TYPE);
        params.put("userId",mUser.getUserId());
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=webService.updatePassword(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        mUser.setPassword(StringUtils.getEditTextValue(etPwd));
                        SharedPreferencesUtil.putString(mContext, Constants.USER_KEY,mGson.toJson(mUser));
                        ToastUtil.showShort(mContext,"密码修改成功！");
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

    private void checkPwd(){
        String password= MD5Utils.encode(StringUtils.getEditTextValue(etOldPwd)).toLowerCase();
        Map<String,Object> params=new HashMap<>();
        params.put("password", password);
        params.put("userId",mUser.getUserId());
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=webService.checkPassword(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        boolean isTrue=new JSONObject(result).getBoolean("result");
                        if(isTrue){
                            updateUser();
                        }else{
                            ToastUtil.showShort(mContext,"旧的密码不正确！");
                        }
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

    public boolean validateForm(){
        String oldPwd=StringUtils.getEditTextValue(etOldPwd);
        String pwd= StringUtils.getEditTextValue(etPwd);
        String confirmPwd=StringUtils.getEditTextValue(etConfirmPwd);

        if(TextUtils.isEmpty(oldPwd)){
            ToastUtil.showShort(mContext,"请输入旧密码！");
            return false;
        }

        if(TextUtils.isEmpty(pwd)){
            ToastUtil.showShort(mContext,"请输入新密码！");
            return false;
        }
        if(TextUtils.isEmpty(confirmPwd)){
            ToastUtil.showShort(mContext,"请再次输入新密码！");
            return false;
        }

        if(!pwd.equals(confirmPwd)){
            ToastUtil.showShort(mContext,"两次输入的新密码不一致！");
            return false;
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_pwd_by_old_pwd;
    }

    @Override
    protected void initView() {
        etOldPwd=(ClearableEditText) findViewById(R.id.et_old_pwd);
        etPwd= (ClearableEditText) findViewById(R.id.et_pwd);
        etConfirmPwd= (ClearableEditText) findViewById(R.id.et_confirm_pwd);
    }

}
