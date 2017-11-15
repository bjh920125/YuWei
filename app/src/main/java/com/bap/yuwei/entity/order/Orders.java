package com.bap.yuwei.entity.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import static com.bap.yuwei.entity.Constants.ORDER_3;
import static com.bap.yuwei.entity.Constants.ORDER_8;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_CLOSED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_HAS_CANCELED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_HAS_COMPLETED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_HAS_SENDED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_PENDING_PAY;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_PRE_DELIVERED;
import static com.bap.yuwei.entity.Constants.ORDER_STATUS_PRE_EVALUATED;

/**
 * Created by BAP0004 on 2017/8/25.
 */
public class Orders implements Serializable{

    public static final String KEY="orders.key";

    /**
     * 订单ID 也是订单号
     */
    private Long orderId;

    private String buyerName;
    private String buyerPhone;
    private String buyerAddress;
    /**
     * 支付方式 0 在线支付 1 其他
     */
    private Integer payType;
    /**
     * 配送方式 0 快递 1 其他
     */
    private Integer deliveryType;
    private BigDecimal freight;
    private BigDecimal payAmount;
    private Long shopId;
    private String shopName;
    private String createTime;
    private Integer status;
    private Long userId;
    private String username;
    private Long invoiceId;
    private String payTime;
    private String deliveryTime;
    private String dealTime;
    private BigDecimal realPayAmount;
    private BigDecimal modifiedPrice;
    private String cancelReason;
    private String buyerMessage;
    private String shopIcon;
    private Integer totalGoodsCount;
    private String qq;
    private String alipayTradeNo;
    private String evaluateTime;
    //评价状态 0待评价 1 买家已评 2 卖家已评 3双方已评
    private Integer evaluateStatus;
    private Boolean canAppendEvaluate;
    private Integer hasReceivedGoods;//已收过货  0：否  1：是
    private String shopPhone;
    private List<OrderItem> orderItems;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }


    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }

    public BigDecimal getRealPayAmount() {
        return realPayAmount;
    }

    public void setRealPayAmount(BigDecimal realPayAmount) {
        this.realPayAmount = realPayAmount;
    }

    public BigDecimal getModifiedPrice() {
        return modifiedPrice;
    }

    public void setModifiedPrice(BigDecimal modifiedPrice) {
        this.modifiedPrice = modifiedPrice;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }

    public String getShopIcon() {
        return shopIcon;
    }

    public void setShopIcon(String shopIcon) {
        this.shopIcon = shopIcon;
    }

    public Integer getTotalGoodsCount() {
        return totalGoodsCount;
    }

    public void setTotalGoodsCount(Integer totalGoodsCount) {
        this.totalGoodsCount = totalGoodsCount;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getAlipayTradeNo() {
        return alipayTradeNo;
    }

    public void setAlipayTradeNo(String alipayTradeNo) {
        this.alipayTradeNo = alipayTradeNo;
    }

    public String getEvaluateTime() {
        return evaluateTime;
    }

    public void setEvaluateTime(String evaluateTime) {
        this.evaluateTime = evaluateTime;
    }

    public Integer getEvaluateStatus() {
        return evaluateStatus;
    }

    public void setEvaluateStatus(Integer evaluateStatus) {
        this.evaluateStatus = evaluateStatus;
    }

    public Boolean getCanAppendEvaluate() {
        return canAppendEvaluate;
    }

    public void setCanAppendEvaluate(Boolean canAppendEvaluate) {
        this.canAppendEvaluate = canAppendEvaluate;
    }

    public Integer getHasReceivedGoods() {
        return hasReceivedGoods;
    }

    public void setHasReceivedGoods(Integer hasReceivedGoods) {
        this.hasReceivedGoods = hasReceivedGoods;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public String getStatusText() {
        String result="";
        switch (status){
            case ORDER_STATUS_PENDING_PAY:
                result="等待买家付款";
            break;
            case ORDER_STATUS_PRE_DELIVERED:
                result="买家已付款";
                break;
            case ORDER_STATUS_HAS_SENDED:
                result="卖家已发货";
                break;
            case ORDER_3:
                result="退款中";
                break;
            case ORDER_STATUS_PRE_EVALUATED:
                result="交易成功";
                break;
            case ORDER_STATUS_HAS_COMPLETED:
                result="已完成";
                break;
            case ORDER_STATUS_CLOSED:
                result="已关闭";
                break;
            case ORDER_STATUS_HAS_CANCELED:
                result="已取消";
                break;
            case ORDER_8:
                result="已退款";
                break;
            default:break;
        }
        return result;
    }
}
