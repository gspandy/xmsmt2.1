/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.math.BigDecimal;
import java.util.Date;

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
@Table(name = "t_owner_cash_detail")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_owner_cash_detail_sequence")
public class OwnerCashDetail extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

	private BigDecimal cashAmt;
	private Date cashDate;
	private Long memberBankId;
	private String status;
	private Long memberId;
	
	private String cashrequestid;

	public BigDecimal getCashAmt() {
		return cashAmt;
	}

	public void setCashAmt(BigDecimal cashAmt) {
		this.cashAmt = cashAmt;
	}

	public Date getCashDate() {
		return cashDate;
	}

	public void setCashDate(Date cashDate) {
		this.cashDate = cashDate;
	}

	public Long getMemberBankId() {
		return memberBankId;
	}

	public void setMemberBankId(Long memberBankId) {
		this.memberBankId = memberBankId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCashrequestid() {
		return cashrequestid;
	}

	public void setCashrequestid(String cashrequestid) {
		this.cashrequestid = cashrequestid;
	}
    
}