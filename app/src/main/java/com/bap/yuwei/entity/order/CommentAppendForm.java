package com.bap.yuwei.entity.order;

import java.util.List;

public class CommentAppendForm {

    private Long orderId;
    private List<CommentAppendItemForm> evaluations;

    public List<CommentAppendItemForm> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<CommentAppendItemForm> evaluations) {
        this.evaluations = evaluations;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
