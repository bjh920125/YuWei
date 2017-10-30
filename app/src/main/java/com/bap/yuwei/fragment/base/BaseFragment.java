package com.bap.yuwei.fragment.base;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.User;
import com.bap.yuwei.entity.event.UserInfoEvent;
import com.bap.yuwei.util.SharedPreferencesUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.MediaType;


/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    protected Activity mActivity;
    protected Dialog mProgressDialog;
    protected MediaType jsonMediaType= MediaType.parse("application/json");
    protected User mUser;
    private TextView tipTextView;
    protected Gson mGson;

    /**
     * 初始化布局与控件
     */
    public abstract void initView();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mActivity = getActivity();
        EventBus.getDefault().register(this);
        mGson=new Gson();
    }

    public void showLoadingDialog() {
        tipTextView.setText("正在加载中...");// 设置加载信息
        mProgressDialog.show();
    }


    public void showProgressDialog(String message, boolean cancelable) {
        tipTextView.setText(message);
        mProgressDialog.setCancelable(cancelable);
        mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
      /*  mProgressDialog = new ProgressDialog(mContext);*/
        initDialog();
        updateUserInfo(new UserInfoEvent());
        initView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUserInfo(UserInfoEvent userInfoEvent){
        String userJson= SharedPreferencesUtil.getString(mContext, Constants.USER_KEY);
        if(null!=userJson) {
            mUser = mGson.fromJson(userJson, User.class);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //MobclickAgent.onPageEnd(BaseFragment.class.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
       //MobclickAgent.onPageStart(BaseFragment.class.getName());
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v
                .findViewById(R.id.dialog_loading_view);// 加载布局


        // 提示文字
        tipTextView = (TextView) v.findViewById(R.id.tipTextView);

        mProgressDialog = new Dialog(mContext, R.style.MyprogressDialogStyle);// 创建自定义样式dialog

        mProgressDialog.setCancelable(true); // 是否可以按“返回键”消失
        mProgressDialog.setCanceledOnTouchOutside(false); // 点击加载框以外的区域
        mProgressDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));// 设置布局
        /**
         *将显示Dialog的方法封装在这里面
         */
        Window window = mProgressDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(lp);
        //   window.setWindowAnimations(R.style.PopWindowAnimStyle);
    }



}
