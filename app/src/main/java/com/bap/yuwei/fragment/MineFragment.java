package com.bap.yuwei.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.goods.FootmarkActivity;
import com.bap.yuwei.activity.goods.MyCollectListActivity;
import com.bap.yuwei.activity.order.OrderListActivity;
import com.bap.yuwei.activity.order.RefundListActivity;
import com.bap.yuwei.activity.sys.LoginActivity;
import com.bap.yuwei.activity.sys.MsgMenusActivity;
import com.bap.yuwei.activity.sys.SettingActivity;
import com.bap.yuwei.activity.sys.UserInfoActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.event.UnreadEvent;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.entity.order.OrderStatistics;
import com.bap.yuwei.fragment.base.BaseFragment;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.GoodsWebService;
import com.bap.yuwei.webservice.OrderWebService;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 我的
 */
public class MineFragment extends BaseFragment implements View.OnClickListener{

    private RelativeLayout rlPersonInfo;
    private LinearLayout llAllOrders;
    private ImageView imgHead;
    private TextView txtName;
    private TextView txtGoodsCollectNum,txtShopCollectNum,txtFootmarkNum;
    private TextView txtMsgCount;
    private ImageView imgSet,imgMsg;
    private TextView btnPay,btnSend,btnReceive,btnComment,btnRefund;
    private TextView txtPayNum,txtSendNum,txtReceiveNum,txtCommentNum,txtRefundNum;
    private RelativeLayout rlGoodsCollect,rlShopCollect,rlFootMark;

