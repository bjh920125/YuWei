package com.bap.yuwei.entity.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 确认订单
 */
public class OrderEnsure implements Serializable{

    public static final String KEY="orderensure.key";

    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private BigDecimal totalFreight;
    private String[] invoiceTypes;
    private List<InvoiceContent> invoiceContents;
    private UserInvoice invoice;
    private int totalRecord;
    private List<OrderShop> shopItems;

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public BigDecimal getTotalFreight() {
        return totalFreight;
    }

    public void setTotalFreight(BigDecimal totalFreight) {
        this.totalFreight = totalFreight;
    }

    public String[] getInvoiceTypes() {
        return invoiceTypes;
    }

    public void setInvoiceTypes(String[] invoiceTypes) {
        this.invoiceTypes = invoiceTypes;
    }

    public List<InvoiceContent> getInvoiceContents() {
        return invoiceContents;
    }

    public void setInvoiceContents(List<InvoiceContent> invoiceContents) {
        this.invoiceContents = invoiceContents;
    }

    public UserInvoice getInvoice() {
        return invoice;
    }

    public void setInvoice(UserInvoice invoice) {
        this.invoice = invoice;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    public List<OrderShop> getShopItems() {
        return shopItems;
    }

    public void setShopItems(List<OrderShop> shopItems) {
        this.shopItems = shopItems;
    }
}
