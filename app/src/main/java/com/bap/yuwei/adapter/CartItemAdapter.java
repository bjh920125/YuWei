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
import com.bap.yuwei.entity.event.CartMoneyEvent;
import com.bap.yuwei.entity.event.UpdateCartEvent;
import com.bap.yuwei.entity.order.GoodsCart;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.bap.yuwei.util.StringUtils;
import com.bap.yuwei.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 */

public class CartItemAdapter extends BaseAdapter {

    private List<GoodsCart> cartItems;
    private Context context;
    private LayoutInflater mInflater;

    public CartItemAdapter(List<GoodsCart> cartItems,Context context){
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
            convertView = mInflater.inflate(R.layout.item_cart, parent, false);
            viewHolder.imgCheck= (ImageView) convertView.findViewById(R.id.img_check);
            viewHolder.imgGoods= (ImageView) convertView.findViewById(R.id.img_goods);
            viewHolder.imgMore= (ImageView) convertView.findViewById(R.id.img_add);
            viewHolder.imgLess= (ImageView) convertView.findViewById(R.id.img_less);
            viewHolder.txtGoodsName= (TextView) convertView.findViewById(R.id.txt_goods_name);
            viewHolder.txtModel= (TextView) convertView.findViewById(R.id.txt_model);
            viewHolder.txtPrice= (TextView) convertView.findViewById(R.id.txt_price);
            viewHolder.txtNum= (TextView) convertView.findViewById(R.id.txt_num);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        final GoodsCart cart=cartItems.get(i);
        if(cart.isChecked()){
            ImageLoader.getInstance().displayImage("drawable://"+R.drawable.checked,viewHolder.imgCheck, DisplayImageOptionsUtil.getOptionsWithoutFade());
        }else {
            ImageLoader.getInstance().displayImage("drawable://"+R.drawable.checke_kong,viewHolder.imgCheck, DisplayImageOptionsUtil.getOptionsWithoutFade());
        }
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+cart.getGoodsImage(),viewHolder.imgGoods, DisplayImageOptionsUtil.getOptionsWithoutFade());
        viewHolder.txtGoodsName.setText(cart.getTitle());
        viewHolder.txtModel.setText("类别："+cart.getModel());
        viewHolder.txtPrice.setText("￥"+cart.getPreferentialPrice());
        viewHolder.txtNum.setText(cart.getGoodsCount()+"");

        viewHolder.imgCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cart.isChecked()){
                    cart.setChecked(false);
                }else {
                    cart.setChecked(true);
                }
                notifyDataSetChanged();
                EventBus.getDefault().post(new CartMoneyEvent());
            }
        });

        viewHolder.imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num= Integer.parseInt(StringUtils.getTextViewValue(viewHolder.txtNum));
                if(num<cart.getMaxQuantity()){
                    viewHolder.txtNum.setText(++num+"");
                    EventBus.getDefault().post(new UpdateCartEvent(cart.getGoodsCartId(),num));
                }else{
                    ToastUtil.showShort(context,"数量不能超过库存！");
                }
            }
        });
        viewHolder.imgLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num= Integer.parseInt(StringUtils.getTextViewValue(viewHolder.txtNum));
                if(num>=2){
                    viewHolder.txtNum.setText(--num+"");
                    EventBus.getDefault().post(new UpdateCartEvent(cart.getGoodsCartId(),num));
                }
            }
        });

        return convertView;
    }

    class ViewHolder{
        ImageView imgCheck;
        ImageView imgGoods;
        TextView txtGoodsName;
        TextView txtModel;
        TextView txtPrice;
        TextView txtNum;
        ImageView imgMore;
        ImageView imgLess;
    }
}
