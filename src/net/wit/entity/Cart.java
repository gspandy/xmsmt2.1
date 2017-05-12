/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.wit.Setting;
import net.wit.util.SettingUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.time.DateUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity - 购物车
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_cart")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_cart_sequence")
public class Cart extends BaseEntity {

	private static final long serialVersionUID = -6565967051825794561L;

	/** 超时时间 */
	public static final int TIMEOUT = 604800;

	/** 最大商品数 */
	public static final Integer MAX_PRODUCT_COUNT = 100;

	/** "ID"Cookie名称 */
	public static final String ID_COOKIE_NAME = "cartId";

	/** "密钥"Cookie名称 */
	public static final String KEY_COOKIE_NAME = "cartKey";

	/** 密钥 */
	private String key;

	/** 会员 */
	private Member member;

	/** 购物车项 */
	private Set<CartItem> cartItems = new HashSet<CartItem>();

	/**
	 * 获取密钥
	 * @return 密钥
	 */
	@Column(name = "cart_key", nullable = false, updatable = false)
	public String getKey() {
		return key;
	}

	/**
	 * 设置密钥
	 * @param key 密钥
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 获取会员
	 * @return 会员
	 */
	@OneToOne(fetch = FetchType.LAZY)
	public Member getMember() {
		return member;
	}

	/**
	 * 设置会员
	 * @param member 会员
	 */
	public void setMember(Member member) {
		this.member = member;
	}

	/**
	 * 获取购物车项
	 * @return 购物车项
	 */
	@OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JsonProperty
	public Set<CartItem> getCartItems() {
		return cartItems;
	}

