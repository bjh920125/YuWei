package com.bap.yuwei.entity.order;

import com.bap.yuwei.entity.sys.ShopScore;

/**
 * Created by Administrator on 2017/11/14.
 */

public class OrderDetail {

    private Orders orders;
    private Logistics logistics;
    private Invoice invoice;
    private ShopScore shopScore;

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public Logistics getLogistics() {
        return logistics;
    }

    public void setLogistics(Logistics logistics) {
        this.logistics = logistics;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public ShopScore getShopScore() {
        return shopScore;
    }

    public void setShopScore(ShopScore shopScore) {
        this.shopScore = shopScore;
    }
}
