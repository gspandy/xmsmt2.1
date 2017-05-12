/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity - 规格
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_specification")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_specification_sequence")
public class Specification extends OrderEntity {

	private static final long serialVersionUID = -6346775052811140926L;

	/**
	 * 类型
	 */
	public enum Type {

		/** 文本 */
		text,

		/** 图片 */
		image
	}

	/** 商品分类 */
	private ProductCategory productCategory;
	
	private Tenant tenant;

	/** 名称 */
	private String name;

	/** 类型 */
	private Type type;

	/** 备注 */
	private String memo;

	/** 规格值 */
	private List<SpecificationValue> specificationValues = new ArrayList<SpecificationValue>();

	/** 商品 */
	private Set<Product> products = new HashSet<Product>();

	/**
	 * 获取名称
	 * @return 名称
	 */
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	@JsonProperty
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * @param name 名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取类型
	 * @return 类型
	 */
	@NotNull
	@Column(nullable = false)
	public Type getType() {
		return type;
	}

	/**
	 * 设置类型
	 * @param type 类型
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * 获取备注
	 * @return 备注
	 */
	@Length(max = 200)
	@JsonProperty
	public String getMemo() {
		return memo;
	}

	/**
	 * 设置备注
	 * @param memo 备注
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * 获取规格值
	 * @return 规格值
	 */
	@Valid
	@NotEmpty
	@OneToMany(mappedBy = "specification", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("order asc")
	public List<SpecificationValue> getSpecificationValues() {
		return specificationValues;
	}

	/**
	 * 设置规格值
	 * @param specificationValues 规格值
	 */
	public void setSpecificationValues(List<SpecificationValue> specificationValues) {
		this.specificationValues = specificationValues;
	}

	/**
	 * 获取商品
	 * @return 商品
	 */
	@ManyToMany(mappedBy = "specifications", fetch = FetchType.LAZY)
	public Set<Product> getProducts() {
		return products;
	}

	/**
	 * 设置商品
	 * @param products 商品
	 */
	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	/**
	 * 获取 productCategory
	 * @return productCategory
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	public ProductCategory getProductCategory() {
		return productCategory;
	}

	/**
	 * 设置 productCategory
	 * @param productCategory productCategory
	 */
	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	/** 获取 tenant
	 * @return tenant
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public Tenant getTenant() {
		return tenant;
	}

	/**设置 tenant
	 * @param tenant tenant
	 */
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

}