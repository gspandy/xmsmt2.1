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

/**
 * Entity - 管理员
 * 
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_rent")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_rent_sequence")
public class Rent extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

//	private Long memberId;
//	private Long tenantId;
	
	private Member member;

	private Tenant tenant;
	
	private BigDecimal rent;
	private String rentDate;
	
	private String txNo;
	
	private Status status;
	
	public enum Status {
		/** 未收取*/
		notCharge , /** 已收取*/
		charged,/** 系统补助*/
		system
	}
	
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public BigDecimal getRent() {
		return rent;
	}
	public void setRent(BigDecimal rent) {
		this.rent = rent;
	}
	public String getRentDate() {
		return rentDate;
	}
	public void setRentDate(String rentDate) {
		this.rentDate = rentDate;
	}
	public String getTxNo() {
		return txNo;
	}
	public void setTxNo(String txNo) {
		this.txNo = txNo;
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

}