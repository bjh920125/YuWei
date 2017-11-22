package com.bap.yuwei.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.goods.ShopCollect;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2017/11/22.
 */

public class ShopCollectAdapter extends ListBaseAdapter<ShopCollect>{


    public ShopCollectAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_shop_collect;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        ImageView imgGoods = holder.getView(R.id.img_shop);
        TextView txtTitle=holder.getView(R.id.txt_shop_name);
        TextView txtAllNum=holder.getView(R.id.txt_num);

        ShopCollect shopCollect=mDataList.get(position);
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+shopCollect.getShopIcon(),imgGoods);
        txtTitle.setText(shopCollect.getShopName());
        txtAllNum.setText(shopCollect.getGoodsTotal()+"");

    }
}