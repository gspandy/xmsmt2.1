/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSet.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity - 券发放表
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_ticket_apply")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_ticket_apply_sequence")
public class TicketApply extends BaseEntity {
	private static final long serialVersionUID = 1L;

	public enum ApplyStatus{
		/**
		 * 申请中
		 */
		apply,
		/**
		 * 同意发放
		 */
		confirmed,
		/**
		 * 拒绝
		 */
		rejected
	}
	
	
	public enum ApplyType{
		/**
		 * 店长申请(0)
		 */
		shopkeeperApply,
		/**
		 *会员向店长申请
		 */
		memberApplyToShopKeeper,
		/**
		 * 会员向企业申请(2)
		 */
		memberApplyToTenant
	}
	
	private Member owner;
	private Member member;
	private Tenant tenant;
	
	/**
	 * false 店主申请，
	 * true 买家申请
	 */
	private ApplyType applyType;
	private ApplyStatus applyStatus;
	
	@ManyToOne(fetch = FetchType.LAZY)
	public Member getOwner() {
		return owner;
	}
	public void setOwner(Member owner) {
		this.owner = owner;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
	public ApplyType getApplyType() {
		return applyType;
	}
	public void setApplyType(ApplyType applyType) {
		this.applyType = applyType;
	}
	public ApplyStatus getApplyStatus() {
		return applyStatus;
	}
	public void setApplyStatus(ApplyStatus applyStatus) {
		this.applyStatus = applyStatus;
	}

}
