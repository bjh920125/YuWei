package com.bap.yuwei.entity.order;

import java.util.List;

public class OrderItemForm {

    private Integer deliveryType;
    private List<GoodsItemForm> goodsItems;
    private Long shopId;
    private String buyerMessage;

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public List<GoodsItemForm> getGoodsItems() {
        return goodsItems;
    }

    public void setGoodsItems(List<GoodsItemForm> goodsItems) {
        this.goodsItems = goodsItems;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }
}
