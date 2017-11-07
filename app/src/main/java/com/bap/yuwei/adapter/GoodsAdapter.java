package com.bap.yuwei.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.goods.Goods;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2017/11/3.
 */

public class GoodsAdapter extends ListBaseAdapter<Goods> {

    public GoodsAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_goods;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        Goods goods=mDataList.get(position);
        ImageView imgGoods=holder.getView(R.id.img_goods);
        TextView txtTitle=holder.getView(R.id.txt_title);
        TextView txtPrice=holder.getView(R.id.txt_price);
        TextView txtCommentNum=holder.getView(R.id.txt_comment_num);
        TextView txtGoodCommentPercent=holder.getView(R.id.txt_comment_percent);
        txtTitle.setText(goods.getTitle());
        txtPrice.setText("￥"+goods.getPreferentialPrice());
        txtCommentNum.setText(goods.getTotalComment()+"条评价");
        txtGoodCommentPercent.setText(goods.getGoodCommentPercent()+"%好评");
        ImageLoader.getInstance().displayImage(null!=goods.getGoodsImage() ? Constants.PICTURE_URL+goods.getGoodsImage() :  Constants.PICTURE_URL+goods.getMainPic(), imgGoods, DisplayImageOptionsUtil.getOptions());
    }



}
