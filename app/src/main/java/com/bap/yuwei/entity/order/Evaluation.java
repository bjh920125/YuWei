package com.bap.yuwei.entity.order;

import java.math.BigDecimal;

public class Evaluation {
    private Long evaluationId;

    private Integer type;

    private String evaluatedUsername;

    private Long evaluatedUserId;

    private String evaluaterUsername;

    private Long evaluaterUserId;

    private Integer evaluateFrom;

    private String goodsTitle;

    private BigDecimal price;

    private String updateTime;

    private String createTime;

    private String evaluationComment;

    private String additionalComment;

    private Long goodsId;

    private Long shopId;

    private String shopName;

    private Integer goodsScore;

    private String[] evaluationImages;

    private Boolean isAnonymous;

    private Boolean hasAdditionalComment;

    private Boolean hasImages;

    private Long orderId;

    private String reply;

    private Boolean hasReplied;

    private String replyTime;

    private String goodsModel;

    private String avatar;

    public Long getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(Long evaluationId) {
        this.evaluationId = evaluationId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getEvaluatedUsername() {
        return evaluatedUsername;
    }

    public void setEvaluatedUsername(String evaluatedUsername) {
        this.evaluatedUsername = evaluatedUsername == null ? null : evaluatedUsername.trim();
    }

    public Long getEvaluatedUserId() {
        return evaluatedUserId;
    }

    public void setEvaluatedUserId(Long evaluatedUserId) {
        this.evaluatedUserId = evaluatedUserId;
    }

    public String getEvaluaterUsername() {
        return evaluaterUsername;
    }

    public void setEvaluaterUsername(String evaluaterUsername) {
        this.evaluaterUsername = evaluaterUsername == null ? null : evaluaterUsername.trim();
    }

    public Long getEvaluaterUserId() {
        return evaluaterUserId;
    }

    public void setEvaluaterUserId(Long evaluaterUserId) {
        this.evaluaterUserId = evaluaterUserId;
    }

    public Integer getEvaluateFrom() {
        return evaluateFrom;
    }

    public void setEvaluateFrom(Integer evaluateFrom) {
        this.evaluateFrom = evaluateFrom;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle == null ? null : goodsTitle.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEvaluationComment() {
        return evaluationComment;
    }

    public void setEvaluationComment(String evaluationComment) {
        this.evaluationComment = evaluationComment == null ? null : evaluationComment.trim();
    }

    public String getAdditionalComment() {
        return additionalComment;
    }

    public void setAdditionalComment(String additionalComment) {
        this.additionalComment = additionalComment == null ? null : additionalComment.trim();
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
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

    public Integer getGoodsScore() {
        return goodsScore;
    }

    public void setGoodsScore(Integer goodsScore) {
        this.goodsScore = goodsScore;
    }

    public String[] getEvaluationImages() {
        return evaluationImages;
    }

    public void setEvaluationImages(String[] evaluationImages) {
        this.evaluationImages = evaluationImages;
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public Boolean getHasAdditionalComment() {
        return hasAdditionalComment;
    }

    public void setHasAdditionalComment(Boolean hasAdditionalComment) {
        this.hasAdditionalComment = hasAdditionalComment;
    }

    public Boolean getHasImages() {
        return hasImages;
    }

    public void setHasImages(Boolean hasImages) {
        this.hasImages = hasImages;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Boolean getHasReplied() {
        return hasReplied;
    }

    public void setHasReplied(Boolean hasReplied) {
        this.hasReplied = hasReplied;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getGoodsModel() {
        return goodsModel;
    }

    public void setGoodsModel(String goodsModel) {
        this.goodsModel = goodsModel;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}