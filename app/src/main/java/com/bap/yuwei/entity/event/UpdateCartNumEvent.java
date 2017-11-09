package com.bap.yuwei.entity.event;

/**
 * Created by Administrator on 2017/11/9.
 */

public class UpdateCartNumEvent {

    public int cartNum;

    public UpdateCartNumEvent() {
    }

    public UpdateCartNumEvent(int cartNum) {
        this.cartNum = cartNum;
    }
}
