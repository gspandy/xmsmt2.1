/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PreRemove;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import net.wit.util.FreemarkerUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;

import freemarker.template.TemplateException;

/**
 * Entity - 促销
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_promotion")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_promotion_sequence")
public class Promotion extends OrderEntity {

	private static final long serialVersionUID = 3536993535267962279L;

	/** 访问路径前缀 */
	private static final String PATH_PREFIX = "/promotion/content";

	/** 访问路径后缀 */
	private static final String PATH_SUFFIX = ".jhtml";

	/** 方案类型 */
	public enum Type {

		/** 普通促销 */
		common,

		/** 团购 */
		groupon,

		/** 搭配 */
		collocation,

		/** 拍卖 */
		auction
	}

	/** 分类 */
	public enum Classify {
		/** 标准拍 */
		standard,
		/** 特权拍 */
		privilege,
		/** 精品团 */
		competitive,
		/** 拼拼团 */
		puzzle
	}

	/** 方案状态 */
	public enum Status {
		/** 提交开始 */
		submit,
		/** 进行中 */
		underway,
		/** 完成 */
		finished,
		/** 取消 */
		cancel,
		/** 申请 */
		apply
	}

	/** 名称 */
	@Expose
	private String name;

	/** 标题 */
	@Expose
	private String title;

	/** 起始日期 */
	@Expose
	private Date beginDate;

	/** 结束日期 */
	@Expose
	private Date endDate;

	/** 最小商品/参与人数量 */
	@Expose
	private Integer minimumQuantity;

	/** 最大商品/参与人数量 */
	@Expose
	private Integer maximumQuantity;

	/** 最小商品价格 */
	@Expose
	private BigDecimal minimumPrice;

	/** 最大商品价格 */
	@Expose
	private BigDecimal maximumPrice;

	/** 当前商品价格 */
	@Expose
	private BigDecimal currentPrice;

	/** 价格运算表达式 */
	@Expose
	private String priceExpression;

	/** 积分运算表达式 */
	@Expose
	private String pointExpression;

	/** 是否免运费 */
	@Expose
	private Boolean isFreeShipping;

	/** 是否允许使用优惠券 */
	@Expose
	private Boolean isCouponAllowed;

	/** 介绍 */
	@Expose
	private String introduction;

	/** 方案类型 */
	@Expose
	private Type type;

	/** 方案分类 */
	@Expose
	private Classify classify;

	/** 采用配送方式 */
	@Expose
	private ShippingMethod shippingMethod;

	/** 保证金 */
	@Expose
	private BigDecimal bail;

	/** 拍卖中的价格策略 */
	@Expose
	private BigDecimal stepPrice;

	/** 方案状态 */
	@Expose
	private Status status = Status.submit;

	/** 佣金比率 */
	private BigDecimal brokerage;

	/** 促销方案商家 */
	private Tenant tenant;

	/** 申请会员 */
	private Member member;

	/** 允许参加会员等级 */
	@Expose
	private Set<MemberRank> memberRanks = new HashSet<MemberRank>();

	/** 允许参与商品分类 */
	@Expose
	private Set<ProductCategory> productCategories = new HashSet<ProductCategory>();

	/** 允许参与品牌 */
	@Expose
	private Set<Brand> brands = new HashSet<Brand>();

	/** 赠送优惠券 */
	@Expose
	private Set<Coupon> coupons = new HashSet<Coupon>();

	/** 赠品 */
	@Expose
	private List<GiftItem> giftItems = new ArrayList<GiftItem>();

	/** 促销区域 */
	@Expose
	private Set<Area> areas = new HashSet<Area>(0);

	/** 允许参与商品 */
	@Expose
	private List<PromotionProduct> promotionProducts = new ArrayList<PromotionProduct>(0);

	/** 促销参与会员 */
	@Expose
	private List<PromotionMember> promotionMembers = new ArrayList<PromotionMember>(0);

	/** 促销订单 */
	private Set<Order> orders = new HashSet<Order>(0);

	/** 促销总价 */
	@Expose
	private BigDecimal totalPrice = BigDecimal.ZERO;

	/**
	 * 获取名称
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
	 * @param name 名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取标题
	 * @return 标题
	 */
	@JsonProperty
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getTitle() {
		return title;
	}

	/**
	 * 设置标题
	 * @param title 标题
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 获取起始日期
	 * @return 起始日期
	 */
	@JsonProperty
	public Date getBeginDate() {
		return beginDate;
	}

	/**
	 * 设置起始日期
	 * @param beginDate 起始日期
	 */
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	/**
	 * 获取结束日期
	 * @return 结束日期
	 */
	@JsonProperty
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * 设置结束日期
	 * @param endDate 结束日期
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * 获取最小商品数量
	 * @return 最小商品数量
	 */
	@JsonProperty
	@Min(0)
	public Integer getMinimumQuantity() {
		return minimumQuantity;
	}

	/**
	 * 设置最小商品数量
	 * @param minimumQuantity 最小商品数量
	 */
	public void setMinimumQuantity(Integer minimumQuantity) {
		this.minimumQuantity = minimumQuantity;
	}

	/**
	 * 获取最大商品数量
	 * @return 最大商品数量
	 */
	@JsonProperty
	@Min(0)
	public Integer getMaximumQuantity() {
		return maximumQuantity;
	}

	/**
	 * 设置最大商品数量
	 * @param maximumQuantity 最大商品数量
	 */
	public void setMaximumQuantity(Integer maximumQuantity) {
		this.maximumQuantity = maximumQuantity;
	}

	/**
	 * 获取最小商品价格
	 * @return 最小商品价格
	 */
	@JsonProperty
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(precision = 21, scale = 6)
	public BigDecimal getMinimumPrice() {
		return minimumPrice;
	}

	/**
	 * 设置最小商品价格
	 * @param minimumPrice 最小商品价格
	 */
	public void setMinimumPrice(BigDecimal minimumPrice) {
		this.minimumPrice = minimumPrice;
	}

	/**
	 * 获取最大商品价格
	 * @return 最大商品价格
	 */
	@JsonProperty
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(precision = 21, scale = 6)
	public BigDecimal getMaximumPrice() {
		return maximumPrice;
	}

	/**
	 * 设置最大商品价格
	 * @param maximumPrice 最大商品价格
	 */
	public void setMaximumPrice(BigDecimal maximumPrice) {
		this.maximumPrice = maximumPrice;
	}

	@JsonProperty
	@Digits(integer = 12, fraction = 3)
	@Column(precision = 21, scale = 6)
	public BigDecimal getCurrentPrice() {
		if (currentPrice == null) {
			return null;
		}
		if (minimumPrice != null) {
			if (currentPrice.compareTo(minimumPrice) <= 0) {
				return minimumPrice;
			}
		}
		return currentPrice;
	}

	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}

	/**
	 * 获取价格运算表达式
	 * @return 价格运算表达式
	 */
	public String getPriceExpression() {
		return priceExpression;
	}

	/**
	 * 设置价格运算表达式
	 * @param priceExpression 价格运算表达式
	 */
	public void setPriceExpression(String priceExpression) {
		this.priceExpression = priceExpression;
	}

	/**
	 * 获取积分运算表达式
	 * @return 积分运算表达式
	 */
	public String getPointExpression() {
		return pointExpression;
	}

	/**
	 * 设置积分运算表达式
	 * @param pointExpression 积分运算表达式
	 */
	public void setPointExpression(String pointExpression) {
		this.pointExpression = pointExpression;
	}

	/**
	 * 获取是否免运费
	 * @return 是否免运费
	 */
	@JsonProperty
	@NotNull
	@Column(nullable = false)
	public Boolean getIsFreeShipping() {
		return isFreeShipping;
	}

	/**
	 * 设置是否免运费
	 * @param isFreeShipping 是否免运费
	 */
	public void setIsFreeShipping(Boolean isFreeShipping) {
		this.isFreeShipping = isFreeShipping;
	}

	/**
	 * 获取是否允许使用优惠券
	 * @return 是否允许使用优惠券
	 */
	@JsonProperty
	@NotNull
	@Column(nullable = false)
	public Boolean getIsCouponAllowed() {
		return isCouponAllowed;
	}

	/**
	 * 设置是否允许使用优惠券
	 * @param isCouponAllowed 是否允许使用优惠券
	 */
	public void setIsCouponAllowed(Boolean isCouponAllowed) {
		this.isCouponAllowed = isCouponAllowed;
	}

	/**
	 * 获取介绍
	 * @return 介绍
	 */
	@JsonProperty
	@Lob
	public String getIntroduction() {
		return introduction;
	}

	/**
	 * 设置介绍
	 * @param introduction 介绍
	 */
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	/**
	 * 获取方案类型
	 * @return 方案类型
	 */
	public Type getType() {
		return type;
	}

	/**
	 * 设置方案类型
	 * @param type 方案类型
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * 获取方案分类
	 * @return 方案分类
	 */
	public Classify getClassify() {
		return classify;
	}

	/**
	 * 设置方案分类
	 * @param classify 方案分类
	 */
	public void setClassify(Classify classify) {
		this.classify = classify;
	}

	/**
	 * 获取允许参加会员等级
	 * @return 允许参加会员等级
	 */
	@JsonProperty
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_promotion_member_rank")
	public Set<MemberRank> getMemberRanks() {
		return memberRanks;
	}

	/**
	 * 设置允许参加会员等级
	 * @param memberRanks 允许参加会员等级
	 */
	public void setMemberRanks(Set<MemberRank> memberRanks) {
		this.memberRanks = memberRanks;
	}

	/**
	 * 获取允许参与商品分类
	 * @return 允许参与商品分类
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_promotion_product_category")
	public Set<ProductCategory> getProductCategories() {
		return productCategories;
	}

	/**
	 * 设置允许参与商品分类
	 * @param productCategories 允许参与商品分类
	 */
	public void setProductCategories(Set<ProductCategory> productCategories) {
		this.productCategories = productCategories;
	}

	/**
	 * 获取配送方式
	 * @return 配送方式
	 */
	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	public ShippingMethod getShippingMethod() {
		return shippingMethod;
	}

	/**
	 * 设置配送方式
	 * @param shippingMethod 配送方式
	 */
	public void setShippingMethod(ShippingMethod shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	/**
	 * 获取促销区域
	 * @return 促销区域
	 */
	@JsonProperty
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_promotion_area")
	public Set<Area> getAreas() {
		return areas;
	}

	/**
	 * 设置促销区域
	 * @param areas 允许参与促销区域
	 */
	public void setAreas(Set<Area> areas) {
		this.areas = areas;
	}

	@OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	public List<PromotionProduct> getPromotionProducts() {
		return promotionProducts;
	}

	public void setPromotionProducts(List<PromotionProduct> promotionProducts) {
		this.promotionProducts = promotionProducts;
	}

	@Transient
	public Product getDefaultProduct() {
		if (promotionProducts == null || promotionProducts.size() == 0) {
			return null;
		}
		return promotionProducts.get(0).getProduct();
	}

	/**
	 * 获取允许参与商品
	 * @return 允许参与商品
	 */
	@JsonProperty
	@Transient
	public Set<Product> getProducts() {
		Set<Product> products = new HashSet<Product>(promotionProducts.size());
		for (PromotionProduct pp : promotionProducts) {
			products.add(pp.getProduct());
		}
		return products;
	}

	@JsonProperty
	@OrderBy("offerPrice desc")
	@OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	public List<PromotionMember> getPromotionMembers() {
		return promotionMembers;
	}

	public void setPromotionMembers(List<PromotionMember> promotionMembers) {
		this.promotionMembers = promotionMembers;
	}

	@OneToMany(mappedBy = "promotionScheme", fetch = FetchType.LAZY)
	public Set<Order> getOrders() {
		return orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	/**
	 * 获取允许参与品牌
	 * @return 允许参与品牌
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_promotion_brand")
	public Set<Brand> getBrands() {
		return brands;
	}

	/**
	 * 设置允许参与品牌
	 * @param brands 允许参与品牌
	 */
	public void setBrands(Set<Brand> brands) {
		this.brands = brands;
	}

	/**
	 * 获取赠送优惠券
	 * @return 赠送优惠券
	 */
	@JsonProperty
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_promotion_coupon")
	public Set<Coupon> getCoupons() {
		return coupons;
	}

	/**
	 * 设置赠送优惠券
	 * @param coupons 赠送优惠券
	 */
	public void setCoupons(Set<Coupon> coupons) {
		this.coupons = coupons;
	}

	/**
	 * 获取赠品
	 * @return 赠品
	 */
	@JsonProperty
	@Valid
	@OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	public List<GiftItem> getGiftItems() {
		return giftItems;
	}

	/**
	 * 设置赠品
	 * @param giftItems 赠品
	 */
	public void setGiftItems(List<GiftItem> giftItems) {
		this.giftItems = giftItems;
	}

	@JsonProperty
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(precision = 21, scale = 6)
	public BigDecimal getBail() {
		return bail;
	}

	public void setBail(BigDecimal bail) {
		this.bail = bail;
	}

	@JsonProperty
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(precision = 21, scale = 6)
	public BigDecimal getStepPrice() {
		return stepPrice;
	}

	public void setStepPrice(BigDecimal stepPrice) {
		this.stepPrice = stepPrice;
	}

	@JsonProperty
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	/**
	 * 佣金比率
	 * @return 佣金比率
	 */
	@Column(nullable = true, precision = 12, scale = 6)
	public BigDecimal getBrokerage() {
		return brokerage;
	}

	/**
	 * 佣金比率
	 * @param brokerage 佣金比率
	 */
	public void setBrokerage(BigDecimal brokerage) {
		this.brokerage = brokerage;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	/**
	 * 判断会员是否已经参与
	 * @param member
	 * @return
	 */
	@Transient
	public boolean isMemberPartake(Member member) {
		for (PromotionMember pm : this.getPromotionMembers()) {
			if (pm.getMember().getId().equals(member.getId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断产品是否直接在促销方案内
	 * @param product
	 * @return
	 */
	@Transient
	public boolean isProductPartake(Product product) {
		for (PromotionProduct pp : this.getPromotionProducts()) {
			if (pp.getProduct().getId().equals(product.getId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否已开始
	 * @return 是否已开始
	 */
	@Transient
	public boolean hasBegun() {
		return getBeginDate() == null || new Date().after(getBeginDate());
	}

	/**
	 * 判断是否已结束
	 * @return 是否已结束
	 */
	@Transient
	public boolean hasEnded() {
		return getEndDate() != null && new Date().after(getEndDate());
	}

	/**
	 * 获取访问路径
	 * @return 访问路径
	 */
	@Transient
	public String getPath() {
		if (getId() != null) {
			return PATH_PREFIX + "/" + getId() + PATH_SUFFIX;
		}
		return null;
	}

	/**
	 * 计算促销价格
	 * @param quantity 商品数量
	 * @param price 商品价格
	 * @return 促销价格
	 */
	@Transient
	public BigDecimal calculatePrice(Integer quantity, BigDecimal price) {
		if (price == null || StringUtils.isEmpty(getPriceExpression())) {
			return price;
		}
		BigDecimal result = new BigDecimal(0);
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("quantity", quantity);
			model.put("price", price);
			result = new BigDecimal(FreemarkerUtils.process("#{(" + getPriceExpression() + ");M50}", model));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
		if (result.compareTo(price) > 0) {
			return price;
		}
		return result.compareTo(new BigDecimal(0)) > 0 ? result : new BigDecimal(0);
	}

	/** 获取促销价格 */
	@Transient
	public BigDecimal getPromotionPrice() {
		BigDecimal result = new BigDecimal(0);
		for (PromotionProduct p : promotionProducts) {
			if (p.getPrice() != null && p.getQuantity() != null) {
				result = result.add(p.getPrice().multiply(new BigDecimal(p.getQuantity())));
			}
		}
		return result;
	}

	/** 获取促销原价 */
	@Transient
	public BigDecimal getPromotionOriginalPrice() {
		BigDecimal result = new BigDecimal(0);
		for (PromotionProduct p : promotionProducts) {
			result = result.add(p.getProduct().getPrice().multiply(new BigDecimal(p.getQuantity())));
		}
		return result;
	}

	@JsonProperty
	@Transient
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = getPromotionPrice();
	}

	/** 获取销售价格 */
	@Transient
	public BigDecimal getOriginalPrice() {
		BigDecimal result = BigDecimal.ZERO;
		for (PromotionProduct p : promotionProducts) {
			result = result.add(p.getProduct().getPrice().multiply(new BigDecimal(p.getQuantity())));
		}
		return result;
	}

	/** 获取促销人员个数 */
	@Transient
	public Integer getMemberCount() {
		return promotionMembers.size();
	}

	/**
	 * 计算促销赠送积分
	 * @param quantity 商品数量
	 * @param point 赠送积分
	 * @return 促销赠送积分
	 */
	@Transient
	public Long calculatePoint(Integer quantity, Long point) {
		if (point == null || StringUtils.isEmpty(getPointExpression())) {
			return point;
		}
		Long result = 0L;
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("quantity", quantity);
			model.put("point", point);
			model.put("amount", BigDecimal.ZERO);
			result = Double.valueOf(FreemarkerUtils.process("#{(" + getPointExpression() + ");M50}", model)).longValue();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (result < point) {
			return point;
		}
		return result > 0L ? result : 0L;
	}

	/**
	 * 计算促销赠送积分
	 * @param quantity 商品数量
	 * @param point 赠送积分
	 * @return 促销赠送积分
	 */
	@Transient
	public BigDecimal calculateAmountPoint(Integer quantity, BigDecimal amount) {
		if (amount == null || StringUtils.isEmpty(getPointExpression())) {
			return amount;
		}
		BigDecimal result = BigDecimal.ZERO;
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("quantity", quantity);
			model.put("point", 0L);
			model.put("amount", amount);
			result = new BigDecimal(FreemarkerUtils.process("#{(" + getPointExpression() + ");M50}", model));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (result.compareTo(amount) < 0) {
			return amount;
		}
		return result.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : result;
	}

	/** 获取促销积分 */
	@Transient
	public Long getPromotionPoint() {
		if (getPointExpression() == null) {
			return 0L;
		}
		Long point = 0L;
		BigDecimal amount = BigDecimal.ZERO;
		try {
			for (PromotionProduct p : promotionProducts) {
				if (p.getProduct().getPoint() != null) {
					point = point + p.getProduct().getPoint() * p.getQuantity();
				} else {
					amount = amount.add(p.getPrice());
				}
			}
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("quantity", 1L);
			model.put("point", point);
			point = Double.valueOf(FreemarkerUtils.process("#{(" + getPointExpression() + ");M50}", model)).longValue();

			model.clear();
			model.put("quantity", 1L);
			model.put("amount", amount);
			amount = new BigDecimal(FreemarkerUtils.process("#{(" + getPointExpression() + ");M50}", model));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return point + amount.longValue();
	}

	/** 删除前处理 */
	@PreRemove
	public void preRemove() {
		Set<Order> orders = getOrders();
		if (orders != null) {
			for (Order order : orders) {
				order.setPromotionScheme(null);
			}
		}
	}
}