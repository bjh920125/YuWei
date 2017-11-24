package com.bap.yuwei.activity.sys;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MD5Utils;
import com.bap.yuwei.util.MyApplication;
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

public class RegisterActivity extends BaseActivity {

    private TextView txtPeronalUser,txtComUser;
    private View viewPeronalUser,viewComUser;
    private LinearLayout llPerson;
    private TextView txtCom;
    private EditText etName,etPhone,etValidateCode,etPwd,etConfirmPwd;

    private TextView txtSecond;
    private String msgId;
    protected Timer timer;
    protected int restSecond=60;
    private SysWebService webService;

    private int color;
    private int selectColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webService = MyApplication.getInstance().getWebService(SysWebService.class);
        color=getResources().getColor(R.color.lightblack);
        selectColor=getResources().getColor(R.color.colorPrimary);
    }

    public void regist(){
        showLoadingDialog();
        String password= MD5Utils.encode(StringUtils.getEditTextValue(etPwd)).toLowerCase();
        Map<String,Object> params=new HashMap<>();
        params.put("username", StringUtils.getEditTextValue(etName));
        params.put("password", password);
        params.put("phone",StringUtils.getEditTextValue(etPhone));
        params.put("code",StringUtils.getEditTextValue(etValidateCode));
        params.put("msgId",msgId);
        params.put("deviceType", Constants.DEVICE_TYPE);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=webService.register(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        ToastUtil.showShort(mContext,"注册成功！");
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

    public void chooseUserType(View v){
        switch (v.getId()){
            case R.id.txt_person_user:
                showPersonalView();
                break;
            case R.id.txt_com_user:
                showComView();
                break;
            default:break;
        }
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
            ToastUtil.showShort(mContext,"请输入手机号码！");
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
        String pwd= StringUtils.getEditTextValue(etPwd);
        String confirmPwd=StringUtils.getEditTextValue(etConfirmPwd);
        if(!pwd.equals(confirmPwd)){
            ToastUtil.showShort(mContext,"两次输入的密码不一致！");
            return;
        }

        String code= StringUtils.getEditTextValue(etValidateCode);
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
                        regist();
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


    private void showPersonalView(){
        txtPeronalUser.setTextColor(selectColor);
        txtComUser.setTextColor(color);
        viewPeronalUser.setVisibility(View.VISIBLE);
        viewComUser.setVisibility(View.INVISIBLE);
        llPerson.setVisibility(View.VISIBLE);
        txtCom.setVisibility(View.GONE);
    }

    private void showComView(){
        txtPeronalUser.setTextColor(color);
        txtComUser.setTextColor(selectColor);
        viewPeronalUser.setVisibility(View.INVISIBLE);
        viewComUser.setVisibility(View.VISIBLE);
        llPerson.setVisibility(View.GONE);
        txtCom.setVisibility(View.VISIBLE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        txtPeronalUser= (TextView) findViewById(R.id.txt_person_user);
        txtComUser= (TextView) findViewById(R.id.txt_com_user);
        viewPeronalUser=findViewById(R.id.view_person_user);
        viewComUser=findViewById(R.id.view_com_user);
        llPerson= (LinearLayout) findViewById(R.id.ll_person);
        txtCom=(TextView) findViewById(R.id.txt_com);
        etName= (EditText) findViewById(R.id.et_user_name);
        etPhone= (EditText) findViewById(R.id.et_phone);
        etValidateCode= (EditText) findViewById(R.id.et_validate_code);
        etPwd= (EditText) findViewById(R.id.et_pwd);
        etConfirmPwd= (EditText) findViewById(R.id.et_confirm_pwd);
        txtSecond= (TextView) findViewById(R.id.txt_send_code_msg);
    }
}
