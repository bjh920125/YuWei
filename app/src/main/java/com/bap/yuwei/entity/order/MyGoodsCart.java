package com.bap.yuwei.entity.order;

import java.util.List;

/**
 * Created by Administrator on 2017/11/7.
 */

public class MyGoodsCart {

    private Long shopId;//店铺ID
    private String shopName;//店铺名称
    private List<GoodsCart> cartItems;
    private boolean isChecked;
    private int status;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
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

    public List<GoodsCart> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<GoodsCart> cartItems) {
        this.cartItems = cartItems;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
