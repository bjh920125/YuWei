package com.bap.yuwei.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bap.yuwei.activity.WebViewActivity;
import com.bap.yuwei.entity.Banner;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.bigkoo.convenientbanner.holder.Holder;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 轮播图adapter
 */
public class RotationMapAdapter implements Holder<Banner> {
    private ImageView imageView;
    private Context mContext;

    @Override
    public View createView(Context context) {
        mContext=context;
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void UpdateUI(final Context context, int position,final Banner data) {
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+data.getImageUrl(),imageView, DisplayImageOptionsUtil.getOptions());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetail(context,data);
            }
        });
    }

    /**
     * 显示详情
     * @param context
     * @param data
     */
    private void showDetail(Context context,Banner data){
        String url=data.getClickUrl();
        if(!TextUtils.isEmpty(url)){
            Intent i=new Intent(context, WebViewActivity.class);
            i.putExtra(WebViewActivity.URL,url);
            i.putExtra(WebViewActivity.TITLE,"");
            context.startActivity(i);
        }
    }


}