package com.bap.yuwei.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.event.UpdateCartNumEvent;
import com.bap.yuwei.entity.http.AppResponse;
import com.bap.yuwei.entity.http.ResponseCode;
import com.bap.yuwei.fragment.CartFragment;
import com.bap.yuwei.fragment.CategoryFragment;
import com.bap.yuwei.fragment.HomeFragment;
import com.bap.yuwei.fragment.MineFragment;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.GoodsWebService;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private HomeFragment homeFragment;
    private CategoryFragment categoryFragment;
    private CartFragment cartFragment;
    private MineFragment mineFragment;

    private Button[] mTabs;
    private Fragment[] fragments;
    private TextView txtCartNum;

    // 当前fragment的index
    private int currentTabIndex;
    //点击的index
    private int index;
    //退出点击次数
    private int times=1;

    private GoodsWebService webService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeFragment=new HomeFragment();
        categoryFragment=new CategoryFragment();
        cartFragment=new CartFragment();
        mineFragment=new MineFragment();

        fragments=new Fragment[]{homeFragment,categoryFragment,cartFragment,mineFragment};
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment)
                .show(homeFragment)
                .commit();
        requestPermissions();
        webService=MyApplication.getInstance().getWebService(GoodsWebService.class);
    }


    public void onTabClicked(View v){
        switch (v.getId()) {
            case R.id.btn_home:
                index=0;
                break;
            case R.id.btn_category:
                index=1;
                break;
            case R.id.btn_cart:
                if(!isLogined()){
                    return;
                }
                index=2;
                break;
            case R.id.btn_mine:
                index=3;
                break;
        }
        if(currentTabIndex != index){
            FragmentTransaction trx=getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if(!fragments[index].isAdded()){
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commitAllowingStateLoss();
        }
        mTabs[currentTabIndex].setSelected(false);
        mTabs[index].setSelected(true);
        currentTabIndex=index;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mTabs=new Button[4];
        mTabs[0]=(Button) findViewById(R.id.btn_home);
        mTabs[1]=(Button) findViewById(R.id.btn_category);
        mTabs[2]=(Button) findViewById(R.id.btn_cart);
        mTabs[3]=(Button) findViewById(R.id.btn_mine);
        mTabs[0].setSelected(true);
        txtCartNum= (TextView) findViewById(R.id.txt_cart_num);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateCartNum(UpdateCartNumEvent event){
        getCartNum();
    }



    private void getCartNum(){
        if(null==mUser) return;
        Call<ResponseBody> call=webService.getCartsNum(mUser.getUserId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    AppResponse appResponse=mGson.fromJson(result,AppResponse.class);
                    if(appResponse.getCode()== ResponseCode.SUCCESS){
                        int num=new JSONObject(result).getInt("result");
                        updateCartNum(num);
                    }else{
                        ToastUtil.showShort(mContext,appResponse.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    updateCartNum(0);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }

    private void updateCartNum(int num){
        if(num==0){
            txtCartNum.setVisibility(View.GONE);
        }else {
            txtCartNum.setVisibility(View.VISIBLE);
            txtCartNum.setText(num+"");
        }
    }

    @Override
    public void onBackPressed(){
        Timer timer=null;
        if(times==1){
            times++;
            ToastUtil.showShort(this, "再按一次退出应用!");
            timer=new Timer();
            timer.schedule(new TimerTask(){
                @Override
                public void run(){
                    times=1;
                }
            }, 2000);
        }else{
            MyApplication.getInstance().exit();
        }
    }

    /**
     * 申请需要的运行时权限
     */
    protected void requestPermissions(){
        //读写sd卡，拍照权限
        AndPermission.with(this)
                .requestCode(101)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA,Manifest.permission.CALL_PHONE)
                .send();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 只需要调用这一句，剩下的 AndPermission 自动完成。
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults, listener);
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode) {
            if(requestCode == 101) {
                LogUtil.print("permission","success");
            }
        }

        @Override
        public void onFailed(int requestCode) {
            LogUtil.print("permission","failed");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        getCartNum();
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
