package com.bap.yuwei.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.fragment.CartFragment;
import com.bap.yuwei.fragment.CategoryFragment;
import com.bap.yuwei.fragment.HomeFragment;
import com.bap.yuwei.fragment.MineFragment;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ToastUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {

    private HomeFragment homeFragment;
    private CategoryFragment categoryFragment;
    private CartFragment cartFragment;
    private MineFragment mineFragment;

    private Button[] mTabs;
    private Fragment[] fragments;

    // 当前fragment的index
    private int currentTabIndex;
    //点击的index
    private int index;
    //退出点击次数
    private int times=1;

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
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CONTACTS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
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
    protected void onDestroy() {
        super.onDestroy();
    }
}
