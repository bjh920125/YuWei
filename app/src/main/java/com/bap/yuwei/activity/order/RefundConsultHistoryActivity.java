package com.bap.yuwei.activity.order;

import android.os.Bundle;
import android.widget.ListView;

import com.bap.yuwei.R;
import com.bap.yuwei.activity.base.BaseActivity;
import com.bap.yuwei.adapter.commonadapter.CommonAdapter;
import com.bap.yuwei.adapter.commonadapter.ViewHolder;
import com.bap.yuwei.entity.Constants;
import com.bap.yuwei.entity.order.Refund;
import com.bap.yuwei.entity.order.RefundConsultHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * 退款协商历史
 */
public class RefundConsultHistoryActivity extends BaseActivity {

    private ListView lvHistory;

    private List<RefundConsultHistory> histories;
    private CommonAdapter<RefundConsultHistory> mAdapter;

    private Refund refund;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refund= (Refund) getIntent().getSerializableExtra(Refund.KEY);
        histories=new ArrayList<>();
        histories.addAll(refund.getHistories());
        mAdapter=new CommonAdapter<RefundConsultHistory>(mContext,histories,R.layout.item_refund_consult) {
            @Override
            public void convert(ViewHolder viewHolder, RefundConsultHistory item) {
                viewHolder.setImageByUrl(R.id.img_head,Constants.PICTURE_URL+item.getHeadImage());
                viewHolder.setText(R.id.txt_user_name,item.getUsername());
                viewHolder.setText(R.id.txt_time,item.getCreateTime());
                viewHolder.setHtmlText(R.id.txt_content,item.getHistoryDesc());
            }
        };
        lvHistory.setAdapter(mAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refund_consult_history;
    }

    @Override
    protected void initView() {
        lvHistory= (ListView) findViewById(R.id.lv_history);
    }
}
