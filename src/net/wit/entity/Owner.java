/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity - 管理员
 * 
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_owner")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_owner_sequence")
public class Owner extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

	private Long memberId;
	private BigDecimal accountBalance;
	private BigDecimal totalCharge;
	private BigDecimal totalBonus;
	private BigDecimal totalRent;
	
	private BigDecimal totalAmt;

	public Owner(){
		
	}
	/**
	 * initOwner;
	 * @param memberId
	 */
	public Owner(Long memberId){
		this.accountBalance=BigDecimal.ZERO;
		this.totalCharge=BigDecimal.ZERO;
		this.totalBonus=BigDecimal.ZERO;
		this.totalRent=BigDecimal.ZERO;
		this.totalAmt=BigDecimal.ZERO;
		this.memberId  = memberId;
	}
	
	
	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public BigDecimal getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}

	public BigDecimal getTotalCharge() {
		return totalCharge;
	}

	public void setTotalCharge(BigDecimal totalCharge) {
		this.totalCharge = totalCharge;
	}

	public BigDecimal getTotalBonus() {
		return totalBonus;
	}

	public void setTotalBonus(BigDecimal totalBonus) {
		this.totalBonus = totalBonus;
	}

	public BigDecimal getTotalRent() {
		return totalRent;
	}

	public void setTotalRent(BigDecimal totalRent) {
		this.totalRent = totalRent;
	}

	public BigDecimal getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}
	
}