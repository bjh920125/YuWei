package com.bap.yuwei.entity.event;

/**
 * Created by Administrator on 2017/11/9.
 */

public class UpdateCartEvent {

    public Long cartId;
    public int num;

    public UpdateCartEvent() {
    }

    public UpdateCartEvent(Long cartId, int num) {
        this.cartId = cartId;
        this.num = num;
    }
}
