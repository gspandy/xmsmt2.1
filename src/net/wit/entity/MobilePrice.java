/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity - 序列号
 * 
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_mobile_price")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_mobile_price_sequence")
public class MobilePrice extends BaseEntity {

	private static final long serialVersionUID = -2330598644835706164L;
	
	/** 地区*/
	private Area area;

	/** 运营商  */
	private Integer providers;
	
	/** 面值 */
	private Integer denomination;
	
	/** 售价 */
	private BigDecimal price;

	/** 进价*/
	private BigDecimal cost;

	/**
	 * 获取地区
	 * 
	 * @return 地区
	 */
	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	public Area getArea() {
		return area;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 *            名称
	 */
	public void setArea(Area area) {
		this.area = area;
	}


	/**
	 * 获取运营商
	 * 
	 * @return 运营商 
	 */
	@JsonProperty
	@Column(nullable = false)
	public Integer getProviders() {
		return providers;
	}

	/**
	 * 设置运营商
	 * 
	 * @param providers
	 *           运营商
	 */
	public void setProviders(Integer providers) {
		this.providers = providers;
	}

	/**
	 * 获取面值
	 * 
	 * @return面值
	 */
	@JsonProperty
	@Column(nullable = false)
	public Integer getDenomination() {
		return denomination;
	}

	/**
	 * 设置面值
	 * 
	 * @param denomination
	 *            面值
	 */
	public void setDenomination(Integer denomination) {
		this.denomination = denomination;
	}

	/**
	 * 获取售价
	 * 
	 * @return 售价
	 */
	@JsonProperty
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * 设置售价
	 * 
	 * @param price
	 *            售价
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	/**
	 * 获取进价
	 * 
	 * @return 进价
	 */
	@JsonProperty
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getCost() {
		return cost;
	}

	/**
	 * 设置进价
	 * 
	 * @param cost 设置进价
	 */
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}


}