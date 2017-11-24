package com.bap.yuwei.activity.sys;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.SharedPreferencesUtil;
import com.bap.yuwei.util.StringUtils;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.SysWebService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPhoneActivity extends BaseActivity {

    private TextView txtSecond;
    private EditText etPhone,etValidateCode;

    private String msgId;
    protected Timer timer;
    protected int restSecond=60;
    private SysWebService webService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webService = MyApplication.getInstance().getWebService(SysWebService.class);
    }


    /**
     * 更新用户信息
     */
    private void updateUser(){
        showLoadingDialog();
        final String phone = StringUtils.getEditTextValue(etPhone);
        Map<String,Object> params=new HashMap<>();
        params.put("phone",phone);
        params.put("userId",mUser.getUserId());
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=webService.updatePhone(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        mUser.setPhone(phone);
                        SharedPreferencesUtil.putString(mContext, Constants.USER_KEY, mGson.toJson(mUser));
                        ToastUtil.showShort(mContext, "新手机号绑定成功！");
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

    protected void startSecond(){
        timer=new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        restSecond--;
                        txtSecond.setText("重新获取（"+restSecond+"）S");
                        if(restSecond==1){
                            timer.cancel();
                            txtSecond.setText("获取验证码");
                            txtSecond.setClickable(true);
                        }
                    }
                });
            }
        }, 0,1000);
    }

    /**
     * 重新获取验证码
     */
    public void resend(View v){
        if(TextUtils.isEmpty(StringUtils.getEditTextValue(etPhone))){
            ToastUtil.showShort(mContext,"请输入新的手机号码！");
            return;
        }
        restSecond=60;
        if(null!=timer){
            timer.cancel();
        }
        txtSecond.setClickable(false);
        getSmsCode();
        startSecond();
    }

    /**
     * 获取验证码信息
     */
    public void getSmsCode() {
        final String phone = StringUtils.getEditTextValue(etPhone);
        Call<ResponseBody> call = webService.getSmsCode(phone);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    LogUtil.print("result", result);
                    AppResponse appResponse = mGson.fromJson(result, AppResponse.class);
                    if (appResponse.getCode() == ResponseCode.SUCCESS) {
                        msgId=new JSONObject(result).getString("result");
                    } else {
                        ToastUtil.showShort(mContext, appResponse.getMessage());
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

    /**
     * 检查验证码的正确性
     */
    public void checkCode(View v){
        String code=StringUtils.getEditTextValue(etValidateCode);
        Map<String,Object> params=new HashMap<>();
        params.put("code",code);
        params.put("msgId",msgId);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call = webService.validSmsCode(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    LogUtil.print("result", result);
                    AppResponse appResponse = mGson.fromJson(result, AppResponse.class);
                    if (appResponse.getCode() == ResponseCode.SUCCESS) {
                        updateUser();
                    } else {
                        ToastUtil.showShort(mContext, appResponse.getMessage());
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_phone;
    }

    @Override
    protected void initView() {
        txtSecond= (TextView) findViewById(R.id.txt_send_code_msg);
        etPhone= (EditText) findViewById(R.id.et_phone);
        etValidateCode= (EditText) findViewById(R.id.et_validate_code);
    }
}
