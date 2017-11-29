package com.bap.yuwei.entity.event;

/**
 * 更新购物车数量event
 */

public class UpdateCartNumEvent {

    public int cartNum;

    public UpdateCartNumEvent() {
    }

    public UpdateCartNumEvent(int cartNum) {
        this.cartNum = cartNum;
    }
}
