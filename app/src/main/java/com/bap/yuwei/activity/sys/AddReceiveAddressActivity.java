package com.bap.yuwei.activity.sys;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.sys.ShippingAddress;
import com.bap.yuwei.util.area.GetJsonDataUtil;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.StringUtils;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.util.area.JsonBean;
import com.bap.yuwei.webservice.SysWebService;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddReceiveAddressActivity extends BaseActivity {

    protected EditText etName,etTel,etAddress;
    protected TextView txtArea;
    protected Checkable cbDefault;

    protected String province,city,region;

    protected ArrayList<JsonBean> options1Items = new ArrayList<>();
    protected ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    protected ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    protected SysWebService sysWebService;

    protected ShippingAddress address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sysWebService= MyApplication.getInstance().getWebService(SysWebService.class);
        address= (ShippingAddress) getIntent().getSerializableExtra(ShippingAddress.KEY);
        initJsonData();
        initUiWithValue();
    }

    public void addAddress(View v){
        Map<String,Object> params=new HashMap<>();
        params.put("cellphone", StringUtils.getEditTextValue(etTel));
        params.put("city", city);
        params.put("consignee",StringUtils.getEditTextValue(etName));
        params.put("isDefault",cbDefault.isChecked()? true:false);
        params.put("province",province);
        params.put("region",region);
        params.put("street",StringUtils.getEditTextValue(etAddress));
        params.put("userId",mUser.getUserId());
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call=sysWebService.addReceiveAddress(mUser.getUserId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }


    public void chooseArea(View v) {// 弹出选择器
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                province=options1Items.get(options1).getPickerViewText();
                city=options2Items.get(options1).get(options2);
                region=options3Items.get(options1).get(options2).get(options3);
                txtArea.setText(province+city+region);
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

    protected void initUiWithValue(){};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_receive_address;
    }

    @Override
    protected void initView() {
        etName= (EditText) findViewById(R.id.et_name);
        etTel= (EditText) findViewById(R.id.et_tel);
        etAddress= (EditText) findViewById(R.id.et_address);
        txtArea= (TextView) findViewById(R.id.txt_area);
        cbDefault= (Checkable) findViewById(R.id.cb_default);
    }
}
