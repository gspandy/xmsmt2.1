/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.wit.Setting;
import net.wit.domain.impl.TenantDomainImpl;
import net.wit.util.SettingUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity - 购物车项
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_cart_item")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_cart_item_sequence")
public class CartItem extends BaseEntity {

	private static final long serialVersionUID = 2979296789363163144L;

	/** 最大数量 */
	public static final Integer MAX_QUANTITY = 10000;

	/** 数量 */
	private Integer quantity;

	/** 商品 */
	private Product product;

	/** 商品包装类别 */
	private PackagUnit packagUnit;

	/** 购物车 */
	private Cart cart;

	/**
	 * 获取数量
	 * @return 数量
	 */
	@Column(nullable = false)
	@JsonProperty
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
	 * 获取商品
	 * @return 商品
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	@JsonProperty
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
	 * 获取商品包装类别
	 * @return 商品包装类别
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public PackagUnit getPackagUnit() {
		return packagUnit;
	}

	/**
	 * 设置商品包装类别
	 * @param packagUnit 商品包装类别
	 */
	public void setPackagUnit(PackagUnit packagUnit) {
		this.packagUnit = packagUnit;
	}

	/**
	 * 获取购物车
	 * @return 购物车
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public Cart getCart() {
		return cart;
	}

	/**
	 * 设置购物车
	 * @param cart 购物车
	 */
	public void setCart(Cart cart) {
		this.cart = cart;
	}

	/** 获取价格 */
	@JsonProperty
	@Transient
	public BigDecimal getPrice() {
		if (getProduct() == null) {
			return BigDecimal.ZERO;
		}
		if (getProduct().getPrice() == null) {
			return BigDecimal.ZERO;
		}
		Setting setting = SettingUtils.get();
		BigDecimal basePrice = null; // 商品基础价格
		if (getPackagUnit() != null) { // 走包装单位计算
			if (TenantDomainImpl.supportWholePrice(getCart())) {
				basePrice = this.packagUnit.getWholePrice() == null ? this.packagUnit.calculateWholePrice() : this.packagUnit.getWholePrice();
			} else {
				basePrice = this.packagUnit.getPrice() == null ? this.packagUnit.calculatePrice() : this.packagUnit.getPrice();
			}
		} else { // 走商品价格
			if (TenantDomainImpl.supportWholePrice(getCart())) {
				basePrice = getProduct().getWholePrice();
			} else {
				basePrice = getProduct().getPrice();
			}
		}
		Member member = getCart().getMember();
		if (member != null && member.getMemberRank() != null) { // 会员折扣价
			MemberRank memberRank = member.getMemberRank();
			Map<MemberRank, BigDecimal> memberPrices = getProduct().getMemberPrice();
			if (memberPrices != null && !memberPrices.isEmpty() && memberPrices.containsKey(memberRank) && !memberRank.getIsDefault()) {
				BigDecimal memberPrice = memberPrices.get(memberRank);
				if (getPackagUnit() != null) {
					memberPrice = memberPrice.multiply(getPackagUnit().getCoefficient());
				}
				return setting.setScale(memberPrice);
			}
			if (memberRank.getScale() != null) {
				return setting.setScale(basePrice.multiply(new BigDecimal(memberRank.getScale())));
			}
		}
		return setting.setScale(basePrice);
	}

	/** 是否走商品积分 */
	@Transient
	public boolean isProductPoint() {
		if (getProduct().getPoint() == null) {
			return false;
		}
		return true;
	}

	/** 获取商品积分 */
	@Transient
	public long getProductPoint() {
		if (getProduct().getPoint() != null && getQuantity() != null) {
			// 走包装单位计算
			if (getPackagUnit() != null) {
				return new BigDecimal(getProduct().getPoint() * getQuantity()).multiply(getPackagUnit().getCoefficient()).longValue();
			}
			return getProduct().getPoint() * getQuantity();
		} else {
			return 0L;
		}
	}

	/** 获取赠送商品积分增加值 */
	@Transient
	public long getProductAddedPoint() {
		long tempPoint = getProductPoint();
		long originalPoint = 0L;
		long currentPoint = 0L;
		for (Promotion promotion : getProduct().getValidPromotions()) {
			if (!getCart().isValid(promotion)) {
				continue;
			}
			long originalPromotionPoint = tempPoint;
			long currentPromotionPoint = promotion.calculatePoint(getQuantity(), originalPromotionPoint);
			originalPoint += originalPromotionPoint;
			currentPoint += currentPromotionPoint;
			if (originalPromotionPoint > 0) {
				tempPoint = (long) (currentPromotionPoint / (double) originalPromotionPoint * tempPoint);
			} else {
				tempPoint = (currentPromotionPoint - originalPromotionPoint) / getQuantity();
			}
		}
		long addedPoint = currentPoint - originalPoint;
		return addedPoint > 0 ? addedPoint : 0L;
	}

