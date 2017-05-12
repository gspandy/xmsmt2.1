/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import net.wit.Setting;
import net.wit.constant.SettingConstant;
import net.wit.entity.Refunds.RefurnsStatus;
import net.wit.entity.Returns.ReturnStatus;
import net.wit.util.SettingUtils;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;

/**
 * Entity - 订单
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_order")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_order_sequence")
public class Order extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/** 订单名称分隔符 */
	private static final String NAME_SEPARATOR = " ";

	/** 订单类型 */
	public enum OrderType {
		/** 单商家 */
		single, /** 多商家 */
		composite
	}

	/** 订单状态 */
	public enum OrderStatus {
		/** 未确认 */
		unconfirmed, /** 已确认 */
		confirmed, /** 已完成 */
		completed, /** 已取消 */
		cancelled
	}

	/** 支付状态 */
	public enum PaymentStatus {
		/** 未支付 */
		unpaid, /** 部分支付 */
		partialPayment, /** 已支付 */
		paid, /** 部分退款 */
		partialRefunds, /** 已退款 */
		refunded, /** 退款申请 */
		refundapply, cancelled, /** 取消退款 */
		rejected, /** 拒绝退款 */

		/** 已支付，未到账 */
		unreached
	}

	/** 配送状态 */
	public enum ShippingStatus {
		/** 未发货 */
		unshipped, /** 部分发货 */
		partialShipment, /** 已发货 */
		shipped, /** 部分退货 */
		partialReturns, /** 已退货 */
		returned, /** 确认收货 */
		accept, /** 退换货申请 */
		apply, cancelled/** 取消退货 */
	}

	/** 订单编号 */
	@Expose
	private String sn;

	/** 会员 */
	private Member member;

	/** 所属企业ID */
	private Tenant tenant;

	/** 所属店主 */
	private Member owner;

	/** 使用的内购券 */
	private Ticket ticket;

	/** 分享佣金 */
	private BigDecimal chargeAmt;

	/** 结算单 */
	private OrderSettlement orderSettlement;

	/** 收货人 */
	@Expose
	private String consignee;

	/** 地区名称 */
	@Expose
	private String areaName;

	/** 地址 */
	@Expose
	private String address;

	/** 邮编 */
	@Expose
	private String zipCode;

	/** 电话 */
	@Expose
	private String phone;

	/** 支付方式名称 */
	private String paymentMethodName;

	/** 支付方式 */
	private PaymentMethod paymentMethod;

	/** 订单状态 */
	@Expose
	private OrderStatus orderStatus;

	private String orderStatusName;

	private String orderMobileStatus;

	/** 支付状态 */
	@Expose
	private PaymentStatus paymentStatus;

	/** 配送状态 */
	@Expose
	private ShippingStatus shippingStatus;

	/** 支付手续费 */
	private BigDecimal fee;

	/** 运费 */
	private BigDecimal freight;

	/** 促销折扣 */
	private BigDecimal promotionDiscount;

	/** 优惠券折扣 */
	private BigDecimal couponDiscount;

	/** 调整金额 */
	private BigDecimal offsetAmount;

	/** 已付金额 */
	@Expose
	private BigDecimal amountPaid;

	/** 赠送积分 */
	private Long point;

	/** 是否开据发票 */
	@Expose
	private Boolean isInvoice;

	/** 发票抬头 */
	@Expose
	private String invoiceTitle;

	/** 税金 */
	private BigDecimal tax;

	/** 附言 */
	@Expose
	private String memo;

	/** 预约时间 */
	private Appointment appointment;

	/** 发票 */
	private InvoiceManagement invoiceManagement;

	/** 促销 */
	@Expose
	private String promotion;

	/** 到期时间 */
	@Expose
	private Date expire;

	/** 锁定到期时间 */
	private Date lockExpire;

	/** 是否已分配库存 */
	private Boolean isAllocatedStock;

	/** 配送方式名称 */
	private String shippingMethodName;

	/** 地区 */
	private Area area;

	/** 配送方式 */
	private ShippingMethod shippingMethod;

	/** 操作员 */
	private Admin operator;

	/** 订单类型 */
	private OrderType orderType;

	/** 优惠码 */
	private CouponCode couponCode;

	/** 促销方案(由团购/拍卖生成) */
	private Promotion promotionScheme;

	/** vip推荐人 */
	private TenantShopkeeper shopkeeper;

	/** 邀请奖金 */
	private BigDecimal bonus;

	/** 优惠券 */
	private List<Coupon> coupons = new ArrayList<Coupon>();

	/** 子订单 */
	@Expose
	private List<Trade> trades = new ArrayList<Trade>();

	/** 订单项 */
	private List<OrderItem> orderItems = new ArrayList<OrderItem>();

	/** 订单日志 */
	private Set<OrderLog> orderLogs = new HashSet<OrderLog>();

	/** 预存款 */
	private Set<Deposit> deposits = new HashSet<Deposit>();

	/** 收款单 */
	private Set<Payment> payments = new HashSet<Payment>();

	/** 退款单 */
	private Set<Refunds> refunds = new HashSet<Refunds>();

	/** 发货单 */
	private Set<Shipping> shippings = new HashSet<Shipping>();

	/** 退货单 */
	private Set<Returns> returns = new HashSet<Returns>();

	/** 是否延长收货 */
	@Expose
	private Boolean isExtend;

	/**
	 * 获取订单编号
	 * @return 订单编号
	 */
	@JsonProperty
	@Column(nullable = false, updatable = false, unique = true, length = 100)
	public String getSn() {
		return sn;
	}

	/**
	 * 设置订单编号
	 * @param sn 订单编号
	 */
	public void setSn(String sn) {
		this.sn = sn;
	}

	/**
	 * 获取订单状态
	 * @return 订单状态
	 */
	@JsonProperty
	@Column(nullable = false)
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	/**
	 * 设置订单状态
	 * @param orderStatus 订单状态
	 */
	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	/**
	 * 获取支付状态
	 * @return 支付状态
	 */
	@JsonProperty
	@Column(nullable = false)
	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	/**
	 * 设置支付状态
	 * @param paymentStatus 支付状态
	 */
	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	/**
	 * 获取配送状态
	 * @return 配送状态
	 */
	@JsonProperty
	@Column(nullable = false)
	public ShippingStatus getShippingStatus() {
		return shippingStatus;
	}

	/**
	 * 设置配送状态
	 * @param shippingStatus 配送状态
	 */
	public void setShippingStatus(ShippingStatus shippingStatus) {
		this.shippingStatus = shippingStatus;
	}

	/**
	 * 获取支付手续费
	 * @return 支付手续费
	 */
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getFee() {
		return fee;
	}

	/**
	 * 设置支付手续费
	 * @param fee 支付手续费
	 */
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	/**
	 * 获取运费
	 * @return 运费
	 */
	@NotNull
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getFreight() {
		return freight;
	}

	/**
	 * 设置运费
	 * @param freight 运费
	 */
	public void setFreight(BigDecimal freight) {
		this.freight = freight;
	}

	/**
	 * 获取促销折扣
	 * @return 促销折扣
	 */
	@Column(nullable = false, updatable = false, precision = 21, scale = 6)
	public BigDecimal getPromotionDiscount() {
		return promotionDiscount;
	}

	/**
	 * 设置促销折扣
	 * @param promotionDiscount 促销折扣
	 */
	public void setPromotionDiscount(BigDecimal promotionDiscount) {
		this.promotionDiscount = promotionDiscount;
	}

	/**
	 * 获取优惠券折扣
	 * @return 优惠券折扣
	 */
	@Column(nullable = false, updatable = false, precision = 21, scale = 6)
	public BigDecimal getCouponDiscount() {
		return couponDiscount;
	}

	/**
	 * 设置优惠券折扣
	 * @param couponDiscount 优惠券折扣
	 */
	public void setCouponDiscount(BigDecimal couponDiscount) {
		this.couponDiscount = couponDiscount;
	}

	/**
	 * 获取调整金额
	 * @return 调整金额
	 */
	@NotNull
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getOffsetAmount() {
		return offsetAmount;
	}

	/**
	 * 设置调整金额
	 * @param offsetAmount 调整金额
	 */
	public void setOffsetAmount(BigDecimal offsetAmount) {
		this.offsetAmount = offsetAmount;
	}

	/**
	 * 获取已付金额
	 * @return 已付金额
	 */
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getAmountPaid() {
		BigDecimal ap = new BigDecimal(0);
		for (OrderItem ot : getOrderItems()) {
			ap = ap.add(ot.getPrice().multiply(new BigDecimal(ot.getQuantity())));
		}
		return ap;
	}

	/**
	 * 设置已付金额
	 * @param amountPaid 已付金额
	 */
	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	/**
	 * 获取赠送积分
	 * @return 赠送积分
	 */
	@JsonProperty
	@NotNull
	@Min(0)
	@Column(nullable = false)
	public Long getPoint() {
		return point;
	}

	/**
	 * 设置赠送积分
	 * @param point 赠送积分
	 */
	public void setPoint(Long point) {
		this.point = point;
	}

	/**
	 * 获取收货人
	 * @return 收货人
	 */
	@JsonProperty
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getConsignee() {
		return consignee;
	}

	/**
	 * 设置收货人
	 * @param consignee 收货人
	 */
	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	/**
	 * 获取地区名称
	 * @return 地区名称
	 */
	@Column(nullable = false)
	public String getAreaName() {
		return areaName;
	}

	/**
	 * 设置地区名称
	 * @param areaName 地区名称
	 */
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	/**
	 * 获取地址
	 * @return 地址
	 */
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	@JsonProperty
	public String getAddress() {
		return address;
	}

	/**
	 * 设置地址
	 * @param address 地址
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 获取邮编
	 * @return 邮编
	 */
	@JsonProperty
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * 设置邮编
	 * @param zipCode 邮编
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * 获取电话
	 * @return 电话
	 */
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	@JsonProperty
	public String getPhone() {
		return phone;
	}

	/**
	 * 设置电话
	 * @param phone 电话
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * 获取是否开据发票
	 * @return 是否开据发票
	 */
	@NotNull
	@Column(nullable = false)
	public Boolean getIsInvoice() {
		return isInvoice;
	}

	/**
	 * 设置是否开据发票
	 * @param isInvoice 是否开据发票
	 */
	public void setIsInvoice(Boolean isInvoice) {
		this.isInvoice = isInvoice;
	}

	/**
	 * 获取发票抬头
	 * @return 发票抬头
	 */
	@Length(max = 200)
	public String getInvoiceTitle() {
		return invoiceTitle;
	}

	/**
	 * 设置发票抬头
	 * @param invoiceTitle 发票抬头
	 */
	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}

	/**
	 * 获取税金
	 * @return 税金
	 */
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getTax() {
		return tax;
	}

	/**
	 * 设置税金
	 * @param tax 税金
	 */
	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	/**
	 * 获取附言
	 * @return 附言
	 */
	@Length(max = 200)
	public String getMemo() {
		return memo;
	}

	/**
	 * 预约时间
	 * @param appointment 预约时间
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	/**
	 * 设置附言
	 * @param memo 附言
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * 获取促销
	 * @return 促销
	 */
	@Column(updatable = false)
	public String getPromotion() {
		return promotion;
	}

	/**
	 * 设置促销
	 * @param promotion 促销
	 */
	public void setPromotion(String promotion) {
		this.promotion = promotion;
	}

	/**
	 * 获取到期时间
	 * @return 到期时间
	 */
	@JsonProperty
	public Date getExpire() {
		return expire;
	}

	/**
	 * 设置到期时间
	 * @param expire 到期时间
	 */
	public void setExpire(Date expire) {
		this.expire = expire;
	}

	/**
	 * 获取锁定到期时间
	 * @return 锁定到期时间
	 */
	public Date getLockExpire() {
		return lockExpire;
	}

	/**
	 * 设置锁定到期时间
	 * @param lockExpire 锁定到期时间
	 */
	public void setLockExpire(Date lockExpire) {
		this.lockExpire = lockExpire;
	}

	/**
	 * 获取是否已分配库存
	 * @return 是否已分配库存
	 */
	@Column(nullable = false)
	public Boolean getIsAllocatedStock() {
		return isAllocatedStock;
	}

	/**
	 * 设置是否已分配库存
	 * @param isAllocatedStock 是否已分配库存
	 */
	public void setIsAllocatedStock(Boolean isAllocatedStock) {
		this.isAllocatedStock = isAllocatedStock;
	}

	/**
	 * 获取支付方式名称
	 * @return 支付方式名称
	 */
	@Column(nullable = false)
	@JsonProperty
	public String getPaymentMethodName() {
		return paymentMethodName;
	}

	/**
	 * 设置支付方式名称
	 * @param paymentMethodName 支付方式名称
	 */
	public void setPaymentMethodName(String paymentMethodName) {
		this.paymentMethodName = paymentMethodName;
	}

	/**
	 * 获取配送方式名称
	 * @return 配送方式名称
	 */
	@Column(nullable = false)
	@JsonProperty
	public String getShippingMethodName() {
		return shippingMethodName;
	}

	/**
	 * 设置配送方式名称
	 * @param shippingMethodName 配送方式名称
	 */
	public void setShippingMethodName(String shippingMethodName) {
		this.shippingMethodName = shippingMethodName;
	}

	/**
	 * 获取地区
	 * @return 地区
	 */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
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

	/**
	 * 获取支付方式
	 * @return 支付方式
	 */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	/**
	 * 设置支付方式
	 * @param paymentMethod 支付方式
	 */
	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	/**
	 * 获取配送方式
	 * @return 配送方式
	 */
	@NotNull
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
	 * 获取操作员
	 * @return 操作员
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public Admin getOperator() {
		return operator;
	}

	/**
	 * 设置操作员
	 * @param operator 操作员
	 */
	public void setOperator(Admin operator) {
		this.operator = operator;
	}

	/**
	 * 获取会员
	 * @return 会员
	 */
	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
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
	 * 获取订单类型
	 * @return 订单类型
	 */
	public OrderType getOrderType() {
		return orderType;
	}

	/**
	 * 设置订单类型
	 * @param orderType 订单类型
	 */
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	/**
	 * 获取优惠码
	 * @return 优惠码
	 */
	@OneToOne(fetch = FetchType.LAZY)
	public CouponCode getCouponCode() {
		return couponCode;
	}

	/**
	 * 设置优惠码
	 * @param couponCode 优惠码
	 */
	public void setCouponCode(CouponCode couponCode) {
		this.couponCode = couponCode;
	}

	/**
	 * 获取促销方案(由团购/拍卖)
	 * @return 促销方案(由团购/拍卖)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public Promotion getPromotionScheme() {
		return promotionScheme;
	}

	/**
	 * 设置促销方案(由团购/拍卖)
	 * @param couponCode 促销方案(由团购/拍卖)
	 */
	public void setPromotionScheme(Promotion promotionScheme) {
		this.promotionScheme = promotionScheme;
	}

	/**
	 * 获取优惠券
	 * @return 优惠券
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_order_coupon")
	public List<Coupon> getCoupons() {
		return coupons;
	}

	/**
	 * 设置优惠券
	 * @param coupons 优惠券
	 */
	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}

	/**
	 * 获取子订单
	 * @return 子订单
	 */
	@JsonProperty
	@Valid
	@NotEmpty
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	public List<Trade> getTrades() {
		return trades;
	}

	/**
	 * 设置子订单
	 * @param trades 子订单
	 */
	public void setTrades(List<Trade> trades) {
		this.trades = trades;
	}

	/**
	 * 获取订单项
	 * @return 订单项
	 */
	@JsonProperty
	@Valid
	@NotEmpty
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	/**
	 * 设置订单项
	 * @param orderItems 订单项
	 */
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	/**
	 * 获取订单日志
	 * @return 订单日志
	 */
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@OrderBy("createDate asc")
	public Set<OrderLog> getOrderLogs() {
		return orderLogs;
	}

	/**
	 * 设置订单日志
	 * @param orderLogs 订单日志
	 */
	public void setOrderLogs(Set<OrderLog> orderLogs) {
		this.orderLogs = orderLogs;
	}

	/**
	 * 获取预存款
	 * @return 预存款
	 */
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
	public Set<Deposit> getDeposits() {
		return deposits;
	}

	/**
	 * 设置预存款
	 * @param deposits 预存款
	 */
	public void setDeposits(Set<Deposit> deposits) {
		this.deposits = deposits;
	}

	/**
	 * 获取收款单
	 * @return 收款单
	 */
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@OrderBy("createDate asc")
	public Set<Payment> getPayments() {
		return payments;
	}

	/**
	 * 设置收款单
	 * @param payments 收款单
	 */
	public void setPayments(Set<Payment> payments) {
		this.payments = payments;
	}

	/**
	 * 获取退款单
	 * @return 退款单
	 */
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@OrderBy("createDate asc")
	public Set<Refunds> getRefunds() {
		return refunds;
	}

	/**
	 * 设置退款单
	 * @param refunds 退款单
	 */
	public void setRefunds(Set<Refunds> refunds) {
		this.refunds = refunds;
	}

	@Transient
	public Refunds getApplayRefunds() {
		Iterator<Refunds> iterator = getRefunds().iterator();
		while (iterator.hasNext()) {
			if (Refunds.RefurnsStatus.apply.equals(iterator.next()))
				return iterator.next();
		}
		return null;
	}

	@Transient
	public Refunds getCompleteRefunds() {
		Iterator<Refunds> iterator = getRefunds().iterator();
		while (iterator.hasNext()) {
			if (Refunds.RefurnsStatus.complete.equals(iterator.next()))
				return iterator.next();
		}
		return null;
	}

	/**
	 * 获取发货单
	 * @return 发货单
	 */
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@OrderBy("createDate asc")
	public Set<Shipping> getShippings() {
		return shippings;
	}

	/**
	 * 设置发货单
	 * @param shippings 发货单
	 */
	public void setShippings(Set<Shipping> shippings) {
		this.shippings = shippings;
	}

	@Transient
	public Shipping getDefaultShipping() {
		Iterator<Shipping> iterator = getShippings().iterator();
		while (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}

	/**
	 * 获取退货单
	 * @return 退货单
	 */
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@OrderBy("createDate asc")
	public Set<Returns> getReturns() {
		return returns;
	}

	/**
	 * 设置退货单
	 * @param returns 退货单
	 */
	public void setReturns(Set<Returns> returns) {
		this.returns = returns;
	}

	@Transient
	public Returns getApplayReturns() {
		Iterator<Returns> iterator = getReturns().iterator();
		while (iterator.hasNext()) {
			if (ReturnStatus.apply.equals(iterator.next().getOrderStat()))
				return iterator.next();
		}
		return null;
	}

	/** 获取订单名称 */
	@Transient
	public String getName() {
		StringBuffer name = new StringBuffer();
		if (getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null && orderItem.getFullName() != null) {
					name.append(NAME_SEPARATOR).append(orderItem.getFullName());
				}
			}
			if (name.length() > 0) {
				name.deleteCharAt(0);
			}
		}
		return name.toString();
	}

	/** 获取商品重量 */
	@Transient
	public int getWeight() {
		int weight = 0;
		if (getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null) {
					weight += orderItem.getTotalWeight();
				}
			}
		}
		return weight;
	}

	/** 获取子订单 */
	@Transient
	public Trade getTrade(Tenant tenant) {
		if (getTrades() != null) {
			for (Trade trade : getTrades()) {
				if (tenant != null && tenant.equals(trade.getTenant())) {
					return trade;
				}
			}
		}
		return null;
	}

	/** 获取子订单 */
	@Transient
	public Trade getTrade(Long id) {
		if (getTrades() != null) {
			for (Trade trade : getTrades()) {
				if (trade.getId().equals(id)) {
					return trade;
				}
			}
		}
		return null;
	}

	/** 获取商品数量 */
	@JsonProperty
	@Transient
	public int getQuantity() {
		int quantity = 0;
		if (getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null && orderItem.getQuantity() != null) {
					quantity += orderItem.getQuantity();
				}
			}
		}
		return quantity;
	}

	/** 获取已发货数量 */
	@Transient
	public int getShippedQuantity() {
		int shippedQuantity = 0;
		if (getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null && orderItem.getShippedQuantity() != null) {
					shippedQuantity += orderItem.getShippedQuantity();
				}
			}
		}
		return shippedQuantity;
	}

	/** 获取已退货数量 */
	@Transient
	public int getReturnQuantity() {
		int returnQuantity = 0;
		if (getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null && orderItem.getReturnQuantity() != null) {
					returnQuantity += orderItem.getReturnQuantity();
				}
			}
		}
		return returnQuantity;
	}

	/** 获取商品价格 */
	@JsonProperty
	@Transient
	public BigDecimal getPrice() {
		BigDecimal price = new BigDecimal(0);
		if (getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null && orderItem.getSubtotal() != null) {
					price = price.add(orderItem.getSubtotal());
				}
			}
		}
		return price;
	}

	/** 市场总价格 */
	@Transient
	public BigDecimal getMarkerAmount() {
		BigDecimal mamout = new BigDecimal(0);
		for (OrderItem ot : getOrderItems()) {
			mamout = mamout.add(ot.getMarkerAmount().multiply(new BigDecimal(ot.getQuantity())));
		}
		return mamout;
	}

	/** 获取订单金额 */
	@JsonProperty
	@Transient
	public BigDecimal getAmount() {
		BigDecimal amount = getPrice();
		if (getFee() != null) {
			amount = amount.add(getFee());
		}
		if (getFreight() != null) {
			amount = amount.add(getFreight());
		}
		if (getPromotionDiscount() != null) {
			amount = amount.subtract(getPromotionDiscount());
		}
		if (getCouponDiscount() != null) {
			amount = amount.subtract(getCouponDiscount());
		}
		if (getOffsetAmount() != null) {
			amount = amount.add(getOffsetAmount());
		}
		if (getTax() != null) {
			amount = amount.add(getTax());
		}
		return amount.compareTo(new BigDecimal(0)) > 0 ? amount : new BigDecimal(0);
	}

	/** 获取应付金额 */
	@Transient
	public BigDecimal getAmountPayable() {
		BigDecimal amountPayable = getAmount().subtract(getAmountPaid());
		return amountPayable.compareTo(new BigDecimal(0)) > 0 ? amountPayable : new BigDecimal(0);
	}

	/** 商家退款上限 */
	@Transient
	public BigDecimal getMaxReturnAmount() {
		BigDecimal amount = getAmount();
		amount = amount.subtract(getFee());
		if (ShippingStatus.unshipped != getShippingStatus()) {
			amount = amount.subtract(getFreight());
		}
		for (Refunds re : getRefunds()) {
			amount = amount.subtract(re.getAmount());
		}
		return amount;
	}

	/** 是否已过期 */
	@Transient
	@JsonProperty
	public boolean isExpired() {
		return getExpire() != null && new Date().after(getExpire());
	}

	/** 是否允许退款申请 */
	@Transient
	public boolean isRefundAllowed() {
		if (getRefunds().size() == 0) {
			return true;
		}
		for (Refunds r : getRefunds()) {
			if (RefurnsStatus.reject.equals(r.getStatus())) {
				return true;
			}
		}
		return false;
	}

	/** 是否允许退货申请 */
	@Transient
	public boolean isReturnsAllowed() {
		if (getReturns().size() == 0) {
			return true;
		}
		for (Returns r : getReturns()) {
			if (ReturnStatus.reject.equals(r.getOrderStat())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取订单项
	 * @param sn 商品编号
	 * @return 订单项
	 */
	@Transient
	public OrderItem getOrderItem(String sn) {
		if (sn != null && getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null && sn.equalsIgnoreCase(orderItem.getSn())) {
					return orderItem;
				}
			}
		}
		return null;
	}

	/**
	 * 是否显示完成
	 * @return boolean
	 */
	@Transient
	public boolean getIsCompleteAllowed() {
		if (OrderStatus.completed == orderStatus) {
			return false;
		}
		for (Trade trade : trades) {
			if (!(trade.getShippingStatus() == ShippingStatus.returned || trade.getShippingStatus() == ShippingStatus.accept)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否显示评价
	 * @return boolean
	 */
	@Transient
	public boolean getIsShowReviewAllowed() {
		for (Trade trade : trades) {
			if (trade.getMemberReview() != null) {
				return true;
			}
		}
		return true;
	}

	/**
	 * 判断是否已锁定
	 * @param operator 操作员
	 * @return 是否已锁定
	 */
	@Transient
	public boolean isLocked(Admin operator) {
		return getLockExpire() != null && new Date().before(getLockExpire()) && ((operator != null && !operator.equals(getOperator())) || (operator == null && getOperator() != null));
	}

	/** 计算税金 */
	@Transient
	public BigDecimal calculateTax() {
		BigDecimal tax = new BigDecimal(0);
		Setting setting = SettingUtils.get();
		if (setting.getIsTaxPriceEnabled()) {
			BigDecimal amount = getPrice();
			if (getPromotionDiscount() != null) {
				amount = amount.subtract(getPromotionDiscount());
			}
			if (getCouponDiscount() != null) {
				amount = amount.subtract(getCouponDiscount());
			}
			if (getOffsetAmount() != null) {
				amount = amount.add(getOffsetAmount());
			}
			tax = amount.multiply(new BigDecimal(setting.getTaxRate().toString()));
		}
		return setting.setScale(tax);
	}

	/** 使用积分上限 */
	@Transient
	public Long getCouponMaxPoint() {
		Setting setting = SettingUtils.get();
		Double defaultPointScale = setting.getDefaultPointScale();
		if (defaultPointScale <= 0) {
			return 0L;
		}
		return getPrice().multiply(SettingConstant.maxCouponPointPer).divide(SettingConstant.amountPointScale).longValue();
	}

	/**
	 * 获取 tenant
	 * @return tenant
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public Tenant getTenant() {
		return tenant;
	}

	/**
	 * 设置 tenant
	 * @param tenant tenant
	 */
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	/**
	 * 获取 owner
	 * @return owner
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public Member getOwner() {
		return owner;
	}

	/**
	 * 设置 owner
	 * @param owner owner
	 */
	public void setOwner(Member owner) {
		this.owner = owner;
	}

	/**
	 * 获取 ticket
	 * @return ticket
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public Ticket getTicket() {
		return ticket;
	}

	/**
	 * 设置 ticket
	 * @param ticket ticket
	 */
	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	/** 持久化前处理 */
	@PrePersist
	public void prePersist() {
		if (getArea() != null) {
			setAreaName(getArea().getFullName());
		}
		if (getPaymentMethod() != null) {
			setPaymentMethodName(getPaymentMethod().getName());
		}
		if (getShippingMethod() != null) {
			setShippingMethodName(getShippingMethod().getName());
		}
	}

	/** 更新前处理 */
	@PreUpdate
	public void preUpdate() {
		if (getArea() != null) {
			setAreaName(getArea().getFullName());
		}
		if (getPaymentMethod() != null) {
			setPaymentMethodName(getPaymentMethod().getName());
		}
		if (getShippingMethod() != null) {
			setShippingMethodName(getShippingMethod().getName());
		}
	}

	/** 删除前处理 */
	@PreRemove
	public void preRemove() {
		Set<Deposit> deposits = getDeposits();
		if (deposits != null) {
			for (Deposit deposit : deposits) {
				deposit.setOrder(null);
			}
		}
	}

	@JsonProperty
	@Column(nullable = true)
	public BigDecimal getChargeAmt() {
		BigDecimal amt = new BigDecimal(0);
		for (OrderItem oi : getOrderItems()) {
			if (oi.getProductRent() != null) {
				amt = amt.add(oi.getProductRent().multiply(new BigDecimal(oi.getQuantity())));
			}
		}
		return amt;
	}

	public void setChargeAmt(BigDecimal chargeAmt) {
		this.chargeAmt = chargeAmt;
	}

	/**
	 * 获取 orderSettlement
	 * @return orderSettlement
	 */
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "order")
	public OrderSettlement getOrderSettlement() {
		return orderSettlement;
	}

	/**
	 * 设置 orderSettlement
	 * @param orderSettlement orderSettlement
	 */
	public void setOrderSettlement(OrderSettlement orderSettlement) {
		this.orderSettlement = orderSettlement;
	}

	@Transient
	public String getOrderStatusName() {
		if (this.orderStatus == Order.OrderStatus.confirmed && this.paymentStatus == Order.PaymentStatus.unpaid && this.shippingStatus == Order.ShippingStatus.unshipped) {// 待支付
			orderStatusName = "待支付";
		} else if (this.orderStatus == Order.OrderStatus.confirmed && this.paymentStatus == Order.PaymentStatus.paid && this.shippingStatus == Order.ShippingStatus.unshipped) {// 待发货
			orderStatusName = "待发货";
		} else if (this.orderStatus == Order.OrderStatus.confirmed && this.paymentStatus == Order.PaymentStatus.paid && this.shippingStatus == Order.ShippingStatus.shipped) {// 待收货(已发货)
			orderStatusName = "待收货";
		} else if (this.orderStatus == Order.OrderStatus.confirmed && this.paymentStatus == Order.PaymentStatus.paid && this.shippingStatus == Order.ShippingStatus.accept) {// 已签收
			orderStatusName = "已签收";
		} else if (this.orderStatus == Order.OrderStatus.completed && this.paymentStatus == Order.PaymentStatus.paid && this.shippingStatus == Order.ShippingStatus.accept) {// 交易完成
			orderStatusName = "交易完成";
		} else if (this.orderStatus == Order.OrderStatus.cancelled && this.paymentStatus == Order.PaymentStatus.unpaid && this.shippingStatus == Order.ShippingStatus.unshipped) {// 交易关闭
			orderStatusName = "交易关闭";
		} else if (this.orderStatus == Order.OrderStatus.confirmed && this.paymentStatus == Order.PaymentStatus.refundapply && this.shippingStatus == Order.ShippingStatus.unshipped) {// 退款中
			orderStatusName = "退款中";
		} else if (this.orderStatus == Order.OrderStatus.confirmed && this.paymentStatus == Order.PaymentStatus.paid && this.shippingStatus == Order.ShippingStatus.apply) {// 退货中
			orderStatusName = "退货中";
		} else if (this.orderStatus == Order.OrderStatus.confirmed && this.paymentStatus == Order.PaymentStatus.paid && this.shippingStatus == Order.ShippingStatus.cancelled) {// 取消退货
			orderStatusName = "取消退货";
		} else if (this.orderStatus == Order.OrderStatus.confirmed && this.paymentStatus == Order.PaymentStatus.cancelled && this.shippingStatus == Order.ShippingStatus.unshipped) {// 取消退款
			orderStatusName = "取消退款";
		} else if (this.orderStatus == Order.OrderStatus.confirmed && this.paymentStatus == Order.PaymentStatus.refunded && this.shippingStatus == Order.ShippingStatus.unshipped) {// 已退款
			orderStatusName = "已退款";
		} else if (this.orderStatus == Order.OrderStatus.confirmed && this.paymentStatus == Order.PaymentStatus.paid && this.shippingStatus == Order.ShippingStatus.returned) {// 退款中
			orderStatusName = "退款中";
		} else if (this.orderStatus == Order.OrderStatus.completed && this.paymentStatus == Order.PaymentStatus.refunded && this.shippingStatus == Order.ShippingStatus.unshipped) {// 已退款
			orderStatusName = "已退款";
		} else if (this.orderStatus == Order.OrderStatus.completed && this.paymentStatus == Order.PaymentStatus.refunded && this.shippingStatus == Order.ShippingStatus.returned) {// 已退款
			orderStatusName = "已退款";
		}else if (this.orderStatus == Order.OrderStatus.confirmed && this.paymentStatus == Order.PaymentStatus.paid && this.shippingStatus == Order.ShippingStatus.partialShipment) {// 部分发货
			orderStatusName = "部分发货";
		}

		return orderStatusName;
	}

	public void setOrderStatusName(String orderStatusName) {
		this.orderStatusName = orderStatusName;
	}

	@Transient
	public String getOrderMobileStatus() {
		return orderMobileStatus;
	}

	public void setOrderMobileStatus(String orderMobileStatus) {
		this.orderMobileStatus = orderMobileStatus;
	}

	@Column(nullable = false, columnDefinition = "int default 0")
	public Boolean getIsExtend() {
		return isExtend;
	}

	public void setIsExtend(Boolean isExtend) {
		this.isExtend = isExtend;
	}

	/**
	 * 获取 invoiceManagement
	 * @return invoiceManagement
	 */
	@OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public InvoiceManagement getInvoiceManagement() {
		return invoiceManagement;
	}

	/**
	 * 设置 invoiceManagement
	 * @param invoiceManagement invoiceManagement
	 */
	public void setInvoiceManagement(InvoiceManagement invoiceManagement) {
		this.invoiceManagement = invoiceManagement;
	}

	@Transient
	public Payment getEffect() {
		for (Payment p : getPayments()) {
			if (Payment.Status.success.equals(p.getStatus()))
				return p;
		}
		return null;
	}

	/**
	 * 获取 shopkeeper
	 * @return shopkeeper
	 */
	@Transient
	public TenantShopkeeper getShopkeeper() {
		return shopkeeper;
	}

	/**
	 * 设置 shopkeeper
	 * @param shopkeeper shopkeeper
	 */
	public void setShopkeeper(TenantShopkeeper shopkeeper) {
		this.shopkeeper = shopkeeper;
	}

	/**
	 * 获取 bonus
	 * @return bonus
	 */
	@Transient
	public BigDecimal getBonus() {
		return bonus;
	}

	/**
	 * 设置 bonus
	 * @param bonus bonus
	 */
	public void setBonus(BigDecimal bonus) {
		this.bonus = bonus;
	}
}