	/**
	 * 设置购物车项
	 * @param cartItems 购物车项
	 */
	public void setCartItems(Set<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	/** 获取商品重量 */
	@Transient
	public int getWeight() {
		int weight = 0;
		if (getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null) {
					weight += cartItem.getWeight();
				}
			}
		}
		return weight;
	}

	/** 获取商品数量 */
	@Transient
	@JsonProperty
	public int getQuantity() {
		int quantity = 0;
		if (getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null && cartItem.getQuantity() != null) {
					quantity += cartItem.getQuantity();
				}
			}
		}
		return quantity;
	}

	/** 获取赠送积分 */
	@Transient
	public long getEffectivePoint() {
		long productPoint = 0L;
		BigDecimal productAmountPoint = BigDecimal.ZERO;
		for (CartItem cartItem : getCartItems()) {
			if (cartItem != null) {
				if (cartItem.isProductPoint()) {
					productPoint += cartItem.getProductPoint() + cartItem.getProductAddedPoint();
				} else {
					productAmountPoint.add(cartItem.getConsumePointAmount()).add(cartItem.getConsumeAddedPointAmount());
				}
			}
		}
		Setting setting = SettingUtils.get();
		Double defaultPointScale = setting.getDefaultPointScale();
		return productPoint + productAmountPoint.multiply(new BigDecimal(defaultPointScale.toString())).longValue();
	}

	/** 获取商品价格 */
	@Transient
	public BigDecimal getPrice() {
		BigDecimal price = BigDecimal.ZERO;
		for (CartItem cartItem : getCartItems()) {
			price = price.add(cartItem.getSubtotal());
		}
		return price;
	}

	/** 获取折扣价 */
	@Transient
	public BigDecimal getDiscount() {
		BigDecimal discount = BigDecimal.ZERO;
		for (CartItem cartItem : getCartItems()) {
			discount.add(cartItem.getDiscount());
		}
		return discount;
	}

	/** 获取有效商品价格 */
	@Transient
	@JsonProperty
	public BigDecimal getEffectivePrice() {
		BigDecimal effectivePrice = getPrice().subtract(getDiscount());
		return effectivePrice.compareTo(BigDecimal.ZERO) > 0 ? effectivePrice : BigDecimal.ZERO;
	}

	/** 获取促销 */
	@Transient
	public Set<Promotion> getPromotions() {
		Set<Promotion> allPromotions = new HashSet<Promotion>();
		if (getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null && cartItem.getProduct() != null) {
					allPromotions.addAll(cartItem.getProduct().getValidPromotions());
				}
			}
		}
		Set<Promotion> promotions = new TreeSet<Promotion>();
		for (Promotion promotion : allPromotions) {
			if (isValid(promotion)) {
				promotions.add(promotion);
			}
		}
		return promotions;
	}

	/** * 获取赠品项 */
	@Transient
	public Set<GiftItem> getGiftItems() {
		Set<GiftItem> giftItems = new HashSet<GiftItem>();
		for (Promotion promotion : getPromotions()) {
			if (promotion.getGiftItems() != null) {
				for (final GiftItem giftItem : promotion.getGiftItems()) {
					GiftItem target = (GiftItem) CollectionUtils.find(giftItems, new Predicate() {
						public boolean evaluate(Object object) {
							GiftItem other = (GiftItem) object;
							return other != null && other.getGift().equals(giftItem.getGift());
						}
					});
					if (target != null) {
						target.setQuantity(target.getQuantity() + giftItem.getQuantity());
					} else {
						giftItems.add(giftItem);
					}
				}
			}
		}
		return giftItems;
	}

	/**
	 * 获取促销购物车项
	 * @param promotion 促销
	 * @return 促销购物车项
	 */
	@Transient
	@JsonProperty
	private Set<CartItem> getCartItems(Promotion promotion) {
		Set<CartItem> cartItems = new HashSet<CartItem>();
		if (promotion != null && getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null && cartItem.getProduct() != null && cartItem.getProduct().isValid(promotion)) {
					cartItems.add(cartItem);
				}
			}
		}
		return cartItems;
	}

	/**
	 * 获取促销商品总价格
	 * @param promotion 促销
	 * @return 促销商品价格
	 */
	@Transient
	private BigDecimal getPrice(Promotion promotion) {
		BigDecimal price = BigDecimal.ZERO;
		for (CartItem cartItem : getCartItems(promotion)) {
			if (cartItem != null && cartItem.getSubtotal() != null) {
				price = price.add(cartItem.getSubtotal());
			}
		}
		return price;
	}

	/**
	 * 获取促销商品总数量
	 * @param promotion 促销
	 * @return 促销商品总数量
	 */
	@Transient
	private int getQuantity(Promotion promotion) {
		int quantity = 0;
		for (CartItem cartItem : getCartItems(promotion)) {
			if (cartItem != null && cartItem.getQuantity() != null) {
				quantity += cartItem.calculateQuantity().intValue();
			}
		}
		return quantity;
	}

	/**
	 * 判断促销是否有效
	 * @param promotion 促销
	 * @return 促销是否有效
	 */
	@Transient
	public boolean isValid(Promotion promotion) {
		if (promotion == null || !promotion.hasBegun() || promotion.hasEnded()) {
			return false;
		}
		if (promotion.getMemberRanks() == null || promotion.getMemberRanks().isEmpty() || getMember() == null || getMember().getMemberRank() == null || !promotion.getMemberRanks().contains(getMember().getMemberRank())) {
			return false;
		}
		Integer quantity = getQuantity(promotion);
		if ((promotion.getMinimumQuantity() != null && promotion.getMinimumQuantity() > quantity) || (promotion.getMaximumQuantity() != null && promotion.getMaximumQuantity() < quantity)) {
			return false;
		}
		BigDecimal price = getPrice(promotion);
		if ((promotion.getMinimumPrice() != null && promotion.getMinimumPrice().compareTo(price) > 0) || (promotion.getMaximumPrice() != null && promotion.getMaximumPrice().compareTo(price) < 0)) {
			return false;
		}
		return true;
	}

	/**
	 * 判断优惠券是否有效
	 * @param coupon 优惠券
	 * @return 优惠券是否有效
	 */
	@Transient
	public boolean isValid(Coupon coupon) {
		if (coupon == null || !coupon.getIsEnabled() || !coupon.hasBegun() || coupon.hasExpired()) {
			return false;
		}
		if ((coupon.getMinimumQuantity() != null && coupon.getMinimumQuantity() > getQuantity()) || (coupon.getMaximumQuantity() != null && coupon.getMaximumQuantity() < getQuantity())) {
			return false;
		}
		if ((coupon.getMinimumPrice() != null && coupon.getMinimumPrice().compareTo(getEffectivePrice()) > 0) || (coupon.getMaximumPrice() != null && coupon.getMaximumPrice().compareTo(getEffectivePrice()) < 0)) {
			return false;
		}
		return true;
	}

	/**
	 * 获取购物车项
	 * @param product 商品
	 * @return 购物车项
	 */
	@Transient
	public CartItem getCartItem(Product product) {
		if (product != null && getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null && cartItem.getProduct() != null && cartItem.getProduct().equals(product)) {
					return cartItem;
				}
			}
		}
		return null;
	}

	/**
	 * 判断是否包含商品
	 * @param product 商品
	 * @return 是否包含商品
	 */
	@Transient
	public boolean contains(Product product) {
		if (product != null && getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				if (cartItem != null && cartItem.getProduct() != null && cartItem.getProduct().equals(product)) {
					return true;
				}
			}
		}
		return false;
	}

	/** 获取令牌 */
	@Transient
	@JsonProperty
	public String getToken() {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(17, 37).append(getKey());
		if (getCartItems() != null) {
			for (CartItem cartItem : getCartItems()) {
				hashCodeBuilder.append(cartItem.getProduct()).append(cartItem.getQuantity()).append(cartItem.getPrice());
			}
		}
		return DigestUtils.md5Hex(hashCodeBuilder.toString());
	}

	/** 获取是否库存不足 */
	@Transient
	public boolean getIsLowStock() {
		for (CartItem cartItem : getCartItems()) {
			if (cartItem != null && cartItem.getIsLowStock()) {
				return true;
			}
		}
		return false;
	}

	/** 判断是否已过期 */
	@Transient
	public boolean hasExpired() {
		return new Date().after(DateUtils.addSeconds(getModifyDate(), TIMEOUT));
	}

	/** 判断是否允许使用促销优惠券 */
	@Transient
	public boolean isCouponAllowed() {
		for (Promotion promotion : getPromotions()) {
			if (promotion != null && !promotion.getIsCouponAllowed()) {
				return false;
			}
		}
		return true;
	}

	/** 判断是否为空 */
	@Transient
	public boolean isEmpty() {
		return getCartItems() == null || getCartItems().isEmpty();
	}

}