/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * Entity - 地理信息经纬度
 * @author rsico Team
 * @version 3.0
 */
@Embeddable
public class Location implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 经度 */
	private BigDecimal x;

	/** 纬度 */
	private BigDecimal y;

	/** 最后一次更新时间 */
	private Date locationUpdateDate;

	/** 地区 */
	private Area area;

	/**
	 * 获取经度
	 * @return 经度
	 */
	@Column(name = "location_x", precision = 28, scale = 8)
	public BigDecimal getX() {
		return x;
	}

	/**
	 * 设置经度
	 * @param x 经度
	 */
	public void setX(BigDecimal x) {
		this.x = x;
	}

	/**
	 * 获取纬度
	 * @return 纬度
	 */
	@Column(name = "location_y", precision = 28, scale = 8)
	public BigDecimal getY() {
		return y;
	}

	/**
	 * 设置纬度
	 * @param y 纬度
	 */
	public void setY(BigDecimal y) {
		this.y = y;
	}

	/** 最后一次更新时间 */
	public Date getLocationUpdateDate() {
		return locationUpdateDate;
	}

	/** 最后一次更新时间 */
	public void setLocationUpdateDate(Date locationUpdateDate) {
		this.locationUpdateDate = locationUpdateDate;
	}

	/**
	 * 获取地区
	 * @return 地区
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "location_area")
	public Area getArea() {
		return area;
	}

	/**
	 * 设置地区
	 * @param area 地区
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	@Transient
	public boolean isExists() {
		return this.x != null && this.y != null;
	}
}