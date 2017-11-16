package com.bap.yuwei.activity.order;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseChoosePhotoActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.OrderItem;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.StringUtils;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.OrderWebService;
import com.bap.yuwei.webservice.SysWebService;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RefundApplyActivity extends BaseChoosePhotoActivity {

    private ImageView imgGoods;
    private TextView txtGoods,txtModel;
    private TextView txtReason;
    private EditText etPrice,etRemark;
    private OrderItem orderItem;

    private List<String> imgPathes;
    protected int uploadCount=0;//已经上传的文件计数器

    private OrderWebService orderWebService;
    private SysWebService sysWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgPathes=new ArrayList<>();
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        sysWebService=MyApplication.getInstance().getWebService(SysWebService.class);
        orderItem= (OrderItem) getIntent().getSerializableExtra(OrderItem.KEY);
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+orderItem.getGoodsImage(),imgGoods);
        txtGoods.setText(orderItem.getTitle());
        txtModel.setText("类型"+orderItem.getModel());
        etPrice.setText(orderItem.getPreferentialPrice()+"");
    }



    private void applyRefund(){
        Map<String,Object> params=new HashMap<>();
        params.put("orderItemId",orderItem.getOrderItemId());
        params.put("refundDesc", StringUtils.getEditTextValue(etRemark));
        params.put("refundMoney",StringUtils.getEditTextValue(etPrice));
        params.put("refundReason",StringUtils.getTextViewValue(txtReason));
        params.put("refundType",orderItem.getRefundType());
        params.put("images",imgPathes);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=orderWebService.addRefund(mUser.getUserId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        ToastUtil.showShort(mContext,"申请成功！");
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

    private void updateFile(File file){
        mProgressDialog.show();
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Call<ResponseBody> call = sysWebService.uploadFile(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        mProgressDialog.dismiss();
                        String path=new JSONObject(result).getString("result");
                        imgPathes.add(path);
                        uploadCount++;
                        if(uploadCount==filePaths.size()){
                            applyRefund();
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
                mProgressDialog.dismiss();
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }


    /**
     * 选择取消理由
     */
    int selectIndex=0;
    public void chooseReason(View v){
        final String[] reason=new String[]{"退运费","大小/尺寸与商品描述不符" ,
                "颜色/图案/款式与商品描述不符","材质与商品描述不符","生产日期/保质期与商品描述不符",
                "做工粗糙/有瑕疵","质量问题","少发/漏发","包装/商品破损/污渍/裂痕/变形","未按约定时间发货","发票问题",
                "卖家发错货","其他"};
        new AlertDialog.Builder(mContext)
                .setTitle("请选择退款原因")
                .setSingleChoiceItems(reason, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
                                selectIndex=i;
                            }
                        }
                )
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        txtReason.setText(reason[selectIndex]);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }


    public void applyRefund(View v){
        uploadCount=0;
        for(String ps:getUsefulImagePathes()){
            updateFile(new File(ps));
        }
    }

    @Override
    public void initView() {
        super.initView();
        imgGoods= (ImageView) findViewById(R.id.img_goods);
        txtGoods= (TextView) findViewById(R.id.txt_goods_name);
        txtModel= (TextView) findViewById(R.id.txt_model);
        etPrice= (EditText) findViewById(R.id.et_price);
        etRemark= (EditText) findViewById(R.id.et_remark);
        txtReason= (TextView) findViewById(R.id.txt_reason);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refund_apply;
    }
}
