package com.bap.yuwei.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.order.GoodsCart;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Administrator on 2017/11/10.
 */

public class OrderItemAdapter extends BaseAdapter {

    private List<GoodsCart> cartItems;
    private Context context;
    private LayoutInflater mInflater;

    public OrderItemAdapter(List<GoodsCart> cartItems,Context context){
        this.context=context;
        this.cartItems=cartItems;
        mInflater= LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public GoodsCart getItem(int i) {
        return cartItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView==null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_order, parent, false);
            viewHolder.imgGoods= (ImageView) convertView.findViewById(R.id.img_goods);
            viewHolder.txtGoodsName= (TextView) convertView.findViewById(R.id.txt_goods_name);
            viewHolder.txtModel= (TextView) convertView.findViewById(R.id.txt_model);
            viewHolder.txtPrice= (TextView) convertView.findViewById(R.id.txt_price);
            viewHolder.txtNum= (TextView) convertView.findViewById(R.id.txt_num);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        final GoodsCart cart=cartItems.get(i);
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+cart.getGoodsImage(),viewHolder.imgGoods, DisplayImageOptionsUtil.getOptionsWithoutFade());
        viewHolder.txtGoodsName.setText(cart.getTitle());
        viewHolder.txtModel.setText("类别："+cart.getModel());
        viewHolder.txtPrice.setText("￥"+cart.getPreferentialPrice());
        viewHolder.txtNum.setText("x"+cart.getGoodsCount()+"");
        return convertView;
    }

    class ViewHolder{
        ImageView imgGoods;
        TextView txtGoodsName;
        TextView txtModel;
        TextView txtPrice;
        TextView txtNum;
    }
}
