package com.bap.yuwei.entity;

import java.io.Serializable;

public class BaseAttachment implements Serializable {
	public static final String KEY="attachment.key";
	public static final String POSITION="position";
	public static final String DELETE_FLAG="delete.flag";
	
	public static final int PICTURE= 10;//图片
	public static final int VIDEO=20;//视频
	public static final int DOCUMENT=30;//文件
	
	private String id;
	private String site;//附件地址
	private String thumbnailSite;//缩略图地址
	private int timeLength;//时长
	private int type;
	private int fileOrder;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getThumbnailSite() {
		return thumbnailSite;
	}
	public void setThumbnailSite(String thumbnailSite) {
		this.thumbnailSite = thumbnailSite;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getTimeLength() {
		return timeLength;
	}
	public void setTimeLength(int timeLength) {
		this.timeLength = timeLength;
	}

	public int getFileOrder() {
		return fileOrder;
	}

	public void setFileOrder(int fileOrder) {
		this.fileOrder = fileOrder;
	}


}
