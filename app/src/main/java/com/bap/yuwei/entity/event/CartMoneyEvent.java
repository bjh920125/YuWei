package com.bap.yuwei.entity.event;

import java.math.BigDecimal;

/**
 * 计算购物车价格event
 */
public class CartMoneyEvent {

    public BigDecimal money;

    public CartMoneyEvent(){}

    public CartMoneyEvent(BigDecimal money){
        this.money=money;
    }
}

