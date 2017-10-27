package com.bap.yuwei.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.activity.sys.LoginActivity;

public class SplashActivity extends BaseActivity {

	private final int GO_HOME_PAGE = 1000;
	private final int GO_LOGIN_PAGE = 1001;
	private final long SPLASH_DELAY_MILLIS = 2000;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case GO_HOME_PAGE:
				goHomePage();
				break;
			case GO_LOGIN_PAGE:
				goLoginpage();
				break;
			}
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!isTaskRoot()) {
			finish();
			return;
		}
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_splash;
	}

	@Override
	public void onResume() {
		//JPushInterface.onResume(mContext);
		super.onResume();
		mHandler.sendEmptyMessageDelayed(GO_HOME_PAGE, SPLASH_DELAY_MILLIS);
	}

	@Override
	public void onPause() {
		//JPushInterface.onPause(mContext);
		super.onPause();
	}
	

	/**
	 * 转跳到主页
	 */
	private void goHomePage(){
		startActivity(new Intent(mContext, MainActivity.class));
		overridePendingTransition(R.anim.alpha_in,R.anim.alpha_out);
	}
	
	/**
	 * 转跳到登录界面
	 */
	private void goLoginpage(){
		startActivity(new Intent(mContext, LoginActivity.class));
	}
	



	@Override
	public void initView() {
	}

}
