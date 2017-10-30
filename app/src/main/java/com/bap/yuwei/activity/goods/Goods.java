package com.bap.yuwei.activity.goods;

import java.math.BigDecimal;

public class Goods {
    private Long goodsId;

    /**
     * 商品类型 0 全新
     */
    private Integer goodsType;

    /**
     * 生产日期开始
     */
    private String productionDateStart;

    /**
     * 生产日期结束
     */
    private String productionDateEnd;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品卖点
     */
    private String sellingPoint;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 优惠价
     */
    private BigDecimal preferentialPrice;

    /**
     * 优惠有效期
     */
    private Integer expirationDate;

    /**
     * 产品数量
     */
    private Integer quantity;

    /**
     * 采购地
     */
    private Integer purchasePlace;

    /**
     * 商品编码
     */
    private String merchantCode;

    /**
     * 商品条形码
     */
    private String barCode;

    private String grossWeight;

    private Integer suitableFor;

    private String features;

    private String specification;

    private BigDecimal freight;

    private Integer guarantee;

    private Integer returnExchangePromise;

    private Integer serviceGuarantee;

    private Integer inventoryCount;

    private Integer startTime;

    private String goodsStartTime;

    private String createTime;

    private Integer reviewStatus;//审核状态 0 未审核 1已通过 2未通过

    private Long categoryId;

    private String categoryNodes;

    private Long shopId;

    private String reason;

    private String reviewTime;

    private Integer showwindowRecommend;

    private Integer sellStatus;//销售状态 -1;从未上架   0;即将上架  1;仓库中  2;已上架(出售中） 3;售完下架  4;我下架的（手动下架）

    private Integer inDiscount;

    private BigDecimal publishPreferentialPrice;

    private String goodsImage;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Integer goodsType) {
        this.goodsType = goodsType;
    }

    public String getProductionDateStart() {
        return productionDateStart;
    }

    public void setProductionDateStart(String productionDateStart) {
        this.productionDateStart = productionDateStart == null ? null : productionDateStart.trim();
    }

    public String getProductionDateEnd() {
        return productionDateEnd;
    }

    public void setProductionDateEnd(String productionDateEnd) {
        this.productionDateEnd = productionDateEnd == null ? null : productionDateEnd.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getSellingPoint() {
        return sellingPoint;
    }

    public void setSellingPoint(String sellingPoint) {
        this.sellingPoint = sellingPoint == null ? null : sellingPoint.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPreferentialPrice() {
        return preferentialPrice;
    }

    public void setPreferentialPrice(BigDecimal preferentialPrice) {
        this.preferentialPrice = preferentialPrice;
    }

    public Integer getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Integer expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPurchasePlace() {
        return purchasePlace;
    }

    public void setPurchasePlace(Integer purchasePlace) {
        this.purchasePlace = purchasePlace;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode == null ? null : merchantCode.trim();
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode == null ? null : barCode.trim();
    }

    public String getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(String grossWeight) {
        this.grossWeight = grossWeight == null ? null : grossWeight.trim();
    }

    public Integer getSuitableFor() {
        return suitableFor;
    }

    public void setSuitableFor(Integer suitableFor) {
        this.suitableFor = suitableFor;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features == null ? null : features.trim();
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification == null ? null : specification.trim();
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    public Integer getGuarantee() {
        return guarantee;
    }

    public void setGuarantee(Integer guarantee) {
        this.guarantee = guarantee;
    }

    public Integer getReturnExchangePromise() {
        return returnExchangePromise;
    }

    public void setReturnExchangePromise(Integer returnExchangePromise) {
        this.returnExchangePromise = returnExchangePromise;
    }

    public Integer getServiceGuarantee() {
        return serviceGuarantee;
    }

    public void setServiceGuarantee(Integer serviceGuarantee) {
        this.serviceGuarantee = serviceGuarantee;
    }

    public Integer getInventoryCount() {
        return inventoryCount;
    }

    public void setInventoryCount(Integer inventoryCount) {
        this.inventoryCount = inventoryCount;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public String getGoodsStartTime() {
        return goodsStartTime;
    }

    public void setGoodsStartTime(String goodsStartTime) {
        this.goodsStartTime = goodsStartTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(Integer reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(String reviewTime) {
        this.reviewTime = reviewTime;
    }

    public Integer getShowwindowRecommend() {
        return showwindowRecommend;
    }

    public void setShowwindowRecommend(Integer showwindowRecommend) {
        this.showwindowRecommend = showwindowRecommend;
    }

    public Integer getSellStatus() {
        return sellStatus;
    }

    public void setSellStatus(Integer sellStatus) {
        this.sellStatus = sellStatus;
    }

    public Integer getInDiscount() {
        return inDiscount;
    }

    public void setInDiscount(Integer inDiscount) {
        this.inDiscount = inDiscount;
    }

    public BigDecimal getPublishPreferentialPrice() {
        return publishPreferentialPrice;
    }

    public void setPublishPreferentialPrice(BigDecimal publishPreferentialPrice) {
        this.publishPreferentialPrice = publishPreferentialPrice;
    }

    public String getCategoryNodes() {
        return categoryNodes;
    }

    public void setCategoryNodes(String categoryNodes) {
        this.categoryNodes = categoryNodes;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
    }
}