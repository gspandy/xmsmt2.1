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

import net.wit.util.DateUtil;

/**
 * Entity - 券发放表
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_ticket")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_ticket_sequence")
public class Ticket extends BaseEntity {
	private static final long serialVersionUID = 1L;

	public enum Status {
		/** 未领取（卖家已领取 */
		nouse, /** 买家领取 */
		recevied, /** 已使用 */
		used, /** 已失效 */
		expired

	}

	private Member member;

	private Member shopkeeper;  //店主  /VIP

	private Tenant tenant;

	private Date expiryDate;

	private Status status;

	private String remark;

	public Ticket() {
	}

	public Ticket(Member shopkeeper, Tenant tenant, Date expiryDate) {
		super();
		this.shopkeeper = shopkeeper;
		this.tenant = tenant;
		this.expiryDate = expiryDate;
		this.status = Status.nouse;
	}
	

	public Ticket(Member shopkeeper, Member member,Tenant tenant,Status status) {
		super();
		this.shopkeeper = shopkeeper;
		this.member = member;
		this.tenant = tenant;
		this.expiryDate = DateUtil.getLastDateOfMonth();;
		this.status =status;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	/**
	 * 获取 shopkeeper
	 * @return shopkeeper
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shopkeeper_id")
	public Member getShopkeeper() {
		return shopkeeper;
	}

	/**
	 * 设置 shopkeeper
	 * @param shopkeeper shopkeeper
	 */
	public void setShopkeeper(Member shopkeeper) {
		this.shopkeeper = shopkeeper;
	}

	/**
	 * 获取 tanent
	 * @return tanent
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tenant_id")
	public Tenant getTenant() {
		return tenant;
	}

	/**
	 * 设置 tanent
	 * @param tanent tanent
	 */
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	/**
	 * 获取 status
	 * @return status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * 设置 status
	 * @param status status
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

}
