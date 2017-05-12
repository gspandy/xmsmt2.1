/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;

/**
 * Entity - 订单项
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_order_item")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_order_item_sequence")
public class OrderItem extends BaseEntity {

	private static final long serialVersionUID = -4999926022604479334L;

	/** 商品编号 */
	@Expose
	private String sn;

	/** 商品名称 */
	@Expose
	private String name;

	/** 商品全称 */
	@Expose
	private String fullName;

	/** 商品价格 */
	@Expose
	private BigDecimal price;

	/** 商品分享佣金价格 */
	private BigDecimal productRent;

	/** 商品折扣价格 */
	@Expose
	private BigDecimal discount = BigDecimal.ZERO;

	/** 商品重量 */
	@Expose
	private Integer weight;

	/** 商品缩略图 */
	@Expose
	private String thumbnail;

	/** 是否为赠品 */
	private Boolean isGift;

	/** 订购数量 */
	@Expose
	private Integer quantity;

	/** 订购单位 */
	@Expose
	private String packagUnitName;

	/** 换算系数 */
	private BigDecimal coefficient;

	/** 已发货数量 */
	@Expose
	private Integer shippedQuantity;

	/** 已退货数量 */
	@Expose
	private Integer returnQuantity;

	/** 商品 */
	@Expose
	private Product product;

	/** 订单 */
	private Order order;

	/** 子订单 */
	private Trade trade;

	/** 买家评价 */
	private Review review;

	/** 发货单 */
	private Set<Authenticode> authenticodes = new HashSet<Authenticode>();

	/**
	 * 获取商品编号
	 * @return 商品编号
	 */
	@JsonProperty
	@NotEmpty
	@Column(nullable = false, updatable = false)
	public String getSn() {
		return sn;
	}

	/**
	 * 设置商品编号
	 * @param sn 商品编号
	 */
	public void setSn(String sn) {
		this.sn = sn;
	}

	/**
	 * 获取商品名称
	 * @return 商品名称
	 */
	@JsonProperty
	@Column(nullable = false, updatable = false)
	public String getName() {
		return name;
	}

	/**
	 * 设置商品名称
	 * @param name 商品名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取商品全称
	 * @return 商品全称
	 */
	@JsonProperty
	@Column(nullable = false, updatable = false)
	public String getFullName() {
		return fullName;
	}

	/**
	 * 设置商品全称
	 * @param fullName 商品全称
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * 获取商品价格
	 * @return 商品价格
	 */
	@JsonProperty
	@NotNull
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * 设置商品价格
	 * @param price 商品价格
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	/**
	 * 获取商品折扣价格
	 * @return 商品折扣价格
	 */
	@NotNull
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6, columnDefinition = "decimal default 0")
	public BigDecimal getDiscount() {
		return discount;
	}

	/**
	 * 设置商品折扣价格
	 * @param price 商品折扣价格
	 */
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	/**
	 * 获取商品重量
	 * @return 商品重量
	 */
	@JsonProperty
	@Column(updatable = false)
	public Integer getWeight() {
		return weight;
	}

	/**
	 * 设置商品重量
	 * @param weight 商品重量
	 */
	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	/**
	 * 获取商品缩略图
	 * @return 商品缩略图
	 */
	@JsonProperty
	@Column(updatable = false)
	public String getThumbnail() {
		return thumbnail;
	}

	/**
	 * 设置商品缩略图
	 * @param thumbnail 商品缩略图
	 */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	/**
	 * 获取是否为赠品
	 * @return 是否为赠品
	 */
	@JsonProperty
	@Column(nullable = false, updatable = false)
	public Boolean getIsGift() {
		return isGift;
	}

	/**
	 * 设置是否为赠品
	 * @param isGift 是否为赠品
	 */
	public void setIsGift(Boolean isGift) {
		this.isGift = isGift;
	}

	/**
	 * 获取数量
	 * @return 数量
	 */
	@JsonProperty
	@NotNull
	@Min(1)
	@Max(10000)
	@Column(nullable = false)
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * 设置数量
	 * @param quantity 数量
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	/**
	 * 获取订购单位
	 * @return 订购单位
	 */
	@Column
	public String getPackagUnitName() {
		return packagUnitName;
	}

	/**
	 * 设置订购单位
	 * @param quantity 订购单位
	 */
	public void setPackagUnitName(String packagUnitName) {
		this.packagUnitName = packagUnitName;
	}

	/**
	 * 获取已发货数量
	 * @return 已发货数量
	 */
	@Column(nullable = false)
	public Integer getShippedQuantity() {
		return shippedQuantity;
	}

	/**
	 * 获取计量总数
	 * @return 计量总数
	 */
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6, columnDefinition = "decimal(21,6) default 1")
	public BigDecimal getCoefficient() {
		if (coefficient == null) {
			return new BigDecimal(1);
		}
		return coefficient;
	}

	/**
	 * 设置 coefficient
	 * @param coefficient coefficient
	 */
	public void setCoefficient(BigDecimal coefficient) {
		this.coefficient = coefficient;
	}

	/**
	 * 设置已发货数量
	 * @param shippedQuantity 已发货数量
	 */
	public void setShippedQuantity(Integer shippedQuantity) {
		this.shippedQuantity = shippedQuantity;
	}

	/**
	 * 获取已退货数量
	 * @return 已退货数量
	 */
	@Column(nullable = false)
	public Integer getReturnQuantity() {
		return returnQuantity;
	}

	/**
	 * 设置已退货数量
	 * @param returnQuantity 已退货数量
	 */
	public void setReturnQuantity(Integer returnQuantity) {
		this.returnQuantity = returnQuantity;
	}

	/**
	 * 获取商品
	 * @return 商品
	 */
	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	public Product getProduct() {
		return product;
	}

	/**
	 * 设置商品
	 * @param product 商品
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * 获取订单
	 * @return 订单
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orders", nullable = false, updatable = false)
	public Order getOrder() {
		return order;
	}

	/**
	 * 设置订单
	 * @param order 订单
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * 获取子订单
	 * @return 子订单
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trades", nullable = false, updatable = false)
	public Trade getTrade() {
		return trade;
	}

	/**
	 * 设置子订单
	 * @param order 子订单
	 */
	public void setTrade(Trade trade) {
		this.trade = trade;
	}

	/**
	 * 获取订单项评价
	 * @return 订单项评价
	 */
	@OneToOne(mappedBy = "orderItem", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, optional = true)
	public Review getReview() {
		return review;
	}

	/**
	 * 设置订单项评价
	 * @param review 订单项评价
	 */
	public void setReview(Review review) {
		this.review = review;
	}

	/**
	 * 获取商品总重量
	 * @return 商品总重量
	 */
	@JsonProperty
	@Transient
	public int getTotalWeight() {
		if (getWeight() != null && getQuantity() != null) {
			return getWeight() * getQuantity();
		} else {
			return 0;
		}
	}

	/**
	 * 获取小计
	 * @return 小计
	 */
	@JsonProperty
	@Transient
	public BigDecimal getSubtotal() {
		if (getPrice() != null && getQuantity() != null) {
			return getPrice().multiply(new BigDecimal(getQuantity()));
		} else {
			return new BigDecimal(0);
		}
	}

	/**
	 * 获取小计
	 * @return 小计
	 */
	@JsonProperty
	@Transient
	public BigDecimal getRealHairSubtotal() {
		if (getPrice() != null && getQuantity() != null) {
			return getPrice().multiply(new BigDecimal(getShippedQuantity()));
		} else {
			return new BigDecimal(0);
		}
	}

	@Transient
	public String getSpecificationValue() {
		return getProduct().getSpecification_value().toString();
	}

	/**
	 * 获取校验码
	 * @return 校验码
	 */
	@OneToMany(mappedBy = "orderItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@OrderBy("createDate asc")
	public Set<Authenticode> getAuthenticodes() {
		return authenticodes;
	}

	/**
	 * 设置校验码
	 * @param shippings 校验码
	 */
	public void setAuthenticodes(Set<Authenticode> authenticodes) {
		this.authenticodes = authenticodes;
	}

	public void setCalculatePackagUnit(Product product, PackagUnit packagUnit) {
		if (packagUnit != null) {
			this.packagUnitName = packagUnit.getName();
			this.coefficient = packagUnit.getCoefficient();
		} else {
			this.packagUnitName = product.getUnit();
			this.coefficient = BigDecimal.ONE;
		}
	}

	/**
	 * 持久化前处理
	 */
	@PrePersist
	public void prePersist() {
		if (this.packagUnitName == null && product != null) {
			this.packagUnitName = product.getUnit();
		}
		if (this.coefficient == null) {
			this.coefficient = BigDecimal.ONE;
		}
	}

	/**
	 * 更新前处理
	 */
	@PreUpdate
	public void preUpdate() {
		if (this.packagUnitName == null && product != null) {
			this.packagUnitName = product.getUnit();
		}
		if (this.coefficient == null) {
			this.coefficient = BigDecimal.ONE;
		}
	}

	/** 计算订货数量 */
	@Transient
	public BigDecimal calculateQuantity() {
		return new BigDecimal(getQuantity()).multiply(this.getCoefficient());
	}

	/** 计算订货数量 */
	@Transient
	public int calculateQuantityIntValue() {
		return calculateQuantity().intValue();
	}

	/** 计算发货数量 */
	@Transient
	public BigDecimal calculateShippedQuantity() {
		return new BigDecimal(getShippedQuantity()).multiply(this.getCoefficient());
	}

	/** 计算发货数量 */
	@Transient
	public int calculateShippedQuantityIntValue() {
		return calculateShippedQuantity().intValue();
	}

	/** 计算退货数量 */
	@Transient
	public BigDecimal calculateReturnQuantity() {
		return new BigDecimal(getReturnQuantity()).multiply(this.getCoefficient());
	}

	/** 计算退货数量 */
	@Transient
	public int calculateReturnQuantityIntValue() {
		return calculateReturnQuantity().intValue();
	}

	/**
	 * 获取 productRent
	 * @return productRent
	 */
	public BigDecimal getProductRent() {
		return productRent;
	}

	/**
	 * 设置 productRent
	 * @param productRent productRent
	 */
	public void setProductRent(BigDecimal productRent) {
		this.productRent = productRent;
	}

	@Transient
	public BigDecimal getMarkerAmount() {
		return product.getMarketPrice();
	}

}