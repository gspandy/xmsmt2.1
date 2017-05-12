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
@Table(name = "xx_barcode_category")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_barcode_category_sequence")
public class BarcodeCategory extends BaseEntity {

	private static final long serialVersionUID = -2330598344835706164L;

	/** 名称 */
	private String name;

	/** 树路径 */
	private String treePath;

	/** 排序 */
	private Integer order;
	
	/** 上级分类 */
	private BarcodeCategory parent;

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
	 * 获取树路径
	 * 
	 * @return 树路径
	 */
	@JsonProperty
	@Column(nullable = false)
	public String getTreePath() {
		return treePath;
	}

	/**
	 * 设置树路径
	 * 
	 * @param treePath
	 *            树路径
	 */
	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}

	/**
	 * 获取排序
	 * 
	 * @return 排序
	 */
	@JsonProperty
	@Min(0)
	@Column(name = "orders")
	public Integer getOrder() {
		return order;
	}

	/**
	 * 设置排序
	 * 
	 * @param order
	 *            排序
	 */
	public void setOrder(Integer order) {
		this.order = order;
	}

	/**
	 * 获取上级分类
	 * 
	 * @return 上级分类
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public BarcodeCategory getParent() {
		return parent;
	}

	/**
	 * 设置上级分类
	 * 
	 * @param parent
	 *            上级分类
	 */
	public void setParent(BarcodeCategory parent) {
		this.parent = parent;
	}

}