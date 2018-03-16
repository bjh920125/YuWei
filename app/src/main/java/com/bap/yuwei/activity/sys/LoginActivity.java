package com.bap.yuwei.activity.sys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.MainActivity;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.sys.User;
import com.bap.yuwei.entity.event.UserInfoEvent;
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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity {

    private ClearableEditText etUserName,etPassword;

    private SysWebService webService;

    private String ip=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webService = MyApplication.getInstance().getWebService(SysWebService.class);
        getIp();
        initUIWithValue();
    }

    /**
     * 登录
     */
    public void login(View v){
        showLoadingDialog();
        Map<String,Object> params=new HashMap<>();
        String username=StringUtils.getEditTextValue(etUserName);
        String password= MD5Utils.encode(StringUtils.getEditTextValue(etPassword)).toLowerCase();
        long timestamp=System.currentTimeMillis();
        String randomStr=new Random().nextLong()+"";
        params.put("username", username);
        params.put("password", password);
        params.put("type",2);
        params.put("timestamp",timestamp);
        params.put("randomStr",randomStr);
        params.put("ip",ip);
        params.put("sign", MD5Utils.encode("ip="+ip+"&password="+password+"&randomStr="+randomStr+"&timestamp="+timestamp+"&type=2"+"&username="+username).toUpperCase());
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=webService.login(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        User user=mGson.fromJson(jo.getString("user"),User.class);
                        user.setLoginName(StringUtils.getEditTextValue(etUserName));
                        user.setPassword(StringUtils.getEditTextValue(etPassword));
                        SharedPreferencesUtil.putString(mContext,Constants.USER_KEY,mGson.toJson(user));
                        SharedPreferencesUtil.putString(mContext, Constants.TOKEN_KEY,jo.getString("token"));
                        getXToken(jo.getString("token"),user.getUserId());
                        EventBus.getDefault().post(new UserInfoEvent());
                        startActivity(new Intent(mContext, MainActivity.class));
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
     * 生成并保存接口头部的token
     */
    private void getXToken(String token,String userId){
        String str=token+":"+userId+":"+Constants.DEVICE_TYPE;
        String xToken = Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
        SharedPreferencesUtil.putString(mContext,Constants.XTOKEN_KEY,xToken.trim());
    }

    private void initUIWithValue(){
        if(null==mUser) return;
        etPassword.setText(mUser.getPassword());
        etUserName.setText(mUser.getLoginName());
    }

    /**
     * 获取ip
     */
    private void getIp(){
        Call<ResponseBody> call=webService.getIp("myip");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jo=new JSONObject(response.body().string());
                    ip=jo.getJSONObject("data").getString("ip");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * 注册
     */
    public void regist(View v){
        startActivity(new Intent(mContext,RegisterActivity.class));
        finish();
    }

    /**
     * 忘记密码
     */
    public void forgetPwd(View v){
        startActivity(new Intent(mContext,ForgetPwdActivity.class));
        finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        etUserName= (ClearableEditText) findViewById(R.id.et_user_name);
        etPassword= (ClearableEditText) findViewById(R.id.et_pwd);
    }
}
