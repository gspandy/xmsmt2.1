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
@Table(name = "t_owner_income")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_owner_income_sequence")
public class OwnerIncome extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

	private Long memberId;
	private String incomeType;
	private Date incomeDate;
	private BigDecimal incomeAmt;
	private String content;
	private Long orderId;
	private Long beRecommendedId;
	
//	private String confirmFlag;

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getIncomeType() {
		return incomeType;
	}

	public void setIncomeType(String incomeType) {
		this.incomeType = incomeType;
	}

	public Date getIncomeDate() {
		return incomeDate;
	}

	public void setIncomeDate(Date incomeDate) {
		this.incomeDate = incomeDate;
	}

	public BigDecimal getIncomeAmt() {
		return incomeAmt;
	}

	public void setIncomeAmt(BigDecimal incomeAmt) {
		this.incomeAmt = incomeAmt;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getBeRecommendedId() {
		return beRecommendedId;
	}

	public void setBeRecommendedId(Long beRecommendedId) {
		this.beRecommendedId = beRecommendedId;
	}


/*	public String getConfirmFlag() {
		return confirmFlag;
	}

	public void setConfirmFlag(String confirmFlag) {
		this.confirmFlag = confirmFlag;
	}*/

	

}