	/** 获取商品消费金额 */
	@Transient
	public BigDecimal getConsumePointAmount() {
		return getPrice().multiply(new BigDecimal(getQuantity()));
	}

	/** 获取赠送商品消费金额 */
	@Transient
	public BigDecimal getConsumeAddedPointAmount() {
		BigDecimal tempAmount = getConsumePointAmount();
		BigDecimal originalAmount = BigDecimal.ZERO;
		BigDecimal currentAmount = BigDecimal.ZERO;
		for (Promotion promotion : getProduct().getValidPromotions()) {
			if (!getCart().isValid(promotion)) {
				continue;
			}
			BigDecimal originalPromotionAmount = tempAmount;
			BigDecimal currentPromotionAmount = promotion.calculateAmountPoint(getQuantity(), originalPromotionAmount);
			originalAmount = originalAmount.add(originalPromotionAmount);
			currentAmount = currentAmount.add(currentPromotionAmount);
			if (originalAmount.compareTo(BigDecimal.ZERO) > 0) {
				tempAmount = currentPromotionAmount.divide(originalPromotionAmount).multiply(tempAmount);
			} else {
				tempAmount = currentPromotionAmount.subtract(originalPromotionAmount).divide(new BigDecimal(getQuantity()));
			}
		}
		BigDecimal addedPoint = currentAmount.subtract(originalAmount);
		return addedPoint.compareTo(BigDecimal.ZERO) > 0 ? addedPoint : BigDecimal.ZERO;
	}

	/** 获取商品重量 */
	@Transient
	public int getWeight() {
		if (getProduct() != null && getProduct().getWeight() != null && getQuantity() != null) {
			// 走包装单位计算
			if (getPackagUnit() != null) {
				return new BigDecimal(getProduct().getWeight() * getQuantity()).multiply(getPackagUnit().getCoefficient()).intValue();
			}
			// 走商品计量单位
			return getProduct().getWeight() * getQuantity();
		} else {
			return 0;
		}
	}

	/** 获取小计金额 */
	@Transient
	public BigDecimal getSubtotal() {
		return getConsumePointAmount();
	}

	/** 获取折扣 */
	@Transient
	public BigDecimal getDiscount() {
		BigDecimal tempPrice = getConsumePointAmount();
		BigDecimal originalPrice = BigDecimal.ZERO;
		BigDecimal currentPrice = BigDecimal.ZERO;
		for (Promotion promotion : getProduct().getValidPromotions()) {
			if (!getCart().isValid(promotion)) {
				continue;
			}
			BigDecimal originalPromotionPrice = tempPrice;
			BigDecimal currentPromotionPrice = promotion.calculatePrice(getQuantity(), originalPromotionPrice);
			originalPrice = originalPrice.add(originalPromotionPrice);
			currentPrice = currentPrice.add(currentPromotionPrice);
			if (originalPromotionPrice.compareTo(BigDecimal.ZERO) > 0) {
				tempPrice = currentPromotionPrice.divide(originalPromotionPrice, 50, RoundingMode.DOWN).multiply(tempPrice);
			} else {
				tempPrice = BigDecimal.ZERO;
			}
		}
		Setting setting = SettingUtils.get();
		BigDecimal discount = setting.setScale(originalPrice.subtract(currentPrice));
		return discount.compareTo(BigDecimal.ZERO) > 0 ? discount : BigDecimal.ZERO;
	}

	/** 获取是否库存不足 */
	@Transient
	public boolean getIsLowStock() {
		if (getQuantity() != null && getProduct() != null && getProduct().getStock() != null && calculateQuantity().intValue() > getProduct().getAvailableStock()) {
			return true;
		} else {
			return false;
		}
	}

	/** 获取最小单位数量 */
	@Transient
	public BigDecimal calculateQuantity() {
		if (this.packagUnit == null) {
			return new BigDecimal(getQuantity());
		} else {
			return new BigDecimal(getQuantity()).multiply(this.packagUnit.getCoefficient());
		}
	}

	/** 获取最小单位数量 */
	@Transient
	public int calculateQuantityIntValue() {
		return calculateQuantity().intValue();
	}

	/** 增加商品数量 */
	@Transient
	public void add(int quantity, PackagUnit packagUnit) {
		PackagUnit tempPackagUnit = this.packagUnit;
		setPackagUnit(packagUnit);
		if (quantity <= 0) {
			return;
		}
		if (getQuantity() == null) {
			setQuantity(quantity);
			return;
		}
		if (tempPackagUnit == null && packagUnit == null) {
			setQuantity(getQuantity() + quantity);
			return;
		}
		if (tempPackagUnit == null && packagUnit != null) {
			setQuantity(quantity);
			return;
		}
		if (tempPackagUnit.equals(packagUnit)) {
			setQuantity(getQuantity() + quantity);
			return;
		}
		setQuantity(quantity);
	}
}