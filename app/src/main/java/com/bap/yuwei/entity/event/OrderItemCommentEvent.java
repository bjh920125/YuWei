package com.bap.yuwei.entity.event;

import com.bap.yuwei.entity.order.EvaluateItemForm;

/**
 * Created by Administrator on 2017/11/21.
 */

public class OrderItemCommentEvent {

    public EvaluateItemForm itemForm;

    public OrderItemCommentEvent() {
    }

    public OrderItemCommentEvent(EvaluateItemForm itemForm) {
        this.itemForm = itemForm;
    }
}
