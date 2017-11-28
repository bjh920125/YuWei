package com.bap.yuwei.entity.event;

import com.bap.yuwei.entity.order.GoodsCart;

/**
 * Created by Administrator on 2017/11/9.
 */

public class UpdateCartEvent {

    public Long cartId;
    public int num;
    public GoodsCart cart;

    public UpdateCartEvent() {
    }

    public UpdateCartEvent(GoodsCart cart,Long cartId, int num) {
        this.cart=cart;
        this.cartId = cartId;
        this.num = num;
    }
}
