package com.bap.yuwei.activity.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.sys.LoginActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.sys.User;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.SharedPreferencesUtil;
import com.google.gson.Gson;

import okhttp3.MediaType;


/**
 * 基类
 * @author jianhua
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected TextView mTxtTitle;
    protected TextView mTxtRightMenu;
    protected Toolbar mToolbar;
    protected Dialog mProgressDialog;
    protected User mUser;
    private TextView tipTextView;
    protected Context mContext;
    protected MediaType jsonMediaType= MediaType.parse("application/json; charset=utf-8");
    protected Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        mContext = this;
        initDialog();
      //  mProgressDialog = new ProgressDialog(mContext, ProgressDialog.THEME_HOLO_LIGHT);
        setContentView(getLayoutId());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTxtTitle = (TextView) findViewById(R.id.txt_title);
        mTxtRightMenu = (TextView) findViewById(R.id.txt_right_menu);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);//将Toolbar显示到界面
        }
        if (mTxtTitle != null) {
            //getTitle()的值是activity的android:lable属性值
            mTxtTitle.setText(getTitle());
            setTitleColor();
            //设置默认的标题不显示
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mGson=new Gson();
        String userJson= SharedPreferencesUtil.getString(mContext, Constants.USER_KEY);
        if(null!=userJson) {
            mUser = mGson.fromJson(userJson, User.class);
        }
        initView();
    }



    @Override
    protected void onStart() {
        super.onStart();
        //判断是否有Toolbar,并默认显示返回按钮
        if(null != getToolbar() && isShowBacking()){
            showBack();
        }
    }

    /**
     * 获取头部标题的TextView
     * @return
     */
    public TextView getToolbarTitle(){
        return mTxtTitle;
    }


    /**
     * 获取头部右侧菜单
     * @return
     */
    public TextView getTxtRightMenu(){
        return mTxtRightMenu;
    }

    /**
     * 设置头部标题
     * @param title
     */
    public void setToolBarTitle(CharSequence title) {
        if(mTxtTitle != null){
            mTxtTitle.setText(title);
        }else{
            getToolbar().setTitle(title);
            setSupportActionBar(getToolbar());
        }
    }

    /**
     * this Activity of tool bar.
     * 获取头部.
     * @return support.v7.widget.Toolbar.
     */
    public Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    /**
     * 后退按钮图片
     */
    protected void showBack(){
        //setNavigationIcon必须在setSupportActionBar(toolbar);方法后面加入
        getToolbar().setNavigationIcon(R.drawable.nav_arrow_left);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * 是否显示后退按钮,默认显示,可在子类重写该方法.
     * @return
     */
    protected boolean isShowBacking(){
        return true;
    }

    protected void setTitleColor(){}




    /**
     * this activity layout res
     * 设置layout布局,在子类重写该方法.
     * @return res layout xml id
     */
    protected abstract int getLayoutId();

    public void onRightBtnClick(View v){};

    public void showLoadingDialog() {
        tipTextView.setText("加载中...");// 设置加载信息
        mProgressDialog.show();
    }


    public void showProgressDialog(String message, boolean cancelable) {
        if(!mProgressDialog.isShowing()){
            tipTextView.setText(message);// 设置加载信息
            mProgressDialog.setCancelable(cancelable);
            mProgressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected abstract void initView();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_loading_view);// 加载布局


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


    protected boolean isLogined(){
        if(null==mUser){
            startActivity(new Intent(mContext, LoginActivity.class));
            return false;
        }
        return true;
    }
}
