package com.bap.yuwei.activity.order;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.Express;
import com.bap.yuwei.entity.order.Refund;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.StringUtils;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.OrderWebService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RefundFillExpressActivity extends BaseActivity {

    private TextView txtComName;
    private EditText etExpressNo;

    private Refund refund;
    private List<Express> mExpresses;
    private Express mSelectExpress;

    private OrderWebService orderWebService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        refund= (Refund) getIntent().getSerializableExtra(Refund.KEY);
        mExpresses=new ArrayList<>();
        initComs();
    }

    private void fillExpressInfo(){
        Map<String,Object> params=new HashMap<>();
        params.put("logisticsCompany",mSelectExpress.getCom());
        params.put("logisticsCompanyCode", mSelectExpress.getComCode());
        params.put("refundId",refund.getRefundId());
        params.put("wayBillNo",StringUtils.getTextViewValue(etExpressNo));
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=orderWebService.fillExpressInfo(mUser.getUserId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        ToastUtil.showShort(mContext,"快递信息输入成功！");
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

    /**
     * 选择公司
     */
    int selectIndex=0;
    public void chooseCom(View v){
        final String[] coms=new String[mExpresses.size()];
        for (int i=0;i<mExpresses.size();i++){
            coms[i]=mExpresses.get(i).getCom();
        }
        new AlertDialog.Builder(mContext)
                .setTitle("请选择快递公司")
                .setSingleChoiceItems(coms, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
                                selectIndex=i;
                            }
                        }
                )
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        txtComName.setText(coms[selectIndex]);
                        mSelectExpress=mExpresses.get(selectIndex);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public void fillExpressInfo(View v){
        fillExpressInfo();
    }

    private void initComs(){
        mExpresses.add(new Express("顺丰","shunfeng"));
        mExpresses.add(new Express("申通","shentong"));
        mExpresses.add(new Express("圆通","yuantong"));
        mExpresses.add(new Express("中通","zhongtong"));
        mExpresses.add(new Express("韵达","yunda"));
        mExpresses.add(new Express("EMS","ems"));
        mExpresses.add(new Express("天天快递","tiantian"));
        mExpresses.add(new Express("国通","guotongkuaidi"));
        mExpresses.add(new Express("德邦","debangwuliu"));
        mExpresses.add(new Express("京东","jd"));
        mExpresses.add(new Express("如风达","rufengda"));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refund_fill_express;
    }

    @Override
    protected void initView() {
        txtComName= (TextView) findViewById(R.id.txt_com);
        etExpressNo= (EditText) findViewById(R.id.et_express_no);
    }
}
