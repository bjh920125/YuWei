package com.bap.yuwei.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.order.OrderItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import static com.bap.yuwei.entity.Constants.REFUND_MONEY;
import static com.bap.yuwei.entity.Constants.REFUND_MONEY_AND_GOODS;

public class ChooseRefundTypeActivity extends BaseActivity {

    private ImageView imgGoods;
    private TextView txtGoods,txtModel;
    private OrderItem orderItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderItem= (OrderItem) getIntent().getSerializableExtra(OrderItem.KEY);

        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+orderItem.getGoodsImage(),imgGoods);
        txtGoods.setText(orderItem.getTitle());
        txtModel.setText("类型"+orderItem.getModel());
    }

    public void chooseType(View v){
        Integer type=null;
        switch (v.getId()){
            case R.id.rl_only_money:
                type=REFUND_MONEY;
                break;
            case R.id.rl_money_goods:
                type=REFUND_MONEY_AND_GOODS;
                break;
            default:break;
        }

        Intent i=new Intent(mContext,RefundApplyActivity.class);
        orderItem.setRefundType(type);
        i.putExtra(OrderItem.KEY,orderItem);
        startActivity(i);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose_refund_type;
    }

    @Override
    public void initView() {
        imgGoods= (ImageView) findViewById(R.id.img_goods);
        txtGoods= (TextView) findViewById(R.id.txt_goods_name);
        txtModel= (TextView) findViewById(R.id.txt_model);
    }
}
