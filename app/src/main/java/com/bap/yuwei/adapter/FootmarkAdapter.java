package com.bap.yuwei.adapter;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.goods.Footmark;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2017/11/23.
 */
public class FootmarkAdapter extends ListBaseAdapter<Footmark> {

    public boolean isEditMode=false;

    public FootmarkAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_footmark;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        ImageView imgCheck=holder.getView(R.id.img_check);
        ImageView imgGoods=holder.getView(R.id.img_goods);
        TextView txtTitle=holder.getView(R.id.txt_goods_name);
        TextView txtPrice=holder.getView(R.id.txt_price);
        TextView txtTime=holder.getView(R.id.txt_time);

        final Footmark footmark=mDataList.get(position);
        if(isEditMode){
            imgCheck.setVisibility(View.VISIBLE);
        }else {
            imgCheck.setVisibility(View.GONE);
        }

        if(footmark.isChecked()){
            ImageLoader.getInstance().displayImage("drawable://"+R.drawable.checked,imgCheck, DisplayImageOptionsUtil.getOptionsWithoutFade());
        }else {
            ImageLoader.getInstance().displayImage("drawable://"+R.drawable.checke_kong,imgCheck, DisplayImageOptionsUtil.getOptionsWithoutFade());
        }
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+footmark.getGoodsImage(),imgGoods, DisplayImageOptionsUtil.getOptionsWithoutFade());
        txtTitle.setText(footmark.getTitle());
        txtPrice.setText("￥"+footmark.getPreferentialPrice());

        String currentTime=footmark.getCreateTime().substring(0,11);
        if(position<=0){
            txtTime.setVisibility(View.VISIBLE);
            txtTime.setText(currentTime);
        }else{
            Footmark lastOne=mDataList.get(position-1);
            String lastTime=lastOne.getCreateTime().substring(0,11);
            if(lastTime.equals(currentTime)){
                txtTime.setVisibility(View.GONE);
            }else {
                txtTime.setVisibility(View.VISIBLE);
                txtTime.setText(currentTime);
            }
        }

        imgCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                footmark.setChecked(!footmark.isChecked());
                notifyDataSetChanged();
            }
        });
    }
}
