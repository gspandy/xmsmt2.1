/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity - 序列号
 * 
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_mobile_segment")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_mobile_segment_sequence")
public class MobileSegment extends BaseEntity {

	private static final long serialVersionUID = -2330598645838576184L;
	
	/** 地区*/
	private Area area;

	/** 运营商  */
	private Integer providers;
	
	/** 号码段 */
	private Integer segment;
	
	/** 省 */
	private String province;

	/** 市*/
	private String city;

	/** 说明*/
	private String descr;

	/**
	 * 获取地区
	 * 
	 * @return 地区
	 */
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

	public Integer getSegment() {
		return segment;
	}

	public void setSegment(Integer segment) {
		this.segment = segment;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}


}