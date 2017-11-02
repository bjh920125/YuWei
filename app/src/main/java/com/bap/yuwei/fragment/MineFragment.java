package com.bap.yuwei.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.sys.LoginActivity;
import com.bap.yuwei.activity.sys.MsgMenusActivity;
import com.bap.yuwei.activity.sys.SettingActivity;
import com.bap.yuwei.activity.sys.UserInfoActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.fragment.base.BaseFragment;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.GoodsWebService;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/10/27.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener{

    private RelativeLayout rlPersonInfo;
    private ImageView imgHead;
    private TextView txtName;
    private TextView txtGoodsCollectNum,txtShopCollectNum,txtFootmarkNum;
    private ImageView imgSet,imgMsg;

    private GoodsWebService goodsWebService;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsWebService= MyApplication.getInstance().getWebService(GoodsWebService.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshUI();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_person_info:
                loginOrUpdateInfo();
                break;
            case R.id.img_set:
                startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.img_msg:
                if(isLogined())
                    startActivity(new Intent(mContext, MsgMenusActivity.class));
                break;
            default:break;
        }
    }

    private void loginOrUpdateInfo(){
        if(null!=mUser){
            startActivity(new Intent(mContext, UserInfoActivity.class));
        }else {
            startActivity(new Intent(mContext, LoginActivity.class));
        }
    }


    /**
     * 获取收藏量
     */
    private void getCollectNum(){
        if(null==mUser) return;
        Call<ResponseBody> call=goodsWebService.getCollectNum(mUser.getUserId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONObject jo=new JSONObject(result).getJSONObject("result");
                        txtGoodsCollectNum.setText(jo.getInt("goodsCollectNum")+"");
                        txtShopCollectNum.setText(jo.getInt("shopCollectNum")+"");
                        txtFootmarkNum.setText(jo.getInt("distinctUserGoodsHistoryNum")+"");
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

    public void refreshUI() {
        if(null != mUser){
            txtName.setText(mUser.getUsername());
            ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+mUser.getAvatar(),imgHead, DisplayImageOptionsUtil.getOptions());
            getCollectNum();
        }else{
            txtName.setText("点击登录");
            ImageLoader.getInstance().displayImage("drawable://"+R.drawable.iconfont_touxiang,imgHead, DisplayImageOptionsUtil.getOptions());
            txtGoodsCollectNum.setText("0");
            txtShopCollectNum.setText("0");
            txtFootmarkNum.setText("0");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshUI();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_mine, container, false);
        rlPersonInfo= (RelativeLayout) fragmentView.findViewById(R.id.rl_person_info);
        imgHead= (ImageView) fragmentView.findViewById(R.id.img_head);
        txtName= (TextView) fragmentView.findViewById(R.id.txt_name);
        txtGoodsCollectNum= (TextView) fragmentView.findViewById(R.id.txt_goods_collect_num);
        txtShopCollectNum= (TextView) fragmentView.findViewById(R.id.txt_shop_collect_num);
        txtFootmarkNum= (TextView) fragmentView.findViewById(R.id.txt_footmark_num);
        imgSet= (ImageView) fragmentView.findViewById(R.id.img_set);
        imgMsg= (ImageView) fragmentView.findViewById(R.id.img_msg);
        rlPersonInfo.setOnClickListener(this);
        txtGoodsCollectNum.setOnClickListener(this);
        txtShopCollectNum.setOnClickListener(this);
        txtFootmarkNum.setOnClickListener(this);
        imgSet.setOnClickListener(this);
        imgMsg.setOnClickListener(this);
        return fragmentView;
    }


    @Override
    public void initView() {
        refreshUI();
    }
}
