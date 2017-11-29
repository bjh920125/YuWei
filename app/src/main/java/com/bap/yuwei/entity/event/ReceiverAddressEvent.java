package com.bap.yuwei.entity.event;

import com.bap.yuwei.entity.sys.ShippingAddress;

/**
 * 收货地址event
 */

public class ReceiverAddressEvent {

    public ShippingAddress shippingAddress;

    public ReceiverAddressEvent() {
    }

    public ReceiverAddressEvent(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
