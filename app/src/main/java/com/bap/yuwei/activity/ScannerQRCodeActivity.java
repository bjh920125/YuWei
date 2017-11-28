package com.bap.yuwei.activity;

import android.content.Intent;
import android.os.Bundle;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.activity.sys.QRLoginActivity;
import com.bap.yuwei.entity.Constants;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScannerQRCodeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scan();
    }

    /**
     *  扫描
     */
    private void scan(){
        new IntentIntegrator(this)
                .setBarcodeImageEnabled(true)
                .setBeepEnabled(true)
                .setOrientationLocked(false)
                .setPrompt("请将二维码置于识别框内")
                //.setCaptureActivity(CustomScanActivity.class) // 设置自定义的activity是CustomActivity
                .initiateScan(); // 初始化扫描
    }

    // 通过 onActivityResult的方法获取 扫描回来的 值
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_CANCELED){//返回建返回的
            finish();
        }else {
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
            if(intentResult != null) {
                if(intentResult.getContents() == null) {
                    //Toast.makeText(this,"请重新扫描！",Toast.LENGTH_LONG).show();
                } else {
                    String scanResult = intentResult.getContents(); // ScanResult 为 获取到的字符串
                    Intent intent=new Intent(mContext, QRLoginActivity.class);
                    intent.putExtra(Constants.TOKEN_KEY,scanResult);
                    startActivity(intent);
                    finish();
                }
            } else {
                super.onActivityResult(requestCode,resultCode,data);
            }
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_scanner_qrcode;
    }

    @Override
    protected void initView() {

    }
}
