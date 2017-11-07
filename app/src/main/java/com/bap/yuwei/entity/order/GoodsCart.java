package com.bap.yuwei.entity.order;

import java.math.BigDecimal;

/**
 * 购物车
 */
public class GoodsCart {

    /**
     * 主键ID
     */
    private Long goodsCartId;
    /**
     * 店铺ID
     */
    private Long shopId;
    /**
     * 店铺名称
     */
    private String shopName;
    /**
     * 商品ID
     */
    private Long goodsId;
    /**
     * 商品标题
     */
    private String title;
    /**
     * 类目名称
     */
    private String categoryName;
    /**
     * 型号 分类
     */
    private String model;
    /**
     * 商品主图
     */
    private String goodsImage;
    /**
     * 原价
     */
    private BigDecimal price;
    /**
     * 优惠价
     */
    private BigDecimal preferentialPrice;
    /**
     * 商品数量
     */
    private Integer goodsCount;
    /**
     * 小计金额
     */
    private BigDecimal subTotal;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 创建时间
     */
    private String createTime;

    private Boolean isValid;

    /**
     * 运费
     */
    private BigDecimal freight;

    private Integer maxQuantity;//最大库存数量,不存在数据库,直接查询回去

    public Long getGoodsCartId() {
        return goodsCartId;
    }

    public void setGoodsCartId(Long goodsCartId) {
        this.goodsCartId = goodsCartId;
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

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
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

    public Integer getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Integer goodsCount) {
        this.goodsCount = goodsCount;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    public Integer getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
    }
}
