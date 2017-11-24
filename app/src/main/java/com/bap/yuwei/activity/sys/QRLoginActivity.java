package com.bap.yuwei.activity.sys;

import android.os.Bundle;
import android.view.View;
import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.SysWebService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QRLoginActivity extends BaseActivity {

    private String token;
    private SysWebService sysWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sysWebService = MyApplication.getInstance().getWebService(SysWebService.class);
        token=getIntent().getStringExtra(Constants.TOKEN_KEY);
    }

    public void qrLogin(View v){
        showLoadingDialog();
        Call<ResponseBody> call=sysWebService.login(mUser.getUserId(),token);
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

    public void onBackClick(View v){
        finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_qrlogin;
    }

    @Override
    protected void initView() {

    }
}
