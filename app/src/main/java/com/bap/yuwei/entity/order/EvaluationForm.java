package com.bap.yuwei.entity.order;

import java.util.List;

public class EvaluationForm {

    private Long userId;
    private String username;
    private Long shopId;
    private String shopName;
    private Integer serviceScore;
    private Integer logisticsScore;
    private Long orderId;
    private List<EvaluateItemForm> items;
    private Integer evaluateFrom;
    private Boolean isAllAnonymous;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Integer getServiceScore() {
        return serviceScore;
    }

    public void setServiceScore(Integer serviceScore) {
        this.serviceScore = serviceScore;
    }

    public Integer getLogisticsScore() {
        return logisticsScore;
    }

    public void setLogisticsScore(Integer logisticsScore) {
        this.logisticsScore = logisticsScore;
    }

    public List<EvaluateItemForm> getItems() {
        return items;
    }

    public void setItems(List<EvaluateItemForm> items) {
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Integer getEvaluateFrom() {
        return evaluateFrom;
    }

    public void setEvaluateFrom(Integer evaluateFrom) {
        this.evaluateFrom = evaluateFrom;
    }

    public Boolean getIsAllAnonymous() {
        return isAllAnonymous;
    }

    public void setIsAllAnonymous(Boolean isAllAnonymous) {
        this.isAllAnonymous = isAllAnonymous;
    }
}
