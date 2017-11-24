package com.bap.yuwei.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.goods.GoodsImage;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.bigkoo.convenientbanner.holder.Holder;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 轮播图adapter
 */
public class GoodsImageAdapter implements Holder<GoodsImage> {
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
    public void UpdateUI(final Context context, int position,final GoodsImage data) {
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+data.getGoodsImagePath(),imageView, DisplayImageOptionsUtil.getOptions());
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
    private void showDetail(Context context,GoodsImage data){


    }


}