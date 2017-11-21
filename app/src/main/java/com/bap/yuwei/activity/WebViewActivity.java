package com.bap.yuwei.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;

/**
 * 网页详情
 */
public class WebViewActivity extends BaseActivity {
    private WebView webView;
    private ProgressBar pb;
    private String url;

    public static final String URL="url.key";
    public static final String TITLE="title.key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mTxtTitle.setText(getIntent().getStringExtra(TITLE));
        url=getIntent().getStringExtra(URL);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pb.setProgress(newProgress);
                if(newProgress==0){
                    pb.setVisibility(View.VISIBLE);
                }else if(newProgress==100){
                    pb.setVisibility(View.GONE);
                }
            }
        });
        webView.loadUrl(url);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    public void initView() {
        webView=(WebView) findViewById(R.id.webview);
        pb=(ProgressBar) findViewById(R.id.progressBar);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
       // webView.setScrollBarStyle(WebView.GONE);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.requestFocus();
    }
}
