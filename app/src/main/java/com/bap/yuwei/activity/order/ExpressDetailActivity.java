package com.bap.yuwei.activity.order;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.commonadapter.CommonAdapter;
import com.bap.yuwei.adapter.commonadapter.ViewHolder;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.order.Express;
import com.bap.yuwei.entity.order.ExpressItem;
import com.bap.yuwei.entity.order.Orders;
import com.bap.yuwei.util.LogUtil;
import com.bap.yuwei.util.MyApplication;
import com.bap.yuwei.util.ThrowableUtil;
import com.bap.yuwei.util.ToastUtil;
import com.bap.yuwei.webservice.OrderWebService;
import com.linearlistview.LinearListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpressDetailActivity extends BaseActivity {


    private LinearListView lvExpress;
    private ImageView imgGoods;
    private TextView txtExpressStauts,txtCom,txtExpressNo;

    private Express mExpress;
    private List<ExpressItem> expressItems;
    private CommonAdapter<ExpressItem> mAdapter;

    private OrderWebService orderWebService;
    private Orders order;

    private int color;
    private int selectColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderWebService= MyApplication.getInstance().getWebService(OrderWebService.class);
        order= (Orders) getIntent().getSerializableExtra(Orders.KEY);
        color=getResources().getColor(R.color.darkgrey);
        selectColor=getResources().getColor(R.color.colorPrimary);

        expressItems=new ArrayList<>();
        mAdapter=new CommonAdapter<ExpressItem>(mContext,expressItems,R.layout.item_express) {
            @Override
            public void convert(ViewHolder viewHolder, ExpressItem item) {
                int position=viewHolder.getPosition();
                if(position==0){
                    viewHolder.setTextWithColor(R.id.txt_desc,item.getContext(),selectColor);
                    viewHolder.setTextWithColor(R.id.txt_time,item.getFtime(),selectColor);
                    viewHolder.setImageResource(R.id.img_dot,R.drawable.wuliu_yuan_fill);
                }else {
                    viewHolder.setTextWithColor(R.id.txt_desc,item.getContext(),color);
                    viewHolder.setTextWithColor(R.id.txt_time,item.getFtime(),color);
                    viewHolder.setImageResource(R.id.img_dot,R.drawable.wuliu_yuan);
                }
            }
        };
        lvExpress.setAdapter(mAdapter);
        getExpress();
    }

    /**
     * 获取快递信息
     */
    private void getExpress(){
        showLoadingDialog();
        Call<ResponseBody> call=orderWebService.getExpress(order.getOrderId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                try {
                    String result=response.body().string();
                    LogUtil.print("result",result);
                    mExpress=mGson.fromJson(result,Express.class);
                    initUIWithValues();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dismissProgressDialog();
                ToastUtil.showShort(mContext, ThrowableUtil.getErrorMsg(t));
            }
        });
    }

    /**
     * 初始化UI
     */
    private void initUIWithValues(){
        ImageLoader.getInstance().displayImage(Constants.PICTURE_URL+order.getOrderItems().get(0).getGoodsImage(),imgGoods);
        txtExpressStauts.setText(mExpress.getStatusText());
        txtCom.setText("承运公司："+mExpress.getComName());
        txtExpressNo.setText("运单号："+mExpress.getNu());
        expressItems.clear();
        expressItems.addAll(mExpress.getData());
        mAdapter.notifyDataSetChanged();
    }



    @Override
    protected int getLayoutId() {
        return R.layout.activity_express_detail;
    }

    @Override
    protected void initView() {
        lvExpress= (LinearListView) findViewById(R.id.lv_express);
        imgGoods= (ImageView) findViewById(R.id.img_goods);
        txtExpressStauts= (TextView) findViewById(R.id.txt_status);
        txtCom= (TextView) findViewById(R.id.txt_express_com);
        txtExpressNo= (TextView) findViewById(R.id.txt_express_no);
    }
}
