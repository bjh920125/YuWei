package com.bap.yuwei.entity.event;

import com.bap.yuwei.entity.order.UserInvoice;

/**
 * Created by Administrator on 2017/11/13.
 */

public class ChooseInvoiceEvent {

    public UserInvoice userInvoice;

    public ChooseInvoiceEvent() {
    }

    public ChooseInvoiceEvent(UserInvoice userInvoice) {
        this.userInvoice = userInvoice;
    }
}