    private GoodsWebService goodsWebService;
    private OrderWebService orderWebService;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsWebService= MyApplication.getInstance().getWebService(GoodsWebService.class);
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshUI();
        getUnreadMsgCount();
        getOrderStatistics();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_show_all://全部订单
                toOrderListPage(0);
                break;
            case R.id.btn_pay://待付款
                toOrderListPage(1);
                break;
            case R.id.btn_send://待发货
                toOrderListPage(2);
                break;
            case R.id.btn_receive://待收货
                toOrderListPage(3);
                break;
            case R.id.btn_comment://待评价
                toOrderListPage(4);
                break;
            case R.id.btn_refund://退款
                if(isLogined())
                startActivity(new Intent(mContext, RefundListActivity.class));
                break;
            case R.id.rl_person_info://个人资料
                loginOrUpdateInfo();
                break;
            case R.id.img_set://设置
                startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.img_msg://消息
                if(isLogined())
                    startActivity(new Intent(mContext, MsgMenusActivity.class));
                break;
            case R.id.rl_goods_collect://商品收藏
                showMyCollects(0);
                break;
            case R.id.rl_shop_collect://店铺收藏
                showMyCollects(1);
                break;
            case R.id.rl_footmark://足迹
                if(isLogined())
                    startActivity(new Intent(mContext, FootmarkActivity.class));
                break;
            default:break;
        }
    }

    /**
     * 转跳品收藏页面
     */
    private void showMyCollects(int showTypeIndex){
        if(isLogined()){
            Intent i=new Intent(mContext,MyCollectListActivity.class);
            i.putExtra(MyCollectListActivity.SHOW_TYPE_INDEX,showTypeIndex);
            startActivity(i);
        }
    }

    /**
     * 转跳订单列表页面
     */
    private void toOrderListPage(int index){
        if(isLogined()){
            Intent i=new Intent(mContext, OrderListActivity.class);
            i.putExtra(OrderListActivity.STATUS_INDEX_KEY,index);
            startActivity(i);
        }
    }

    /**
     * 转跳登录或者个人资料页面
     */
    private void loginOrUpdateInfo(){
        if(null!=mUser){
            startActivity(new Intent(mContext, UserInfoActivity.class));
        }else {
            startActivity(new Intent(mContext, LoginActivity.class));
        }
    }

    /**
     * 获取个状态下订单数量
     */
    private void getOrderStatistics(){
        if(null==mUser) return;
        Call<ResponseBody> call=orderWebService.getOrderStatistics(mUser.getUserId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        JSONArray ja=new JSONObject(result).getJSONArray("result");
                        List<OrderStatistics> tempList = mGson.fromJson(ja.toString(), new TypeToken<List<OrderStatistics>>() {}.getType());
                        Map<Integer,String> map=new HashMap<Integer, String>();
                        for(OrderStatistics os:tempList){
                            map.put(os.getStatus(),os.getCount());
                        }
                        showOrderNum(map);
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
     * 显示订单数量
     */
    private void showOrderNum(Map<Integer,String> map){
        String payNum=map.get(Constants.ORDER_STATUS_PENDING_PAY);
        if(!TextUtils.isEmpty(payNum)){
            txtPayNum.setVisibility(View.VISIBLE);
            txtPayNum.setText(payNum);
        }else {
            txtPayNum.setVisibility(View.GONE);
        }

        String sendNum=map.get(Constants.ORDER_STATUS_PRE_DELIVERED);
        if(!TextUtils.isEmpty(sendNum)){
            txtSendNum.setVisibility(View.VISIBLE);
            txtSendNum.setText(sendNum);
        }else {
            txtSendNum.setVisibility(View.GONE);
        }

        String receiveNum=map.get(Constants.ORDER_STATUS_HAS_SENDED);
        if(!TextUtils.isEmpty(receiveNum)){
            txtReceiveNum.setVisibility(View.VISIBLE);
            txtReceiveNum.setText(receiveNum);
        }else {
            txtReceiveNum.setVisibility(View.GONE);
        }

        String commentNum=map.get(Constants.ORDER_STATUS_PRE_EVALUATED);
        if(!TextUtils.isEmpty(commentNum)){
            txtCommentNum.setVisibility(View.VISIBLE);
            txtCommentNum.setText(commentNum);
        }else {
            txtCommentNum.setVisibility(View.GONE);
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

    /**
     * 更新UI
     */
    public void refreshUI() {
        if(null != mUser){
            txtName.setText(mUser.getUsername());
            ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+mUser.getAvatar(),imgHead, DisplayImageOptionsUtil.getOptionsRounded(360));
            getCollectNum();
            getOrderStatistics();
        }else{
            txtName.setText("点击登录");
            ImageLoader.getInstance().displayImage("drawable://"+R.drawable.iconfont_touxiang,imgHead, DisplayImageOptionsUtil.getOptionsRounded(360));
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

    /**
     * 显示未读消息数量
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setUnReadMsgCount(UnreadEvent event){
        int unreadNum=event.unreadNum;
        if(unreadNum>0){
            txtMsgCount.setVisibility(View.VISIBLE);
            txtMsgCount.setText(unreadNum+"");
        }else{
            txtMsgCount.setVisibility(View.GONE);
        }
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
        txtMsgCount=(TextView) fragmentView.findViewById(R.id.txt_msg_count);
        btnPay= (TextView) fragmentView.findViewById(R.id.btn_pay);
        btnSend= (TextView) fragmentView.findViewById(R.id.btn_send);
        btnReceive= (TextView) fragmentView.findViewById(R.id.btn_receive);
        btnComment= (TextView) fragmentView.findViewById(R.id.btn_comment);
        btnRefund= (TextView) fragmentView.findViewById(R.id.btn_refund);
        llAllOrders= (LinearLayout) fragmentView.findViewById(R.id.ll_show_all);
        txtPayNum= (TextView) fragmentView.findViewById(R.id.txt_pay_count);
        txtSendNum= (TextView) fragmentView.findViewById(R.id.txt_send_count);
        txtReceiveNum= (TextView) fragmentView.findViewById(R.id.txt_receive_count);
        txtCommentNum= (TextView) fragmentView.findViewById(R.id.txt_comment_count);
        txtRefundNum= (TextView) fragmentView.findViewById(R.id.txt_refund_count);
        rlGoodsCollect= (RelativeLayout) fragmentView.findViewById(R.id.rl_goods_collect);
        rlShopCollect= (RelativeLayout) fragmentView.findViewById(R.id.rl_shop_collect);
        rlFootMark= (RelativeLayout) fragmentView.findViewById(R.id.rl_footmark);
        rlGoodsCollect.setOnClickListener(this);
        rlShopCollect.setOnClickListener(this);
        rlFootMark.setOnClickListener(this);
        rlPersonInfo.setOnClickListener(this);
        txtGoodsCollectNum.setOnClickListener(this);
        txtShopCollectNum.setOnClickListener(this);
        txtFootmarkNum.setOnClickListener(this);
        imgSet.setOnClickListener(this);
        imgMsg.setOnClickListener(this);
        btnPay.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnReceive.setOnClickListener(this);
        btnComment.setOnClickListener(this);
        btnRefund.setOnClickListener(this);
        llAllOrders.setOnClickListener(this);
        return fragmentView;
    }


    @Override
    public void initView() {
        refreshUI();
    }
}
