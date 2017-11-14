package com.bap.yuwei.entity.order;

import java.util.List;

public class PayOrderForm {

    private String buyerName;
    private String buyerPhone;
    private String buyerAddress;
    private Integer payType;
    private List<OrderItemForm> shopItems;
    private Long userId;
    private Integer from; //订单来源 0表示立即购买 1表示从购物车
    private Long[] cartIds;
    private Long invoiceId;


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

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public List<OrderItemForm> getShopItems() {
        return shopItems;
    }

    public void setShopItems(List<OrderItemForm> shopItems) {
        this.shopItems = shopItems;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Long[] getCartIds() {
        return cartIds;
    }

    public void setCartIds(Long[] cartIds) {
        this.cartIds = cartIds;
    }

}
