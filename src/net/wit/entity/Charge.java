/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Entity - 管理员
 * 
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_charge")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_charge_sequence")
public class Charge extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

//	private Long memberId;
//	private Long tenantId;
	
	private Member member;

	private Tenant tenant;
	
	private BigDecimal charge;
	private String chargeDate;
	
	private Status status;
	private Type type;
	
	private String statusName;
	
	private String txNo;
	
	public enum Type {
		/** 奖金*/
		bonus, /** 佣金*/
		charge
	}
	
	public enum Status {
		/** 未领取*/
		notReceive ,
		/** 审核中*/
		receiving,
		/** 已领取*/
		received,
		/** 退回*/
		returned,
		/** 冻结 前台业务逻辑*/ 
		freezed,
		/** 无效 */
		invalid
	}
	
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public BigDecimal getCharge() {
		return charge;
	}
	public void setCharge(BigDecimal charge) {
		this.charge = charge;
	}
	public String getChargeDate() {
		return chargeDate;
	}
	public void setChargeDate(String chargeDate) {
		this.chargeDate = chargeDate;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tenant_id")
	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
	@Transient
	public String getStatusName() {
		if(this.status == Status.notReceive){
			statusName = "未发放";
		}else if(this.status == Status.receiving){
			statusName = "申请中";
		}else if(this.status == Status.received){
			statusName = "已发放";
		}else if(this.status == Status.returned){
			statusName = "已退回";
		}else if(this.status == Status.freezed){
			statusName = "冻结";
		}
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getTxNo() {
		return txNo;
	}
	public void setTxNo(String txNo) {
		this.txNo = txNo;
	}

}