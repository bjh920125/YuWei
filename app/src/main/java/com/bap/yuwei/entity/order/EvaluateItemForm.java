package com.bap.yuwei.entity.order;

import java.math.BigDecimal;
import java.util.List;

public class EvaluateItemForm {

    private Long goodsId;
    private Integer goodsScore;
    private String evaluateComment;
    private String goodsTitle;
    private BigDecimal price;
    private String[] evaluationImages;
    private Boolean isAnonymous;
    private String model;
    private transient  List<String> filePathes;

    public List<String> getFilePathes() {
        return filePathes;
    }

    public void setFilePathes(List<String> filePathes) {
        this.filePathes = filePathes;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getGoodsScore() {
        return goodsScore;
    }

    public void setGoodsScore(Integer goodsScore) {
        this.goodsScore = goodsScore;
    }

    public String getEvaluateComment() {
        return evaluateComment;
    }

    public void setEvaluateComment(String evaluateComment) {
        this.evaluateComment = evaluateComment;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
