package com.bap.yuwei.activity.sys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.event.UserInfoEvent;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MediaUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.SharedPreferencesUtil;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.SysWebService;

import org.greenrobot.eventbus.EventBus;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends BaseActivity {

    private TextView txtLogout;

    private SysWebService webService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webService = MyApplication.getInstance().getWebService(SysWebService.class);
        if(null==mUser){
            txtLogout.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 退出登录
     */
    public void loginout(View v){
        Call<ResponseBody> call=webService.loginout();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        SharedPreferencesUtil.putString(mContext, Constants.USER_KEY,null);
                        SharedPreferencesUtil.putString(mContext, Constants.TOKEN_KEY,null);
                        startActivity(new Intent(mContext,LoginActivity.class));
                        EventBus.getDefault().post(new UserInfoEvent());
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

    public void onMenuClick(View v){
        switch (v.getId()){
            case R.id.txt_user_info:
                if(isLogined())
                startActivity(new Intent(mContext,UserInfoActivity.class));
                break;
            case R.id.txt_security:
                if(isLogined())
                startActivity(new Intent(mContext,AccountMenusActivity.class));
                break;
            case R.id.txt_msg:
                startActivity(new Intent(mContext,MsgSetActivity.class));
                break;
            case R.id.txt_clear:
                clearCache();
                break;
            case R.id.txt_service:
                break;
            case R.id.txt_feedback:
                startActivity(new Intent(mContext,AdviceActivity.class));
                break;
            case R.id.txt_about:
                break;
            default:break;
        }
    }


    public void clearCache(){
        MediaUtil.deleteFilesByDirectory(mContext.getCacheDir(), mContext);//删除缓存
        showProgressDialog("正在清除...",true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissProgressDialog();
                ToastUtil.showShort(mContext, "已成功清除缓存！");
            }
        }, 2500);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        txtLogout= (TextView) findViewById(R.id.txt_logout);
    }
}
