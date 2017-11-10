package com.bap.yuwei.activity.order;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.InvoiceContent;
import com.bap.yuwei.entity.order.OrderEnsure;
import com.bap.yuwei.entity.order.UserInvoice;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.StringUtils;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.OrderWebService;
import com.bap.yuwei.webservice.SysWebService;

import org.json.JSONObject;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceSetActivity extends BaseActivity {

    private RadioGroup rgType;
    private RadioGroup rgHeaderType;
    private RadioGroup rgInvoiceContent;
    private EditText etUnitName,etTaxNo;
    private EditText etElecTel,etElecEmail;
    private EditText etCompanyAddress,etCompanyCellphone,etBankAccount,etBankName;
    private EditText etReceiverName,etReceiverTel,etCity,etAddress;
    private EditText etVatUnitName,etVatTaxNo;
    private LinearLayout llMakeInvoiceType,llHeader,llUnit,llElecReceiverInfo,llVatInfo;

    private OrderEnsure orderEnsure;
    private List<InvoiceContent> invoiceContents;
    private String selectContent="";
    private String province,city,area;
    private UserInvoice mUserInvoice;

    private int type=Constants.INVOICE_COMMON;
    private int headerType= Constants.INVOICE_HEADER_PERSONAL;

    private OrderWebService orderWebService;
    private SysWebService webService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderEnsure= (OrderEnsure) getIntent().getSerializableExtra(OrderEnsure.KEY);
        invoiceContents=orderEnsure.getInvoiceContents();
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        webService = MyApplication.getInstance().getWebService(SysWebService.class);
        setContents();

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
                switch (id){
                    case R.id.rb_paper:
                        type=Constants.INVOICE_COMMON;
                        break;
                    case R.id.rb_elec:
                        type=Constants.INVOICE_ELEC;
                        break;
                    case R.id.rb_vat:
                        type=Constants.INVOICE_VAT;
                        break;
                    default:break;
                }
                showUIByType();
            }
        });

        rgHeaderType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
                switch (id){
                    case R.id.rb_person:
                        headerType= Constants.INVOICE_HEADER_PERSONAL;
                        break;
                    case R.id.rb_unit:
                        headerType=Constants.INVOICE_HEADER_UNIT;
                        break;
                    default:break;
                }
                showUIByHeaderType();
            }
        });

        rgInvoiceContent.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                RadioButton radioButton= (RadioButton) findViewById(i);
                selectContent=radioButton.getText().toString();
            }
        });

        showUIByType();
        showUIByHeaderType();
        getInvoiceByType();
    }


    public void saveInvoice(View v){
        addOrUpdateInvoice();
    }

    private UserInvoice getUserInvoiceEntity(){
        UserInvoice userInvoice=new UserInvoice();
        userInvoice.setType(type);
        userInvoice.setHeaderType(headerType);
        userInvoice.setContent(selectContent);
        if(type==Constants.INVOICE_COMMON){
            if(headerType==Constants.INVOICE_HEADER_UNIT){
                userInvoice.setCompanyName(StringUtils.getEditTextValue(etUnitName));
                userInvoice.setTaxpayerNumber(StringUtils.getEditTextValue(etTaxNo));
            }
        }else if (type==Constants.INVOICE_ELEC){
            userInvoice.setCellphone(StringUtils.getEditTextValue(etElecTel));
            userInvoice.setEmail(StringUtils.getEditTextValue(etElecEmail));
            if(headerType==Constants.INVOICE_HEADER_UNIT){
                userInvoice.setCompanyName(StringUtils.getEditTextValue(etUnitName));
                userInvoice.setTaxpayerNumber(StringUtils.getEditTextValue(etTaxNo));
            }
        }else if (type==Constants.INVOICE_VAT){
            userInvoice.setInvoiceMode(0);//0：订单完成后开票;
            userInvoice.setCompanyName(StringUtils.getEditTextValue(etVatUnitName));
            userInvoice.setTaxpayerNumber(StringUtils.getEditTextValue(etVatTaxNo));
            userInvoice.setCompanyAddress(StringUtils.getEditTextValue(etCompanyAddress));
            userInvoice.setCompanyCellphone(StringUtils.getEditTextValue(etCompanyCellphone));
            userInvoice.setBankName(StringUtils.getEditTextValue(etBankName));
            userInvoice.setBankAccount(StringUtils.getEditTextValue(etBankAccount));
            userInvoice.setName(StringUtils.getEditTextValue(etReceiverName));
            userInvoice.setCellphone(StringUtils.getEditTextValue(etReceiverTel));
            userInvoice.setProvince(province);
            userInvoice.setCity(city);
            userInvoice.setArea(area);
            userInvoice.setAddress(StringUtils.getEditTextValue(etAddress));
        }
        return userInvoice;
    }

    /**
     * 新增或修改发票
     */
    private void addOrUpdateInvoice(){
        showLoadingDialog();
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(getUserInvoiceEntity()));
        Call<ResponseBody> call=orderWebService.addUserInvoice(mUser.getUserId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");

                    }else{
                        ToastUtil.showShort(mContext,appResponse.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dismissProgressDialog();
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }

    /**
     * 查询发票模板
     */
    private void getInvoiceByType(){
        showLoadingDialog();
        Call<ResponseBody> call=orderWebService.getInvoiceByType(mUser.getUserId(),type,headerType);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        mUserInvoice=mGson.fromJson(jo.toString(),UserInvoice.class);
                        initUIWithValues();
                    }else{
                        ToastUtil.showShort(mContext,appResponse.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dismissProgressDialog();
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }

    private void getVat(){
        if(null==mUser) return;
        Call<ResponseBody> call=webService.getVat(mUser.getUserId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        //mVat=mGson.fromJson(jo.toString(),Vat.class);
                        initUIWithValues();
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


    private void initUIWithValues(){
        if(null==mUserInvoice) return;
        int type=mUserInvoice.getType();
        if(type==Constants.INVOICE_COMMON){
            rgType.check(R.id.rb_paper);
        }else if(type==Constants.INVOICE_ELEC){
            rgType.check(R.id.rb_elec);
        }else if(type==Constants.INVOICE_VAT){
            rgType.check(R.id.rb_vat);
        }
        int headerType=mUserInvoice.getHeaderType();
        if(headerType==Constants.INVOICE_HEADER_UNIT){
            rgHeaderType.check(R.id.rb_unit);
        }else {
            rgHeaderType.check(R.id.rb_person);
        }

        etUnitName.setText(mUserInvoice.getCompanyName());
        etTaxNo.setText(mUserInvoice.getTaxpayerNumber());
        etElecTel.setText(mUserInvoice.getCellphone());
        etElecEmail.setText(mUserInvoice.getEmail());
        etVatUnitName.setText(mUserInvoice.getCompanyName());
        etVatTaxNo.setText(mUserInvoice.getTaxpayerNumber());
        etCompanyAddress.setText(mUserInvoice.getCompanyAddress());
        etCompanyCellphone.setText(mUserInvoice.getCompanyCellphone());
        etBankAccount.setText(mUserInvoice.getBankAccount());
        etBankName.setText(mUserInvoice.getBankName());
        etReceiverName.setText(mUserInvoice.getName());
        etReceiverTel.setText(mUserInvoice.getCellphone());
        etCity.setText(mUserInvoice.getProvince()+mUserInvoice.getCity()+mUserInvoice.getAddress());
        etAddress.setText(mUserInvoice.getAddress());
    }

    private void showUIByType(){
        if(type==Constants.INVOICE_COMMON){
            llMakeInvoiceType.setVisibility(View.GONE);
            llHeader.setVisibility(View.VISIBLE);
            llElecReceiverInfo.setVisibility(View.GONE);
            llVatInfo.setVisibility(View.GONE);
        }else if (type==Constants.INVOICE_ELEC){
            llMakeInvoiceType.setVisibility(View.GONE);
            llHeader.setVisibility(View.VISIBLE);
            llElecReceiverInfo.setVisibility(View.VISIBLE);
            llVatInfo.setVisibility(View.GONE);
        }else if (type==Constants.INVOICE_VAT){
            llMakeInvoiceType.setVisibility(View.VISIBLE);
            llHeader.setVisibility(View.GONE);
            llElecReceiverInfo.setVisibility(View.GONE);
            llVatInfo.setVisibility(View.VISIBLE);
        }
    }

    private void showUIByHeaderType(){
        if(headerType==Constants.INVOICE_HEADER_PERSONAL){
            llUnit.setVisibility(View.GONE);
        }else if (headerType==Constants.INVOICE_HEADER_UNIT){
            llUnit.setVisibility(View.VISIBLE);
        }
    }

    private void setContents(){
        LayoutInflater inflater=LayoutInflater.from(mContext);
        RadioButton rb;
        for(InvoiceContent ic:invoiceContents){
            rb = (RadioButton) inflater.inflate(R.layout.item_invoice_content,null);
            rb.setText(ic.getContent());
            rb.setTag(ic);
            rgInvoiceContent.addView(rb);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_invoice_set;
    }

    @Override
    protected void initView() {
        rgType=(RadioGroup) findViewById(R.id.rg_type);
        rgInvoiceContent= (RadioGroup) findViewById(R.id.rg_invoice_content);
        rgHeaderType= (RadioGroup) findViewById(R.id.rg_header_type);
        llMakeInvoiceType= (LinearLayout) findViewById(R.id.ll_make_invoice_type);
        llHeader= (LinearLayout) findViewById(R.id.ll_header);
        llUnit= (LinearLayout) findViewById(R.id.ll_unit);
        llElecReceiverInfo= (LinearLayout) findViewById(R.id.ll_elec_receiver_info);
        llVatInfo= (LinearLayout) findViewById(R.id.ll_vat_info);
        etUnitName= (EditText) findViewById(R.id.et_unit);
        etTaxNo= (EditText) findViewById(R.id.et_unit);
        etElecTel=  (EditText) findViewById(R.id.et_elec_tel);
        etElecEmail =(EditText) findViewById(R.id.et_elec_email);
        etVatUnitName=(EditText) findViewById(R.id.et_vat_unit);
        etVatTaxNo=(EditText) findViewById(R.id.et_vat_tax_no);
        etCompanyAddress=(EditText) findViewById(R.id.et_com_address);
        etCompanyCellphone=(EditText) findViewById(R.id.et_com_tel);
        etBankAccount=(EditText) findViewById(R.id.et_bank_account);
        etBankName=(EditText) findViewById(R.id.et_bank);
        etReceiverName=(EditText) findViewById(R.id.et_receiver_name);
        etReceiverTel=(EditText) findViewById(R.id.et_receiver_tel);
        etCity=(EditText) findViewById(R.id.et_receiver_city);
        etAddress=(EditText) findViewById(R.id.et_receiver_address);
    }
}
