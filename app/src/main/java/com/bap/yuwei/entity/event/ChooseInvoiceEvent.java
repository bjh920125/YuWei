package com.bap.yuwei.entity.event;

import com.bap.yuwei.entity.order.UserInvoice;

/**
 * 选择发票event
 */
public class ChooseInvoiceEvent {

    public UserInvoice userInvoice;

    public ChooseInvoiceEvent() {
    }

    public ChooseInvoiceEvent(UserInvoice userInvoice) {
        this.userInvoice = userInvoice;
    }
}
