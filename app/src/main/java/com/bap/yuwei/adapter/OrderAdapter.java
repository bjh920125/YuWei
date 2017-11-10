package com.bap.yuwei.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.order.OrderEnsure;
import com.bap.yuwei.entity.order.OrderShop;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.bap.yuwei.util.StringUtils;
import com.linearlistview.LinearListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Administrator on 2017/11/10.
 */

public class OrderAdapter extends BaseAdapter {

    private OrderEnsure mOrderEnsure;
    private List<OrderShop> shopItems;
    private Context context;
    private LayoutInflater mInflater;

    public OrderAdapter(OrderEnsure mOrderEnsure, Context context) {
        this.context = context;
        this.mOrderEnsure = mOrderEnsure;
        this.shopItems=mOrderEnsure.getShopItems();
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return shopItems.size();
    }

    @Override
    public OrderShop getItem(int i) {
        return shopItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_order_module, parent, false);
            viewHolder.imgShop = (ImageView) convertView.findViewById(R.id.img_shop);
            viewHolder.txtShopName= (TextView) convertView.findViewById(R.id.txt_shop_name);
            viewHolder.lvCartItem = (LinearListView) convertView.findViewById(R.id.lv_cart_item);
            viewHolder.txtExpressType= (TextView) convertView.findViewById(R.id.txt_express_type);
            viewHolder.etBuyerMsg=(EditText) convertView.findViewById(R.id.et_buyer_msg);
            viewHolder.txtPrice= (TextView) convertView.findViewById(R.id.txt_price);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final OrderShop orderShop = shopItems.get(i);
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+orderShop.getShopIcon(),viewHolder.imgShop, DisplayImageOptionsUtil.getOptionsWithoutFade());
        viewHolder.txtShopName.setText(orderShop.getShopName());
        viewHolder.txtExpressType.setText("快递：￥"+orderShop.getShopTotalFreight());
        viewHolder.txtPrice.setText("￥"+orderShop.getShopPayAmount());
        viewHolder.etBuyerMsg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    orderShop.setBuyerMsg(StringUtils.getEditTextValue(viewHolder.etBuyerMsg));
                }
            }
        });

        final OrderItemAdapter adapter = new OrderItemAdapter(orderShop.getCartItems(), context);
        viewHolder.lvCartItem.setAdapter(adapter);

        return convertView;
    }

    class ViewHolder {
        ImageView imgShop;
        TextView txtShopName;
        LinearListView lvCartItem;
        TextView txtExpressType;
        EditText etBuyerMsg;
        TextView txtPrice;
    }
}