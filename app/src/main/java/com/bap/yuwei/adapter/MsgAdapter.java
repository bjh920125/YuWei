package com.bap.yuwei.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.sys.Msg;
import com.bap.yuwei.util.DisplayImageOptionsUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2017/11/6.
 */

public class MsgAdapter extends ListBaseAdapter<Msg> {
    private int type;//0系统消息 1物流消息 2订单消息 3通知消息

    public MsgAdapter(Context context, int type) {
        super(context);
        this.type = type;
    }

    @Override
    public int getLayoutId() {
        if(type==Constants.SYS_MSG){
            return R.layout.item_msg_sys;
        }else if(type==Constants.EXPRESS_MSG){
            return R.layout.item_msg_express;
        }else if(type==Constants.ORDER_MSG){
            return R.layout.item_msg_order;
        }
        return R.layout.item_msg_sys;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        Msg msg = mDataList.get(position);
        if(type==Constants.SYS_MSG){
            TextView txtTime=holder.getView(R.id.txt_time);
            TextView txtTitle=holder.getView(R.id.txt_title);
            TextView txtContent=holder.getView(R.id.txt_content);
            txtTime.setText(msg.getCreateTime().substring(0,16));
            txtTitle.setText(msg.getTitle());
            txtContent.setText(msg.getContent());
        }else if(type==Constants.EXPRESS_MSG){
            TextView txtTime=holder.getView(R.id.txt_time);
            TextView txtTitle=holder.getView(R.id.txt_title);
            TextView txtContent=holder.getView(R.id.txt_content);
            ImageView imageView=holder.getView(R.id.img_goods);
           // TextView txtExpressNo=holder.getView(R.id.txt_express_no);
            txtTime.setText(msg.getCreateTime().substring(2,10));
            txtTitle.setText(msg.getLogiTitle());
            txtContent.setText(msg.getContent());
            ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+msg.getPicUrl(),imageView, DisplayImageOptionsUtil.getOptions());
        }else if(type==Constants.ORDER_MSG){
            TextView txtTime=holder.getView(R.id.txt_time);
            TextView txtTitle=holder.getView(R.id.txt_title);
            TextView txtContent=holder.getView(R.id.txt_content);
            ImageView imageView=holder.getView(R.id.img_goods);
            txtTime.setText(msg.getCreateTime().substring(2,10));
            txtTitle.setText(msg.getLogiTitle());
            txtContent.setText(msg.getContent());
            ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+msg.getPicUrl(),imageView, DisplayImageOptionsUtil.getOptions());
        }
    }
}