package com.bap.yuwei.entity.order;

import java.io.Serializable;

/**
 * 发票信息
 * Created by BAP0004 on 2017/8/25.
 */
public class Invoice implements Serializable {

    /**
            * 主键ID
     */
    private Long invoiceId;
    /**
     * 发票类型 0 普通发票 1 电子发票 2 增值税发票
     */
    private Integer type;

    /**
     * 发票抬头类型  0个人 1 公司
     */
    private Integer headerType;
    /**
     * 发票抬头
     */
    private String header;
    /**
     * 纳税人识别码
     */
    private String taxpayerNumber;
    /**
     * 发票内容
     */
    private String content;
    /**
     * 手机号码
     */
    private String cellphone;
    /**
     * 电子邮箱
     */
    private String email;
    /**
     * 收票人名称
     */
    private String name;
    /**
     * 收票人所在省
     */
    private String province;
    /**
     * 收票人所在市
     */
    private String city;
    /**
     * 收票人所在区
     */
    private String area;
    /**
     * 收票详细地址
     */
    private String address;
    /**
     * 开票方式 0：订单完成后开票
     */
    private Integer invoiceMode;

    /**
     *公司注册名称
     */
    private String companyName;
    /**
     *公司注册地址
     */
    private String companyAddress;
    /**
     *公司注册电话
     */
    private String companyCellphone;
    /**
     *开户银行名称
     */
    private String bankName;
    /**
     *银行账户
     */
    private String bankAccount;
    /**
     *userid
     */
    private Long userId;

    /**
     * 创建时间
     */
    private String createTime;

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getTaxpayerNumber() {
        return taxpayerNumber;
    }

    public void setTaxpayerNumber(String taxpayerNumber) {
        this.taxpayerNumber = taxpayerNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getHeaderType() {
        return headerType;
    }

    public void setHeaderType(Integer headerType) {
        this.headerType = headerType;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Integer getInvoiceMode() {
        return invoiceMode;
    }

    public void setInvoiceMode(Integer invoiceMode) {
        this.invoiceMode = invoiceMode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyCellphone() {
        return companyCellphone;
    }

    public void setCompanyCellphone(String companyCellphone) {
        this.companyCellphone = companyCellphone;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
