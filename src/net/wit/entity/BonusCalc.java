/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.math.BigDecimal;
import java.util.Date;

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
@Table(name = "t_bonus_calc")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_bonus_calc_sequence")
public class BonusCalc extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

//	private Long memberId;
//	private Long tenantId;
//	private Long beRecommendId;
	private Member member;
	private Member beRecommend;
	private Tenant tenant;
	private BigDecimal bonus;
	private Date bonusTime;
	private Type type;
	private Integer bonusPercent;
	private String vipLevelName;
	
	private Status status;
	
	private Long chargeId;
	
	public enum Type {
		/** 本店销售奖金*/
		owner, /** 关联点销售奖金*/
		relateive

	}
	
	public enum Status {
		/** 未领取*/
		notReceive , /** 审核中*/
		receiving,/** 已领取*/
		received,/** 退回*/
		returned,
		/**未结算*/
		notSettle
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
	public BigDecimal getBonus() {
		return bonus;
	}
	public void setBonus(BigDecimal bonus) {
		this.bonus = bonus;
	}
	public Date getBonusTime() {
		return bonusTime;
	}
	public void setBonusTime(Date bonusTime) {
		this.bonusTime = bonusTime;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Long getChargeId() {
		return chargeId;
	}
	public void setChargeId(Long chargeId) {
		this.chargeId = chargeId;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "be_recommend_id")
	public Member getBeRecommend() {
		return beRecommend;
	}
	public void setBeRecommend(Member beRecommend) {
		this.beRecommend = beRecommend;
	}
	public Integer getBonusPercent() {
		return bonusPercent;
	}
	public void setBonusPercent(Integer bonusPercent) {
		this.bonusPercent = bonusPercent;
	}
	public String getVipLevelName() {
		return vipLevelName;
	}
	public void setVipLevelName(String vipLevelName) {
		this.vipLevelName = vipLevelName;
	}

}