package com.bap.yuwei.entity.event;

import com.bap.yuwei.entity.sys.ShippingAddress;

/**
 * Created by Administrator on 2017/11/10.
 */

public class ReceiverAddressEvent {

    public ShippingAddress shippingAddress;

    public ReceiverAddressEvent() {
    }

    public ReceiverAddressEvent(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
