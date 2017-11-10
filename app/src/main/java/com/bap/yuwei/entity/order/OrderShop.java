package com.bap.yuwei.entity.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/11/10.
 */

public class OrderShop implements Serializable {

    private boolean isTakeMySelf;
    private BigDecimal shopPayAmount;
    private String takeSelfAddress;
    private String shopName;
    private String shopIcon;
    private BigDecimal shopTotalFreight;
    private Long shopId;
    private BigDecimal shopTotalAmount;
    private String buyerMsg;
    private List<GoodsCart> cartItems;

    public String getBuyerMsg() {
        return buyerMsg;
    }

    public void setBuyerMsg(String buyerMsg) {
        this.buyerMsg = buyerMsg;
    }

    public boolean isTakeMySelf() {
        return isTakeMySelf;
    }

    public void setTakeMySelf(boolean takeMySelf) {
        isTakeMySelf = takeMySelf;
    }

    public BigDecimal getShopPayAmount() {
        return shopPayAmount;
    }

    public void setShopPayAmount(BigDecimal shopPayAmount) {
        this.shopPayAmount = shopPayAmount;
    }

    public String getTakeSelfAddress() {
        return takeSelfAddress;
    }

    public void setTakeSelfAddress(String takeSelfAddress) {
        this.takeSelfAddress = takeSelfAddress;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopIcon() {
        return shopIcon;
    }

    public void setShopIcon(String shopIcon) {
        this.shopIcon = shopIcon;
    }

    public BigDecimal getShopTotalFreight() {
        return shopTotalFreight;
    }

    public void setShopTotalFreight(BigDecimal shopTotalFreight) {
        this.shopTotalFreight = shopTotalFreight;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public BigDecimal getShopTotalAmount() {
        return shopTotalAmount;
    }

    public void setShopTotalAmount(BigDecimal shopTotalAmount) {
        this.shopTotalAmount = shopTotalAmount;
    }

    public List<GoodsCart> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<GoodsCart> cartItems) {
        this.cartItems = cartItems;
    }
}
