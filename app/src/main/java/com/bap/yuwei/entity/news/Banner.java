package com.bap.yuwei.entity.news;

/**
 * Created by Administrator on 2017/7/24.
 * 首页轮播图
 */
public class Banner {
    private Long bannerId;
    private String title;
    private String imageUrl;
    private String clickUrl;
    private Integer positionType;//0：首页轮播图   1：首页中间推荐图

    public Long getBannerId() {
        return bannerId;
    }

    public void setBannerId(Long bannerId) {
        this.bannerId = bannerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getClickUrl() {
        return clickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    public Integer getPositionType() {
        return positionType;
    }

    public void setPositionType(Integer positionType) {
        this.positionType = positionType;
    }
}
