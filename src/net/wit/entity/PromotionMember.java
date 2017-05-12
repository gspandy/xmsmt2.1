/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;

/**
 * Entity - 促销
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_promotion_member")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_promotion_member_sequence")
public class PromotionMember extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/** 参与状态 */
	public enum Status {

		/** 参与中 */
		partake,

		/** 完成 */
		finished,

		/** 取消 */
		cancel
	}

	/** 促销方案 */
	private Promotion promotion;

	/** 会员信息 */
	@Expose
	private Member member;

	/** 参与数量 */
	private Integer quantity = 1;

	/** 出价 */
	private BigDecimal offerPrice = BigDecimal.ZERO;

	/** 优惠码 */
	private CouponCode couponCode;

	/** 随机码 */
	private String random;

	/** 参与情况 */
	private Status status;

	/** 收货地址 */
	private Receiver receiver;

	/** 预付款记录 */
	private Deposit deposit;

	/** 代理竞价 */
	private BigDecimal proxyPrice;

	/** 备注信息 */
	private String memo;

	@ManyToOne(fetch = FetchType.LAZY)
	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	@Min(0)
	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(precision = 21, scale = 6)
	public BigDecimal getOfferPrice() {
		return offerPrice;
	}

	public void setOfferPrice(BigDecimal offerPrice) {
		this.offerPrice = offerPrice;
	}

	@OneToOne(fetch = FetchType.LAZY)
	public CouponCode getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(CouponCode couponCode) {
		this.couponCode = couponCode;
	}

	public String getRandom() {
		return random;
	}

	public void setRandom(String random) {
		this.random = random;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Digits(integer = 12, fraction = 3)
	@Column(precision = 21, scale = 6)
	public BigDecimal getProxyPrice() {
		return proxyPrice;
	}

	public void setProxyPrice(BigDecimal proxyPrice) {
		this.proxyPrice = proxyPrice;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Receiver getReceiver() {
		return receiver;
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Deposit getDeposit() {
		return deposit;
	}

	public void setDeposit(Deposit deposit) {
		this.deposit = deposit;
	}

}