package com.bap.yuwei.entity.order;

import java.io.Serializable;

public class InvoiceContent implements Serializable {
    private Long invoiceCotentId;

    private String content;

    private Integer status;

    private String description;

    private String createTime;

    public Long getInvoiceCotentId() {
        return invoiceCotentId;
    }

    public void setInvoiceCotentId(Long invoiceCotentId) {
        this.invoiceCotentId = invoiceCotentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}