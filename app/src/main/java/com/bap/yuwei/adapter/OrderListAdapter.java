package com.bap.yuwei.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.order.OrderItem;
import com.bap.yuwei.entity.order.Orders;
import com.linearlistview.LinearListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import static com.bap.yuwei.entity.Constants.ORDER_STATUS_CLOSED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_HAS_CANCELED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_HAS_COMPLETED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_HAS_SENDED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_PENDING_PAY;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_PRE_DELIVERED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_PRE_EVALUATED;

/**
 * Created by Administrator on 2017/11/14.
 */

public class OrderListAdapter extends ListBaseAdapter<Orders> {

    private List<Orders> orderses;

    public OrderListAdapter(Context context,List<Orders> orderses) {
        super(context);
        this.orderses=orderses;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_order_list_module;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        ImageView imgshop=holder.getView(R.id.img_shop);
        TextView txtShopName=holder.getView(R.id.txt_shop_name);
        TextView txtStatus=holder.getView(R.id.txt_status);
        LinearListView lvOrderItem=holder.getView(R.id.lv_order_item);
        TextView txtTotalNum=holder.getView(R.id.txt_total_num);
        TextView txtTotalPrice=holder.getView(R.id.txt_price);
        TextView txtPay=holder.getView(R.id.txt_pay);
        TextView txtAlertSend=holder.getView(R.id.txt_alert_send);
        TextView txtShowExpress=holder.getView(R.id.txt_show_express);
        TextView txtReceive=holder.getView(R.id.txt_receive);
        TextView txtComment=holder.getView(R.id.txt_comment);
        TextView txtCancel=holder.getView(R.id.txt_cancel);
        TextView txtDelete=holder.getView(R.id.txt_delete);

        Orders order=orderses.get(position);
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+order.getShopIcon(),imgshop);
        txtShopName.setText(order.getShopName());
        txtStatus.setText(order.getStatusText());
        txtTotalNum.setText("共"+order.getTotalGoodsCount()+"件商品");
        txtTotalPrice.setText("合计：￥"+order.getRealPayAmount()+"（含运费￥"+order.getFreight()+"）");

        int status=order.getStatus();
        if(status==ORDER_STATUS_PENDING_PAY){//待付款
            txtPay.setVisibility(View.VISIBLE);
            txtAlertSend.setVisibility(View.GONE);
            txtShowExpress.setVisibility(View.GONE);
            txtReceive.setVisibility(View.GONE);
            txtComment.setVisibility(View.GONE);
            txtCancel.setVisibility(View.VISIBLE);
            txtDelete.setVisibility(View.GONE);
        }else if(status==ORDER_STATUS_PRE_DELIVERED){//待发货
            txtPay.setVisibility(View.GONE);
            txtAlertSend.setVisibility(View.VISIBLE);
            txtShowExpress.setVisibility(View.GONE);
            txtReceive.setVisibility(View.GONE);
            txtComment.setVisibility(View.GONE);
            txtCancel.setVisibility(View.GONE);
            txtDelete.setVisibility(View.GONE);
        }else if(status==ORDER_STATUS_HAS_SENDED){//待收货
            txtPay.setVisibility(View.GONE);
            txtAlertSend.setVisibility(View.GONE);
            txtShowExpress.setVisibility(View.VISIBLE);
            txtReceive.setVisibility(View.VISIBLE);
            txtComment.setVisibility(View.GONE);
            txtCancel.setVisibility(View.GONE);
            txtDelete.setVisibility(View.GONE);
        }else if(status==ORDER_STATUS_PRE_EVALUATED){//待评价
            txtPay.setVisibility(View.GONE);
            txtAlertSend.setVisibility(View.GONE);
            txtShowExpress.setVisibility(View.VISIBLE);
            txtReceive.setVisibility(View.GONE);
            txtComment.setVisibility(View.VISIBLE);
            txtCancel.setVisibility(View.GONE);
            txtDelete.setVisibility(View.GONE);
        }else if(status==ORDER_STATUS_HAS_COMPLETED){//已完成
            txtPay.setVisibility(View.GONE);
            txtAlertSend.setVisibility(View.GONE);
            txtShowExpress.setVisibility(View.VISIBLE);
            txtReceive.setVisibility(View.GONE);
            txtComment.setVisibility(View.GONE);
            txtCancel.setVisibility(View.GONE);
            txtDelete.setVisibility(View.VISIBLE);
        }else if(status==ORDER_STATUS_HAS_CANCELED){//已取消
            txtPay.setVisibility(View.GONE);
            txtAlertSend.setVisibility(View.GONE);
            txtShowExpress.setVisibility(View.GONE);
            txtReceive.setVisibility(View.GONE);
            txtComment.setVisibility(View.GONE);
            txtCancel.setVisibility(View.GONE);
            txtDelete.setVisibility(View.VISIBLE);
        }else if(status==ORDER_STATUS_CLOSED){//已关闭
            txtPay.setVisibility(View.GONE);
            txtAlertSend.setVisibility(View.GONE);
            txtShowExpress.setVisibility(View.GONE);
            txtReceive.setVisibility(View.GONE);
            txtComment.setVisibility(View.GONE);
            txtCancel.setVisibility(View.GONE);
            txtDelete.setVisibility(View.VISIBLE);
        }

        List<OrderItem> items=order.getOrderItems();
        OrderListItemAdapter adapter=new OrderListItemAdapter(mContext,items);
        lvOrderItem.setAdapter(adapter);
    }
}
