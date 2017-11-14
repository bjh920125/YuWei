package com.bap.yuwei.entity.sys;

import java.math.BigDecimal;

public class ShopScore {
    private Long shopScoreId;

    private BigDecimal goodsScore;

    private Integer goodsScoreNum;

    private BigDecimal serviceScore;

    private Integer serviceScoreNum;

    private BigDecimal logisticsScore;

    private Integer logisticsScoreNum;

    private Long shopId;

    private String createTime;

    public Long getShopScoreId() {
        return shopScoreId;
    }

    public void setShopScoreId(Long shopScoreId) {
        this.shopScoreId = shopScoreId;
    }

    public BigDecimal getGoodsScore() {
        return goodsScore;
    }

    public void setGoodsScore(BigDecimal goodsScore) {
        this.goodsScore = goodsScore;
    }

    public Integer getGoodsScoreNum() {
        return goodsScoreNum;
    }

    public void setGoodsScoreNum(Integer goodsScoreNum) {
        this.goodsScoreNum = goodsScoreNum;
    }

    public BigDecimal getServiceScore() {
        return serviceScore;
    }

    public void setServiceScore(BigDecimal serviceScore) {
        this.serviceScore = serviceScore;
    }

    public Integer getServiceScoreNum() {
        return serviceScoreNum;
    }

    public void setServiceScoreNum(Integer serviceScoreNum) {
        this.serviceScoreNum = serviceScoreNum;
    }

    public BigDecimal getLogisticsScore() {
        return logisticsScore;
    }

    public void setLogisticsScore(BigDecimal logisticsScore) {
        this.logisticsScore = logisticsScore;
    }

    public Integer getLogisticsScoreNum() {
        return logisticsScoreNum;
    }

    public void setLogisticsScoreNum(Integer logisticsScoreNum) {
        this.logisticsScoreNum = logisticsScoreNum;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}