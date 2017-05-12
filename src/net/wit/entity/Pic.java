/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity - 管理员
 * 
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_pic")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_pic_sequence")
public class Pic extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

	private Long tenantId;
	private String picType;
	private Date uploadDate;
	private String picName;
	private String largeUrl;
	private String mediumUrl;
	private String smallUrl;
	private int largeWidth;
	
	private int largeHeight;
	
	private int mediumWidth;
	
	private int mediumHeight;

	private int thumbnailWidth;
	
	private int thumbnailHeight;
	
	private String remark;

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public String getPicType() {
		return picType;
	}

	public void setPicType(String picType) {
		this.picType = picType;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getPicName() {
		return picName;
	}

	public void setPicName(String picName) {
		this.picName = picName;
	}

	public String getLargeUrl() {
		return largeUrl;
	}

	public void setLargeUrl(String largeUrl) {
		this.largeUrl = largeUrl;
	}

	public String getMediumUrl() {
		return mediumUrl;
	}

	public void setMediumUrl(String mediumUrl) {
		this.mediumUrl = mediumUrl;
	}

	public String getSmallUrl() {
		return smallUrl;
	}

	public void setSmallUrl(String smallUrl) {
		this.smallUrl = smallUrl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getLargeWidth() {
		return largeWidth;
	}

	public void setLargeWidth(int largeWidth) {
		this.largeWidth = largeWidth;
	}

	public int getLargeHeight() {
		return largeHeight;
	}

	public void setLargeHeight(int largeHeight) {
		this.largeHeight = largeHeight;
	}

	public int getMediumWidth() {
		return mediumWidth;
	}

	public void setMediumWidth(int mediumWidth) {
		this.mediumWidth = mediumWidth;
	}

	public int getMediumHeight() {
		return mediumHeight;
	}

	public void setMediumHeight(int mediumHeight) {
		this.mediumHeight = mediumHeight;
	}

	public int getThumbnailWidth() {
		return thumbnailWidth;
	}

	public void setThumbnailWidth(int thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
	}

	public int getThumbnailHeight() {
		return thumbnailHeight;
	}

	public void setThumbnailHeight(int thumbnailHeight) {
		this.thumbnailHeight = thumbnailHeight;
	}

}