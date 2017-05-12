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
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity - 序列号
 * 
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_barcode")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_barcode_sequence")
public class Barcode extends BaseEntity {

	private static final long serialVersionUID = -2330598644835706164L;

	/** 名称 */
	private String name;

	/** 拼音码 */
	private String spell;
	
	/** 条码 */
	private String barcode;

	/** 单位 */
	private String unitName;
	
	/** 参考进价 */
	private BigDecimal inPrice;

	/** 参考售价*/
	private BigDecimal outPrice;
	
	/** 上级分类 */
	private BarcodeCategory barcodeCategory;

	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	@JsonProperty
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 *            名称
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * 获取拼音码 
	 * 
	 * @return 拼音码 
	 */
	@JsonProperty
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getSpell() {
		return spell;
	}

	/**
	 * 设置拼音码 
	 * 
	 * @param name
	 *            拼音码 
	 */
	public void setSpell(String spell) {
		this.spell = spell;
	}

	/**
	 * 获取条码
	 * 
	 * @return 条码
	 */
	@JsonProperty
	@NotEmpty
	@Length(max = 13)
	@Column(nullable = false)
	public String getBarcode() {
		return barcode;
	}

	/**
	 * 设置条码
	 * 
	 * @param barcode
	 *            条码
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	/**
	 * 获取单位
	 * 
	 * @return 单位
	 */
	@JsonProperty
	@NotEmpty
	@Length(max = 10)
	@Column(nullable = false)
	public String getUnitName() {
		return unitName;
	}

	/**
	 * 设置单位
	 * 
	 * @param unitName
	 *            单位
	 */
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	@JsonProperty
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getInPrice() {
		return inPrice;
	}

	/**
	 * 设置参考进价
	 * 
	 * @param inPrice 设置参考进价
	 */
	public void setInPrice(BigDecimal inPrice) {
		this.inPrice = inPrice;
	}

	@JsonProperty
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getOutPrice() {
		return outPrice;
	}

	/**
	 * 设置参考售价
	 * 
	 * @param outPrice 设置参考售价
	 */
	public void setOutPrice(BigDecimal outPrice) {
		this.outPrice = outPrice;
	}

	/**
	 * 获取所属分类
	 * 
	 * @return 所属分类
	 */
	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	public BarcodeCategory getBarcodeCategory() {
		return barcodeCategory;
	}

	/**
	 * 设置所属分类
	 * 
	 * @param barcodeCategory
	 *            所属分类
	 */
	public void setBarcodeCategory(BarcodeCategory barcodeCategory) {
		this.barcodeCategory = barcodeCategory;
	}

}