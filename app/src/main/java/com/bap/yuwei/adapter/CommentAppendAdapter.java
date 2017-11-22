package com.bap.yuwei.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.order.CommentAppendItemForm;
import com.bap.yuwei.entity.order.OrderItem;
import com.bap.yuwei.util.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Administrator on 2017/11/22.
 */

public class CommentAppendAdapter extends BaseAdapter {

    private List<OrderItem> orderItems;
    private List<CommentAppendItemForm> itemForms;
    private Context context;
    private LayoutInflater mInflater;

    public CommentAppendAdapter(List<OrderItem> orderItems, List<CommentAppendItemForm> itemForms, Context context){
        this.context=context;
        this.orderItems=orderItems;
        this.itemForms=itemForms;
        this.mInflater= LayoutInflater.from(context);
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
            convertView = mInflater.inflate(R.layout.item_append_comment, parent, false);
            viewHolder.imgGoods = (ImageView) convertView.findViewById(R.id.img_goods);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txt_goods_title);
            viewHolder.etDesc = (EditText) convertView.findViewById(R.id.et_desc);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        OrderItem orderItem=orderItems.get(i);
        final CommentAppendItemForm form=itemForms.get(i);
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+orderItem.getGoodsImage(),viewHolder.imgGoods);
        viewHolder.txtTitle.setText(orderItem.getTitle());
        viewHolder.etDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                form.setComment(StringUtils.getEditTextValue(viewHolder.etDesc));
            }
        });
        return convertView;
    }

    class ViewHolder{
        ImageView imgGoods;
        TextView txtTitle;
        EditText etDesc;
    }
}
