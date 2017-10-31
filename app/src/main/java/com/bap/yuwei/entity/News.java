package com.bap.yuwei.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/21.
 */
public class News implements Serializable{
    public static final String KEY="news.key";


    private Long newsId;//新闻ID
    private String title;//标题
    private String body;//正文
    private Integer newsType;//类型  0：公告   1：特惠
    private Boolean isTop;//是否置顶
    private Integer readTimes;//阅读次数
    private Integer status;//状态  0:禁用   1：启用
    private String createTime;//创建时间


    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getNewsType() {
        return newsType;
    }

    public void setNewsType(Integer newsType) {
        this.newsType = newsType;
    }

    public Boolean getIsTop() {
        return isTop;
    }

    public void setIsTop(Boolean top) {
        isTop = top;
    }

    public Integer getReadTimes() {
        return readTimes;
    }

    public void setReadTimes(Integer readTimes) {
        this.readTimes = readTimes;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
