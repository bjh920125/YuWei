package com.bap.yuwei.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.entity.event.CartMoneyEvent;
import com.bap.yuwei.entity.order.GoodsCart;
import com.bap.yuwei.entity.order.MyGoodsCart;
import com.linearlistview.LinearListView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 */

public class CartAdapter extends BaseAdapter{

    private List<MyGoodsCart> myGoodsCarts;
    private Context context;
    private LayoutInflater mInflater;

    public CartAdapter(List<MyGoodsCart> myGoodsCarts,Context context){
        this.context=context;
        this.myGoodsCarts=myGoodsCarts;
        mInflater= LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return myGoodsCarts.size();
    }

    @Override
    public MyGoodsCart getItem(int i) {
        return myGoodsCarts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_cart_module, parent, false);
            viewHolder.txtShopName= (TextView) convertView.findViewById(R.id.txt_shop_name);
            viewHolder.lvCartItem= (LinearListView) convertView.findViewById(R.id.lv_cart_item);
            viewHolder.cbAll= (CheckBox) convertView.findViewById(R.id.cb_all);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        final MyGoodsCart myGoodsCart=myGoodsCarts.get(i);
        viewHolder.txtShopName.setText(myGoodsCart.getShopName());
        viewHolder.cbAll.setChecked(myGoodsCart.isChecked());

        final CartItemAdapter adapter=new CartItemAdapter(myGoodsCart.getCartItems(),context);
        viewHolder.lvCartItem.setAdapter(adapter);

        viewHolder.cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for(GoodsCart cartItem:myGoodsCart.getCartItems()){
                    cartItem.setChecked(b);
                }
                adapter.notifyDataSetChanged();
                EventBus.getDefault().post(new CartMoneyEvent());
            }
        });
        return convertView;
    }

    class ViewHolder{
        CheckBox cbAll;
        TextView txtShopName;
        LinearListView lvCartItem;
    }


}
