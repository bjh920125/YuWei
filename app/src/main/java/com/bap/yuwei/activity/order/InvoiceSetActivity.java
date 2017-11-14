package com.bap.yuwei.activity.order;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.event.ChooseInvoiceEvent;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.InvoiceContent;
import com.bap.yuwei.entity.order.OrderEnsure;
import com.bap.yuwei.entity.order.UserInvoice;
import com.bap.yuwei.entity.sys.Vat;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.SoftInputUtil;
import com.bap.yuwei.util.StringUtils;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.util.area.GetJsonDataUtil;
import com.bap.yuwei.util.area.JsonBean;
import com.bap.yuwei.webservice.OrderWebService;
import com.bap.yuwei.webservice.SysWebService;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private RadioButton rbNoInvoice;
    private EditText etUnitName,etTaxNo;
    private EditText etElecTel,etElecEmail;
    private EditText etCompanyAddress,etCompanyCellphone,etBankAccount,etBankName;
    private EditText etReceiverName,etReceiverTel,etAddress;
    private EditText etVatUnitName,etVatTaxNo;
    private LinearLayout llMakeInvoiceType,llHeader,llUnit,llElecReceiverInfo,llVatInfo;
    private TextView txtCity;

    private OrderEnsure orderEnsure;
    private List<InvoiceContent> invoiceContents;
    private String selectContent="";
    private String province,city,area;
    private UserInvoice mUserInvoice;
    private Long userInvoiceId;

    private int type=Constants.INVOICE_COMMON;
    private int headerType= Constants.INVOICE_HEADER_PERSONAL;

    protected ArrayList<JsonBean> options1Items = new ArrayList<>();
    protected ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    protected ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

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
        setType();

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
                switch (id){
                    case R.id.rb_paper:
                        type=Constants.INVOICE_COMMON;
                        rgHeaderType.check(R.id.rb_person);
                        break;
                    case R.id.rb_elec:
                        type=Constants.INVOICE_ELEC;
                        rgHeaderType.check(R.id.rb_person);
                        break;
                    case R.id.rb_vat:
                        type=Constants.INVOICE_VAT;
                        headerType=Constants.INVOICE_HEADER_UNIT;
                        break;
                    default:break;
                }
                showUIByType();
                getInvoiceByType();
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
                getInvoiceByType();
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
        initJsonData();
    }


    public void saveInvoice(View v){
        addOrUpdateInvoice();
    }

    private UserInvoice getUserInvoiceEntity(){
        UserInvoice userInvoice=new UserInvoice();
        userInvoice.setUserInvoiceId(userInvoiceId);
        userInvoice.setType(type);
        userInvoice.setHeaderType(headerType);
        userInvoice.setContent(selectContent);
        if(type==Constants.INVOICE_COMMON){
            if(headerType==Constants.INVOICE_HEADER_UNIT){
                userInvoice.setHeader(StringUtils.getEditTextValue(etUnitName));
                userInvoice.setCompanyName(StringUtils.getEditTextValue(etUnitName));
                userInvoice.setTaxpayerNumber(StringUtils.getEditTextValue(etTaxNo));
            }else {
                userInvoice.setHeader("个人");
            }
        }else if (type==Constants.INVOICE_ELEC){
            userInvoice.setCellphone(StringUtils.getEditTextValue(etElecTel));
            userInvoice.setEmail(StringUtils.getEditTextValue(etElecEmail));
            if(headerType==Constants.INVOICE_HEADER_UNIT){
                userInvoice.setHeader(StringUtils.getEditTextValue(etUnitName));
                userInvoice.setCompanyName(StringUtils.getEditTextValue(etUnitName));
                userInvoice.setTaxpayerNumber(StringUtils.getEditTextValue(etTaxNo));
            }else {
                userInvoice.setHeader("个人");
            }
        }else if (type==Constants.INVOICE_VAT){
            userInvoice.setInvoiceMode(0);//0：订单完成后开票;
            userInvoice.setHeader(StringUtils.getEditTextValue(etVatUnitName));
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
                        JSONObject jo=new JSONObject(result);
                        userInvoiceId=jo.getLong("result");
                        EventBus.getDefault().post(new ChooseInvoiceEvent(getUserInvoiceEntity()));
                        finish();
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
                        Vat vat=mGson.fromJson(jo.toString(),Vat.class);
                        initUIWithVat(vat);
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

    private void initUIWithVat(Vat vat){
        if(null==vat) return;
        etVatUnitName.setText(vat.getCompanyName());
        etVatTaxNo.setText(vat.getTaxpayerNo());
        etCompanyCellphone.setText(vat.getCellphone());
        etCompanyAddress.setText(vat.getAddress());
        etBankAccount.setText(vat.getBankAccount());
        etBankName.setText(vat.getBankName());
    }

    private void initUIWithValues(){
        if(null==mUserInvoice) return;
        userInvoiceId=mUserInvoice.getUserInvoiceId();
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

        String content=mUserInvoice.getContent();
        for(int i=0;i<rgInvoiceContent.getChildCount();i++){
            RadioButton btn= (RadioButton) rgInvoiceContent.getChildAt(i);
            if(btn.getText().toString().equals(content)){
                rgInvoiceContent.check(btn.getId());
            }
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
        if(null!=mUserInvoice.getProvince()){
            province=mUserInvoice.getProvince();
            city=mUserInvoice.getCity();
            area=mUserInvoice.getArea();
            txtCity.setText(mUserInvoice.getProvince()+mUserInvoice.getCity()+mUserInvoice.getArea());
        }
        etAddress.setText(mUserInvoice.getAddress());
        if(TextUtils.isEmpty(mUserInvoice.getBankAccount())){
            getVat();
        }
    }

    private void showUIByType(){
        if(type==Constants.INVOICE_COMMON){
            llMakeInvoiceType.setVisibility(View.GONE);
            llHeader.setVisibility(View.VISIBLE);
            llElecReceiverInfo.setVisibility(View.GONE);
            llVatInfo.setVisibility(View.GONE);
            rbNoInvoice.setVisibility(View.VISIBLE);
        }else if (type==Constants.INVOICE_ELEC){
            llMakeInvoiceType.setVisibility(View.GONE);
            llHeader.setVisibility(View.VISIBLE);
            llElecReceiverInfo.setVisibility(View.VISIBLE);
            llVatInfo.setVisibility(View.GONE);
            rbNoInvoice.setVisibility(View.GONE);
        }else if (type==Constants.INVOICE_VAT){
            llMakeInvoiceType.setVisibility(View.VISIBLE);
            llHeader.setVisibility(View.GONE);
            llElecReceiverInfo.setVisibility(View.GONE);
            llVatInfo.setVisibility(View.VISIBLE);
            rbNoInvoice.setVisibility(View.GONE);
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

    private void setType(){
        String[] types=orderEnsure.getInvoiceTypes();
        for(int i=0;i<types.length;i++){
            if(types[i].equals("0")){
                RadioButton btn= (RadioButton) rgType.getChildAt(i);
                btn.setEnabled(false);
                btn.setTextColor(getResources().getColor(R.color.darkgrey));
                btn.setBackgroundResource(R.drawable.checked_darkgrey_bg);
            }
        }
    }


    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(new ChooseInvoiceEvent(getUserInvoiceEntity()));
        finish();
    }

    public void chooseArea(View v) {// 弹出选择器
        SoftInputUtil.hideKeyboard(mContext);
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                province=options1Items.get(options1).getPickerViewText();
                city=options2Items.get(options1).get(options2);
                area=options3Items.get(options1).get(options2).get(options3);
                txtCity.setText(province+city+area);
            }
        }).setTitleText("城市选择").setDividerColor(Color.BLACK)
                .setCancelText("取消").setSubmitText("确定")
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器
        pvOptions.show();
    }

    private void initJsonData() {//解析数据
        String JsonData = new GetJsonDataUtil().getJson(this,"province.json");//获取assets目录下的json文件数据
        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体
        options1Items = jsonBean;
        for (int i=0;i<jsonBean.size();i++){//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）
            for (int c=0; c<jsonBean.get(i).getCityList().size(); c++){//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表
                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        ||jsonBean.get(i).getCityList().get(c).getArea().size()==0) {
                    City_AreaList.add("");
                }else {
                    for (int d=0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);
                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }
            //添加城市数据
            options2Items.add(CityList);
            //添加地区数据
            options3Items.add(Province_AreaList);
        }
    }

    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
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
        rbNoInvoice= (RadioButton) findViewById(R.id.rb_no_invoice);
        llMakeInvoiceType= (LinearLayout) findViewById(R.id.ll_make_invoice_type);
        llHeader= (LinearLayout) findViewById(R.id.ll_header);
        llUnit= (LinearLayout) findViewById(R.id.ll_unit);
        llElecReceiverInfo= (LinearLayout) findViewById(R.id.ll_elec_receiver_info);
        llVatInfo= (LinearLayout) findViewById(R.id.ll_vat_info);
        etUnitName= (EditText) findViewById(R.id.et_unit);
        etTaxNo= (EditText) findViewById(R.id.et_tax_no);
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
        txtCity=(TextView) findViewById(R.id.et_receiver_city);
        etAddress=(EditText) findViewById(R.id.et_receiver_address);
    }
}
