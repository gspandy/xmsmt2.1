/*
m * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

/**
 * Entity - 退款单
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_refunds")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_refunds_sequence")
public class Refunds extends BaseEntity {

	private static final long serialVersionUID = 354885216604823632L;

	/** 方式 */
	public enum Method {
		/** 在线支付 */
		online, /** 线下支付 */
		offline, /** 预存款支付 */
		deposit
	}

	/** 订单金额结算状态 */
	public enum Status {
		/** 未确认 */
		uncomfirm, /** 已确认 */
		comfirm
	}

	public enum RefurnsStatus {
		/** 申请 */
		apply, /** 同意 */
		agree, /** 拒绝 */
		reject, /** 完成 */
		complete, cancel/** 取消 */
	}

	/** 编号 */
	private String sn;

	/** 方式 */
	private Method method;

	/** 状态 */
	private RefurnsStatus status;

	/** 支付方式 */
	private String paymentMethod;

	/** 退款银行 */
	private String bank;

	/** 退款账号 */
	private String account;

	/** 退款金额 */
	private BigDecimal amount;


	/** 收款人 */
	private String payee;

	/** 操作员 */
	private String operator;

	/** 备注 */
	private String memo;

	/** 订单 */
	private Order order;

	/** 子订单 */
	private Trade trade;

	/**
	 * 获取编号
	 * @return 编号
	 */
	@Column(nullable = false, updatable = false, unique = true, length = 100)
	public String getSn() {
		return sn;
	}

	/**
	 * 设置编号
	 * @param sn 编号
	 */
	public void setSn(String sn) {
		this.sn = sn;
	}

	/**
	 * 获取方式
	 * @return 方式
	 */
	@NotNull
	@Column(nullable = false, updatable = false)
	public Method getMethod() {
		return method;
	}

	/**
	 * 设置方式
	 * @param method 方式
	 */
	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * 获取状态
	 * @return 状态
	 */
	@NotNull
	@Column(nullable = false)
	public RefurnsStatus getStatus() {
		return status;
	}

	/**
	 * 设置状态
	 * @param method 状态
	 */
	public void setStatus(RefurnsStatus status) {
		this.status = status;
	}

	/**
	 * 获取支付方式
	 * @return 支付方式
	 */
	@Column(updatable = false)
	public String getPaymentMethod() {
		return paymentMethod;
	}

	/**
	 * 设置支付方式
	 * @param paymentMethod 支付方式
	 */
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	/**
	 * 获取退款银行
	 * @return 退款银行
	 */
	@Length(max = 200)
	@Column(updatable = false)
	public String getBank() {
		return bank;
	}

	/**
	 * 设置退款银行
	 * @param bank 退款银行
	 */
	public void setBank(String bank) {
		this.bank = bank;
	}

	/**
	 * 获取退款账号
	 * @return 退款账号
	 */
	@Length(max = 200)
	@Column(updatable = false)
	public String getAccount() {
		return account;
	}

	/**
	 * 设置退款账号
	 * @param account 退款账号
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * 获取退款金额
	 * @return 退款金额
	 */
	@NotNull
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * 设置退款金额
	 * @param amount 退款金额
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * 获取收款人
	 * @return 收款人
	 */
	@Length(max = 200)
	public String getPayee() {
		return payee;
	}

	/**
	 * 设置收款人
	 * @param payee 收款人
	 */
	public void setPayee(String payee) {
		this.payee = payee;
	}

	/**
	 * 获取操作员
	 * @return 操作员
	 */
	@Column(nullable = false, updatable = false)
	public String getOperator() {
		return operator;
	}

	/**
	 * 设置操作员
	 * @param operator 操作员
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * 获取备注
	 * @return 备注
	 */
	@Length(max = 200)
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
	 * 获取订单
	 * @return 订单
	 */
	@NotNull
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
	public Trade getTrade() {
		return trade;
	}

	/**
	 * 设置子订单
	 * @param trade 子订单
	 */
	public void setTrade(Trade trade) {
		this.trade = trade;
	}


}