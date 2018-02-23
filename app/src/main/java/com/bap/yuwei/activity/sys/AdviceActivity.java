package com.bap.yuwei.activity.sys;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

/**
 * 意见反馈
 */
public class AdviceActivity extends BaseActivity {

    private Button btnAdvice,btnBuyQuestion,btnQuestion,btnOther;
    private Button[] btns;
    private EditText etContent;
    private String title;

    private SysWebService sysWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sysWebService= MyApplication.getInstance().getWebService(SysWebService.class);
    }


    public void chooseTitle(View v){
        int index=-1;
        switch (v.getId()){
            case R.id.btn_advice:
                index=0;
                break;
            case R.id.btn_buy_question:
                index=1;
                break;
            case R.id.btn_use_question:
                index=2;
                break;
            case R.id.btn_other:
                index=3;
                break;
            default:break;
        }
        for(Button btn:btns){
            btn.setSelected(false);
        }
        btns[index].setSelected(true);
        title=btns[index].getText().toString();
    }

    /**
     * 评价
     */
    public void commit(View v){
        Map<String,Object> params=new HashMap<>();
        params.put("userName", null!=mUser ? mUser.getUsername() : "");
        params.put("telphone",null!=mUser ? mUser.getPhone() : "");
        params.put("title",title);
        params.put("content",StringUtils.getEditTextValue(etContent));
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=sysWebService.addAdvice(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        ToastUtil.showShort(mContext,"反馈成功！");
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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_advice;
    }

    @Override
    protected void initView() {
        etContent= (EditText) findViewById(R.id.et_content);
        btnAdvice= (Button) findViewById(R.id.btn_advice);
        btnBuyQuestion= (Button) findViewById(R.id.btn_buy_question);
        btnQuestion= (Button) findViewById(R.id.btn_use_question);
        btnOther= (Button) findViewById(R.id.btn_other);
        btns=new Button[]{btnAdvice,btnBuyQuestion,btnQuestion,btnOther};
        btnAdvice.setSelected(true);
        title=btnAdvice.getText().toString();
    }
}
