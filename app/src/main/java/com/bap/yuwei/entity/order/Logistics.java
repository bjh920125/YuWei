package com.bap.yuwei.entity.order;

/**
 * 物流信息表
 * Created by BAP0004 on 2017/8/30.
 */
public class Logistics {

    /**
     * 主键ID
     */
    private Long logisticsId;
    /**
     * 收货地址
     */
    private String address;
    /**
     * 运送方式
     */
    private Integer deliveryType;
    /**
     * 物流公司
     */
    private String logisticsCompany;

    /**
     * 物流公司Code
     */
    private String logisticsCompanyCode;

    /**
     * 运单号
     */
    private String waybillNo;
    /**
     * 买家留言
     */
    private String buyerMessage;
    /**
     * 所属订单ID
     */
    private Long orderId;
    /**
     * 创建时间
     */
    private String createTime;

    public Long getLogisticsId() {
        return logisticsId;
    }

    public void setLogisticsId(Long logisticsId) {
        this.logisticsId = logisticsId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }


    public String getLogisticsCompany() {
        return logisticsCompany;
    }

    public void setLogisticsCompany(String logisticsCompany) {
        this.logisticsCompany = logisticsCompany;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLogisticsCompanyCode() {
        return logisticsCompanyCode;
    }

    public void setLogisticsCompanyCode(String logisticsCompanyCode) {
        this.logisticsCompanyCode = logisticsCompanyCode;
    }
}
