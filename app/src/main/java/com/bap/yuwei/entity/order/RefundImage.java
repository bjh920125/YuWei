package com.bap.yuwei.entity.order;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/11.
 * 退款附件
 */
public class RefundImage implements Serializable{
    private Long refundImageId;
    private String imagePath;
    private Long refundId;
    private String createTime;

    public Long getRefundImageId() {
        return refundImageId;
    }

    public void setRefundImageId(Long refundImageId) {
        this.refundImageId = refundImageId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
