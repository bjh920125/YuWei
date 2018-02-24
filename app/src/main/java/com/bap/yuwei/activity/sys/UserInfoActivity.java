package com.bap.yuwei.activity.sys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.event.UserInfoEvent;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.sys.User;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.SharedPreferencesUtil;
import com.bap.yuwei.util.StringUtils;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.view.ActionSheet;
import com.bap.yuwei.webservice.SysWebService;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pizidea.imagepicker.AndroidImagePicker;
import com.pizidea.imagepicker.bean.ImageItem;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
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

/**
 * 个人信息
 */
public class UserInfoActivity extends BaseActivity  implements ActionSheet.ActionSheetListener {

    private ImageView imgHead;
    private EditText etName;
    private TextView txtGender;
    private int gender;
    private String avatar;

    private ActionSheet.Builder mActionSheetBuilder;

    private SysWebService sysWebService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sysWebService= MyApplication.getInstance().getWebService(SysWebService.class);
        getUserInfo();
    }

    @Override
    public void onBackPressed() {
        updateUserInfo();
    }

    /**
     * 上传附件
     */
    private void uploadFile(File file){
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
                        avatar=new JSONObject(result).getString("result");
                        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+avatar,imgHead, DisplayImageOptionsUtil.getOptionsRounded(360));
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
     * 修改用户信息
     */
    private void updateUserInfo(){
        Map<String,Object> params=new HashMap<>();
        params.put("nickname", StringUtils.getEditTextValue(etName));
        params.put("gender", gender);
        params.put("avatar",avatar);
        RequestBody body=RequestBody.create(jsonMediaType,mGson.toJson(params));
        Call<ResponseBody> call = sysWebService.updateUserInfo(mUser.getUserId(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        mUser.setAvatar(avatar);
                        mUser.setNickname(StringUtils.getEditTextValue(etName));
                        mUser.setGender(gender);
                        SharedPreferencesUtil.putString(mContext,Constants.USER_KEY,mGson.toJson(mUser));
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
     * 获取用户信息
     */
    private void getUserInfo(){
        Call<ResponseBody> call = sysWebService.getUserInfo(mUser.getUserId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        mUser=mGson.fromJson(jo.toString(),User.class);
                        refreshUI();
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
     * 刷新UI
     */
    public void refreshUI() {
        if(null != mUser){
            etName.setText(mUser.getUsername());
            ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+mUser.getAvatar(),imgHead,DisplayImageOptionsUtil.getOptionsRounded(360));
            avatar=mUser.getAvatar();
            int gender=mUser.getGender();
            if(gender==0){
                gender=0;
                txtGender.setText("男");
            }else if(gender==1){
                gender=1;
                txtGender.setText("女");
            }else{
                gender=2;
                txtGender.setText("保密");
            }
        }
    }

    /**
     * 选择照片
     */
    public void chooseImage(View v){
        AndroidImagePicker.getInstance().pickSingle(this, true, new AndroidImagePicker.OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(List<ImageItem> items) {
                if (items != null && items.size() > 0) {
                    File file=new File(items.get(0).path);
                    uploadFile(file);
                }
            }
        });
    }

    /**
     * 选择性别
     */
    public void chooseGender(View v){
        mActionSheetBuilder.show();
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch(index){
            case 0:
                gender=0;
                txtGender.setText("男");
                break;
            case 1:
                gender=1;
                txtGender.setText("女");
                break;
            case 2:
                gender=2;
                txtGender.setText("保密");
                break;
            default:break;
        }

    }

    /**
     * 更新用户信息后接收的event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUserInfo(UserInfoEvent userInfoEvent){
        String userJson= SharedPreferencesUtil.getString(mContext, Constants.USER_KEY);
        if(null!=userJson) {
            mUser = mGson.fromJson(userJson, User.class);
            refreshUI();
        }
    }

    /**
     * 新增收货地址
     */
    public void addAddress(View v){
        startActivity(new Intent(mContext,ReceiveAddressListActivity.class));
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {}

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initView() {
        imgHead= (ImageView) findViewById(R.id.img_head);
        etName= (EditText) findViewById(R.id.et_name);
        txtGender=(TextView) findViewById(R.id.txt_gender);

        mActionSheetBuilder=ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("男","女","保密")
                .setCancelableOnTouchOutside(true)
                .setListener(this);
    }
}
