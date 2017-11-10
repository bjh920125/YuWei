package com.bap.yuwei.entity.order;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/28.
 */
public class InvoiceHeader implements Serializable {

    /**
     * 发票抬头类型  0个人 1 公司
     */
    private Integer headerType;
    /**
     * 发票抬头
     */
    private String header;

    public Integer getHeaderType() {
        return headerType;
    }

    public void setHeaderType(Integer headerType) {
        this.headerType = headerType;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}
