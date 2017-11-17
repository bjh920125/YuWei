package com.bap.yuwei.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.order.ChooseRefundTypeActivity;
import com.bap.yuwei.activity.order.RefundDetailActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.order.OrderItem;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import static com.bap.yuwei.entity.Constants.ORDER_ITEM_STATUS_HAS_BUYER_RECEIVED;
import static com.bap.yuwei.entity.Constants.ORDER_ITEM_STATUS_PRE_BUYER_RECEIVE;
import static com.bap.yuwei.entity.Constants.ORDER_ITEM_STATUS_REFUND_PRE_DEAL;
import static com.bap.yuwei.entity.Constants.ORDER_ITEM_STATUS_REFUND_SUCCESS;

/**
 * Created by Administrator on 2017/11/10.
 */

public class OrderItemDetailAdapter extends BaseAdapter {

    private List<OrderItem> orderItems;
    private Context context;
    private LayoutInflater mInflater;

    public OrderItemDetailAdapter(List<OrderItem> orderItems, Context context){
        this.context=context;
        this.orderItems=orderItems;
        mInflater= LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return orderItems.size();
    }

    @Override
    public OrderItem getItem(int i) {
        return orderItems.get(i);
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
            convertView = mInflater.inflate(R.layout.item_order_detail, parent, false);
            viewHolder.imgGoods= (ImageView) convertView.findViewById(R.id.img_goods);
            viewHolder.txtGoodsName= (TextView) convertView.findViewById(R.id.txt_goods_name);
            viewHolder.txtModel= (TextView) convertView.findViewById(R.id.txt_model);
            viewHolder.txtPrice= (TextView) convertView.findViewById(R.id.txt_price);
            viewHolder.txtNum= (TextView) convertView.findViewById(R.id.txt_num);
            viewHolder.txtRefund= (TextView) convertView.findViewById(R.id.txt_refund);
            viewHolder.txtRefunding= (TextView) convertView.findViewById(R.id.txt_refunding);
            viewHolder.txtRefundSuccess=(TextView) convertView.findViewById(R.id.txt_refund_success);
            viewHolder.llOperate= (LinearLayout) convertView.findViewById(R.id.ll_order_item_operate);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        final OrderItem cart=orderItems.get(i);
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+cart.getGoodsImage(),viewHolder.imgGoods, DisplayImageOptionsUtil.getOptionsWithoutFade());
        viewHolder.txtGoodsName.setText(cart.getTitle());
        viewHolder.txtModel.setText("类别："+cart.getModel());
        viewHolder.txtPrice.setText("￥"+cart.getPreferentialPrice());
        viewHolder.txtNum.setText("x"+cart.getQuantity()+"");

        viewHolder.txtRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context, ChooseRefundTypeActivity.class);
                i.putExtra(OrderItem.KEY,cart);
                context.startActivity(i);
            }
        });

        viewHolder.txtRefunding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context, RefundDetailActivity.class);
                i.putExtra(OrderItem.KEY,cart);
                context.startActivity(i);
            }
        });
        //控制退款按钮显示
        Integer status=cart.getStatus();
        if(null==status){
            viewHolder.llOperate.setVisibility(View.VISIBLE);
            viewHolder.txtRefund.setVisibility(View.VISIBLE);
            viewHolder.txtRefunding.setVisibility(View.GONE);
            viewHolder.txtRefundSuccess.setVisibility(View.GONE);
        }else if(status==ORDER_ITEM_STATUS_REFUND_PRE_DEAL){//退款待处理
            viewHolder.llOperate.setVisibility(View.VISIBLE);
            viewHolder.txtRefund.setVisibility(View.GONE);
            viewHolder.txtRefunding.setVisibility(View.VISIBLE);
            viewHolder.txtRefundSuccess.setVisibility(View.GONE);
        }else if(status==ORDER_ITEM_STATUS_PRE_BUYER_RECEIVE || status==ORDER_ITEM_STATUS_HAS_BUYER_RECEIVED){
            viewHolder.llOperate.setVisibility(View.VISIBLE);
            viewHolder.txtRefund.setVisibility(View.GONE);
            viewHolder.txtRefunding.setVisibility(View.VISIBLE);
            viewHolder.txtRefundSuccess.setVisibility(View.GONE);
        }else if(status==ORDER_ITEM_STATUS_REFUND_SUCCESS){//退款成功
            viewHolder.llOperate.setVisibility(View.VISIBLE);
            viewHolder.txtRefund.setVisibility(View.GONE);
            viewHolder.txtRefunding.setVisibility(View.GONE);
            viewHolder.txtRefundSuccess.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class ViewHolder{
        ImageView imgGoods;
        TextView txtGoodsName;
        TextView txtModel;
        TextView txtPrice;
        TextView txtNum;
        TextView txtRefund;
        TextView txtRefunding;
        TextView txtRefundSuccess;
        LinearLayout llOperate;
    }
}
