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
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.ShippingStatus;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;

/**
 * Entity - 子订单
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_trade")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_trade_sequence")
public class Trade extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/** 订单名称分隔符 */
	private static final String NAME_SEPARATOR = " ";

	/** 配送验证码 **/
	@Expose
	private String sn;

	/** 配送状态 */
	@Expose
	private ShippingStatus shippingStatus;

	/** 安全密匙 */
	private SafeKey safeKey;

	/** 商家 */
	@Expose
	private Tenant tenant;

	/** 最终发货时间 */
	@Expose
	private Date shippingDate;

	/** 确认收货截止时间 */
	private Date confirmDueDate;

	/** 申请延期申请 */
	private boolean delayPermit;

	/** 税金 */
	private BigDecimal tax;

	/** 运费 */
	@Expose
	private BigDecimal freight;

	/** 调整金额 */
	private BigDecimal offsetAmount;

	/** 买家评价 */
	private Review memberReview;

	/** 卖家评价 */
	private Review tenantReview;

	/** 操作员 */
	private Member operator;

	/** 锁定到期时间 */
	private Date lockExpire;

	/** 订单 */
	private Order order;

	/** 订单项 */
	@Expose
	private List<OrderItem> orderItems = new ArrayList<OrderItem>();

	/** 发货单 */
	private Set<Shipping> shippings = new HashSet<Shipping>();

	/** 退款单 */
	private Set<Refunds> refunds = new HashSet<Refunds>();

	/** 退货单 */
	private Set<Returns> returns = new HashSet<Returns>();

	/** 申请信息 */
	private Set<TradeApply> tradeApplys = new HashSet<TradeApply>();

	/** 售后服务 */
	private Set<TradeFollowup> tradeFollowups = new HashSet<TradeFollowup>();

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
	 * 获取商家
	 * @return 商家
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	public Tenant getTenant() {
		return tenant;
	}

	/**
	 * 设置商家
	 * @param member 商家
	 */
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
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
	 * 获取操作员
	 * @return 操作员
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public Member getOperator() {
		return operator;
	}

	/**
	 * 设置操作员
	 * @param operator 操作员
	 */
	public void setOperator(Member operator) {
		this.operator = operator;
	}

	/**
	 * 获取安全密匙
	 * @return 安全密匙
	 */
	@Embedded
	public SafeKey getSafeKey() {
		return safeKey;
	}

	/**
	 * 设置安全密匙
	 * @param safeKey 安全密匙
	 */
	public void setSafeKey(SafeKey safeKey) {
		this.safeKey = safeKey;
	}

	@JsonProperty
	@Column(nullable = false, updatable = false, unique = true, length = 100)
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
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
	 * 获取最终发货时间
	 * @return 最终发货时间
	 */
	@JsonProperty
	public Date getShippingDate() {
		return shippingDate;
	}

	/**
	 * 设置最终发货时间
	 * @param shippingDate 最终发货时间
	 */
	public void setShippingDate(Date shippingDate) {
		this.shippingDate = shippingDate;
	}

	/**
	 * 获取确认收货截止时间
	 * @return 确认收货截止时间
	 */
	public Date getConfirmDueDate() {
		return confirmDueDate;
	}

	/**
	 * 设置确认收货截止时间
	 * @param lockExpire 确认收货截止时间
	 */
	public void setConfirmDueDate(Date confirmDueDate) {
		this.confirmDueDate = confirmDueDate;
	}

	/**
	 * 获取申请延期申请
	 * @return 申请延期申请
	 */
	public boolean isDelayPermit() {
		return delayPermit;
	}

	/**
	 * 设置申请延期申请
	 * @param delayPermit 申请延期申请
	 */
	public void setDelayPermit(boolean delayPermit) {
		this.delayPermit = delayPermit;
	}

	/**
	 * 获取买家评价
	 * @return 买家评价
	 */
	@OneToOne(mappedBy = "memberTrade", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, optional = true)
	public Review getMemberReview() {
		return memberReview;
	}

	/**
	 * 设置买家评价
	 * @param memberReview 买家评价
	 */
	public void setMemberReview(Review memberReview) {
		this.memberReview = memberReview;
	}

	/**
	 * 获取卖家评价
	 * @return 卖家评价
	 */
	@OneToOne(mappedBy = "tenantTrade", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, optional = true)
	public Review getTenantReview() {
		return tenantReview;
	}

	/**
	 * 设置卖家评价
	 * @param memberReview 卖家评价
	 */
	public void setTenantReview(Review tenantReview) {
		this.tenantReview = tenantReview;
	}

	/**
	 * 获取订单项
	 * @return 订单项
	 */
	@JsonProperty
	@Valid
	@NotEmpty
	@OneToMany(mappedBy = "trade", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("isGift asc")
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
	 * 获取发货单
	 * @return 发货单
	 */
	@OneToMany(mappedBy = "trade", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
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

	/**
	 * 获取退款单
	 * @return 退款单
	 */
	@OneToMany(mappedBy = "trade", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
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

	/**
	 * 获取退货单
	 * @return 退货单
	 */
	@OneToMany(mappedBy = "trade", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
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

	/**
	 * 获取申请信息
	 * @return 申请信息
	 */
	@OneToMany(mappedBy = "trade", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@OrderBy("createDate asc")
	public Set<TradeApply> getTradeApplys() {
		return tradeApplys;
	}

	/**
	 * 设置申请信息
	 * @param returns 申请信息
	 */
	public void setTradeApplys(Set<TradeApply> tradeApplys) {
		this.tradeApplys = tradeApplys;
	}

	/**
	 * 获取售后服务
	 * @return 售后服务
	 */
	@OneToMany(mappedBy = "trade", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@OrderBy("createDate asc")
	public Set<TradeFollowup> getTradeFollowups() {
		return tradeFollowups;
	}

	/**
	 * 设置售后服务
	 * @param tradeFollowups 售后服务
	 */
	public void setTradeFollowups(Set<TradeFollowup> tradeFollowups) {
		this.tradeFollowups = tradeFollowups;
	}

	/**
	 * 获取税金
	 * @return 税金
	 */
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6, columnDefinition = "decimal default 0")
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
	 * 获取运费
	 * @return 运费
	 */
	@NotNull
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6, columnDefinition = "decimal default 0")
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
	 * 获取调整金额
	 * @return 调整金额
	 */
	@NotNull
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6, columnDefinition = "decimal default 0")
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

	// ========================================================================================================//
	/**
	 * 判断是否已锁定
	 * @param operator 操作员
	 * @return 是否已锁定
	 */
	@Transient
	public boolean isLocked(Member operator) {
		return getLockExpire() != null && new Date().before(getLockExpire()) && ((operator != null && !operator.equals(getOperator())) || (operator == null && getOperator() != null));
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

	/** 获取商品数量 */
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
	@Transient
	public BigDecimal getPrice() {
		BigDecimal price = BigDecimal.ZERO;
		if (getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null && orderItem.getSubtotal() != null) {
					price = price.add(orderItem.getSubtotal());
				}
			}
		}
		return price;
	}

	/** 是否允许退货申请 */
	@Transient
	public boolean isReturnAllowed() {
		if (OrderStatus.confirmed != this.getOrder().getOrderStatus()) {
			return false;
		}
		for (TradeApply apply : getTradeApplys()) {
			if (TradeApply.Status.submited == apply.getStatus()) {
				return false;
			}
		}
		return ShippingStatus.accept != this.shippingStatus;
	}

	/** 是否允许换货申请 */
	@Transient
	public boolean getIsChangeAllowed() {
		if (OrderStatus.confirmed != this.getOrder().getOrderStatus()) {
			return false;
		}
		for (TradeApply apply : getTradeApplys()) {
			if (TradeApply.Status.submited == apply.getStatus()) {
				return false;
			}
		}
		return ShippingStatus.accept != this.shippingStatus;
	}

	/** 是否允许申请售后 */
	@Transient
	public boolean getIsFollowupAllowed() {
		if (ShippingStatus.accept != this.shippingStatus) {
			return false;
		}
		if (!this.getTradeFollowups().isEmpty()) {
			return false;
		}
		return true;
	}

	/** 获取当前申请信息 */
	@Transient
	public TradeApply getSubmitApply() {
		for (TradeApply apply : getTradeApplys()) {
			if (TradeApply.Status.submited == apply.getStatus()) {
				return apply;
			}
		}
		return null;
	}

	/** 实发商品金额 */
	@Transient
	public BigDecimal getRealHairAmount() {
		BigDecimal price = BigDecimal.ZERO;
		if (getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null && orderItem.getRealHairSubtotal() != null) {
					price = price.add(orderItem.getRealHairSubtotal());
				}
			}
		}
		return price;
	}

	/** 退款金额 */
	@Transient
	public BigDecimal getRefundAmount() {
		BigDecimal amount = BigDecimal.ZERO;
		for (Refunds refund : getRefunds()) {
			amount = amount.add(refund.getAmount());
		}
		return amount;
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

}