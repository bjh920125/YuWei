package com.bap.yuwei.entity.event;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/11/9.
 */

public class CartMoneyEvent {

    public BigDecimal money;

    public CartMoneyEvent(){}

    public CartMoneyEvent(BigDecimal money){
        this.money=money;
    }
}

