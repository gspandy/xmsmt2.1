/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity - 管理员
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_order_settlement")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_order_settlement_sequence")
public class OrderSettlement extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

	public enum SettlementStatus {
		/** 未完成 */
		uncomplete,
		/** 完成 */
		complete, 
		/** 已结算 */
		settlement,
		/** 已取消 */
		cancel,
		/**已发放 由回调修改*/
		recevied
	}

	/** 订单 */
	private Order order;

	/** 店主 */
	private Member owner;

	/** 会员 */
	private Member member;

	/** 下单金额 */
	private BigDecimal orderAmount;

	/** 下单分享佣金 */
	private BigDecimal orderCharge;

	/** 退货分享佣金 */
	private BigDecimal returnCharge;

	/** 退货结算金额 */
	private BigDecimal orderReturnAmt;

	/** 下单结算金额=下单金额-退货金额 */
	private BigDecimal orderSettleAmt;

	/** 结算分享佣金=下单分享佣金-退货分享佣金 */
	private BigDecimal settleCharge;

	/** 预计完成时间，退货发货，修改预计完成时间 */
	private Date planFinishDate;

	/** 完成时间 */
	private Date finishDate;

	/** 是否完成 */
	private SettlementStatus status;

	private String remark;
	
	private Long chargeId;
	/**是否有效,店主解绑以后无效*/
	private boolean invalid;

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}

	public BigDecimal getOrderCharge() {
		return orderCharge;
	}

	public void setOrderCharge(BigDecimal orderCharge) {
		this.orderCharge = orderCharge;
	}

	public BigDecimal getReturnCharge() {
		return returnCharge;
	}

	public void setReturnCharge(BigDecimal returnCharge) {
		this.returnCharge = returnCharge;
	}

	public BigDecimal getOrderReturnAmt() {
		return orderReturnAmt;
	}

	public void setOrderReturnAmt(BigDecimal orderReturnAmt) {
		this.orderReturnAmt = orderReturnAmt;
	}

	/** 下单结算金额=下单金额-退货金额 */
	public BigDecimal getOrderSettleAmt() {
		return getOrderAmount().subtract(getOrderReturnAmt());
	}

	public void setOrderSettleAmt(BigDecimal orderSettleAmt) {
		this.orderSettleAmt = orderSettleAmt;
	}

	/** 结算分享佣金=下单分享佣金-退货分享佣金 */
	public BigDecimal getSettleCharge() {
		return getOrderCharge().subtract(getReturnCharge());
	}

	public void setSettleCharge(BigDecimal settleCharge) {
		this.settleCharge = settleCharge;
	}

	public Date getPlanFinishDate() {
		return planFinishDate;
	}

	public void setPlanFinishDate(Date planFinishDate) {
		this.planFinishDate = planFinishDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	/**
	 * 获取 order
	 * @return order
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	public Order getOrder() {
		return order;
	}

	/**
	 * 设置 order
	 * @param order order
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * 获取 owner
	 * @return owner
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
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
	 * 获取 member
	 * @return member
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	public Member getMember() {
		return member;
	}

	/**
	 * 设置 member
	 * @param member member
	 */
	public void setMember(Member member) {
		this.member = member;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 获取 status
	 * @return status
	 */
	@Column(nullable= false, columnDefinition = "int default 0")
	public SettlementStatus getStatus() {
		return status;
	}

	/**
	 * 设置 status
	 * @param status status
	 */
	public void setStatus(SettlementStatus status) {
		this.status = status;
	}

	public Long getChargeId() {
		return chargeId;
	}

	public void setChargeId(Long chargeId) {
		this.chargeId = chargeId;
	}

	public boolean isInvalid() {
		return invalid;
	}
	@Column(nullable= false, name = "invalid", columnDefinition = "bit default 0")
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

}