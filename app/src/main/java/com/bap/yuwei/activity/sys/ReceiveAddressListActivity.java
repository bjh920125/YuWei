package com.bap.yuwei.activity.sys;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.commonadapter.CommonAdapter;
import com.bap.yuwei.adapter.commonadapter.ViewHolder;
import com.bap.yuwei.entity.event.ReceiverAddressEvent;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.sys.ShippingAddress;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.SysWebService;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceiveAddressListActivity extends BaseActivity {

    private SysWebService sysWebService;
    private ListView mListView;

    private List<ShippingAddress> addressList;

    private CommonAdapter<ShippingAddress> mAdapter;

    private int color;
    private int selectColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sysWebService= MyApplication.getInstance().getWebService(SysWebService.class);
        addressList=new ArrayList<>();
        color=getResources().getColor(R.color.lightblack);
        selectColor=getResources().getColor(R.color.colorPrimary);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShippingAddress shippingAddress= (ShippingAddress) mListView.getItemAtPosition(i);
                EventBus.getDefault().post(new ReceiverAddressEvent(shippingAddress));
                finish();
            }
        });

        mAdapter=new CommonAdapter<ShippingAddress>(mContext,addressList,R.layout.item_address) {
            @Override
            public void convert(ViewHolder viewHolder,final ShippingAddress item) {
                viewHolder.setText(R.id.txt_name,item.getConsignee());
                viewHolder.setText(R.id.txt_tel,item.getCellphone());
                viewHolder.setText(R.id.txt_address,item.getProvince()+item.getCity()+item.getRegion()+item.getStreet());
                Drawable drawable=null;
                if(item.getIsDefault()){
                    viewHolder.setTextWithColor(R.id.txt_default,"默认",selectColor);
                    drawable= getResources().getDrawable(R.drawable.location_fill);
                }else{
                    viewHolder.setTextWithColor(R.id.txt_default,"设为默认",color);
                    drawable= getResources().getDrawable(R.drawable.location);
                }
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                ((TextView)viewHolder.getView(R.id.txt_default)).setCompoundDrawables(drawable,null,null,null);

                viewHolder.setOnClickListener(R.id.txt_default, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setDefaultAddress(item.getShippingAddressId());
                    }
                });

                viewHolder.setOnClickListener(R.id.txt_edit, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(mContext,UpdateReceiveAddressActivity.class);
                        intent.putExtra(ShippingAddress.KEY,item);
                        startActivity(intent);
                    }
                });

                viewHolder.setOnClickListener(R.id.txt_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteAddress(item.getShippingAddressId());
                    }
                });
            }
        };
        mListView.setAdapter(mAdapter);
        getReceiveAddress();
    }

    /**
     * 设置默认收获地址
     */
    private void setDefaultAddress(Long addressId){
        Call<ResponseBody> call=sysWebService.setDefaultAddress(mUser.getUserId(),addressId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        getReceiveAddress();
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
     * 获取收获地址
     */
    private void getReceiveAddress(){
        Call<ResponseBody> call=sysWebService.getReceiveAddress(mUser.getUserId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONArray ja=new JSONObject(result).getJSONArray("result");
                        List<ShippingAddress> tempList=mGson.fromJson(ja.toString(),new TypeToken<List<ShippingAddress>>() {}.getType());
                        addressList.clear();
                        addressList.addAll(tempList);
                        mAdapter.notifyDataSetChanged();
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
     * 删除收获地址
     */
    private void deleteAddress(Long addressId){
        Call<ResponseBody> call=sysWebService.deleteReceiveAddress(mUser.getUserId(),addressId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        getReceiveAddress();
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


    public void addAddress(View v){
        startActivity(new Intent(mContext,AddReceiveAddressActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getReceiveAddress();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_receive_address_list;
    }

    @Override
    protected void initView() {
        mListView= (ListView) findViewById(R.id.lv_address);
    }
}
