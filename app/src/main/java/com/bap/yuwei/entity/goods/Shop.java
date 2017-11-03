package com.bap.yuwei.entity.goods;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/12.
 */
public class Shop implements Serializable{
    public static final String KEY="shop.key";

    private Long shopId;//主键ID
    private String shopName;//店铺名称
    private String icon;//店铺图像
    private String headImage;//App店铺详情头部图片
    private String summary;//店铺简介
    private String address;//联系地址
    private Integer productResource;//主要货源
    private Boolean isTakeSelf;//是否支持自提 false 不支持  true：支持
    private Boolean isNightSend;//是否支持夜间发货  false 不支持  true：支持
    private String takeSelfAddress;//自提地址
    private String description;//店铺介绍
    private String qq;//QQ
    private String tel;//联系电话
    private String[] invoiceType;//发票类型 0 普通发票 1电子发票 2增值税发票
    private Integer taxpayerType;//纳税人性质 0：小规模纳税人  1：一般纳税人
    private String[] invoiceContent;//发票内容
    private Long companyId;//企业ID
    private Long userId;//用户ID
    private Integer businessStatus;//经营状态  1：正常营业 2：已冻结  3：已关店
    private String createTime;//门店开启时间
    private int shopCollectUserTotal;
    private String shopIcon;
    private int recentGoodsTotal;
    private int goodsTotal;
    private int goodCommentPercent;

    public int getGoodCommentPercent() {
        return goodCommentPercent;
    }

    public void setGoodCommentPercent(int goodCommentPercent) {
        this.goodCommentPercent = goodCommentPercent;
    }

    public int getGoodsTotal() {
        return goodsTotal;
    }

    public void setGoodsTotal(int goodsTotal) {
        this.goodsTotal = goodsTotal;
    }

    public int getShopCollectUserTotal() {
        return shopCollectUserTotal;
    }

    public void setShopCollectUserTotal(int shopCollectUserTotal) {
        this.shopCollectUserTotal = shopCollectUserTotal;
    }

    public String getShopIcon() {
        return shopIcon;
    }

    public void setShopIcon(String shopIcon) {
        this.shopIcon = shopIcon;
    }

    public int getRecentGoodsTotal() {
        return recentGoodsTotal;
    }

    public void setRecentGoodsTotal(int recentGoodsTotal) {
        this.recentGoodsTotal = recentGoodsTotal;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getProductResource() {
        return productResource;
    }

    public void setProductResource(Integer productResource) {
        this.productResource = productResource;
    }

    public Boolean getIsTakeSelf() {
        return isTakeSelf;
    }

    public void setIsTakeSelf(Boolean isTakeSelf) {
        this.isTakeSelf = isTakeSelf;
    }

    public Boolean getIsNightSend() {
        return isNightSend;
    }

    public void setIsNightSend(Boolean isNightSend) {
        this.isNightSend = isNightSend;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(Integer businessStatus) {
        this.businessStatus = businessStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTakeSelfAddress() {
        return takeSelfAddress;
    }

    public void setTakeSelfAddress(String takeSelfAddress) {
        this.takeSelfAddress = takeSelfAddress;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String[] getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String[] invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String[] getInvoiceContent() {
        return invoiceContent;
    }

    public void setInvoiceContent(String[] invoiceContent) {
        this.invoiceContent = invoiceContent;
    }

    public Integer getTaxpayerType() {
        return taxpayerType;
    }

    public void setTaxpayerType(Integer taxpayerType) {
        this.taxpayerType = taxpayerType;
    }


}
