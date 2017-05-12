/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSet.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.wit.util.DateUtil;
import net.wit.util.TicketUtil;
/**
 * Entity - 券发放表
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_ticket_cache")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_ticket_cache_sequence")
public class TicketCache extends BaseEntity{
	private static final long serialVersionUID = 1L;

	/**
	 * 系统发放 VIP月初定额发放
	 */
	public  static final String TICKETCACHE_SYSTEM_MODEL = "0";
	/**
	 * 新店主发放 新开通VIP
	 */
	public static final String TICKETCACHE_NEW_SHOPKEEPER_MODEL = "1";
	/**
	 * 申请发放
	 */
	public static final String TICKETCACHE_APPLY_MODEL="2";
	/**
	 * 定向发放
	 */
	public static final String TICKETCACHE_MENUAL_MODEL = "3";
	
	/**
	 * 未收取状态
	 */
	public static final String TICKETCACHE_NORECEIVESTATUS = "0";
	/**
	 * 已经领取
	 */
	public static final String TICKETCACHE_RECEIVEDSTATUS = "1";
	/**
	 * 已失效
	 */
	public static final String TICKETCACHE_EXPIREDSTATUS = "2";
	
	
	private Long memberId;
	private Long tenantId;
	private Integer num;
	private String model;
	private Date effectiveDate;
	private Date expiryDate;
	private String receiveStatus;
	private String remark;
	
  public TicketCache(){
	  super();
  }
	
	
	/**
	 * 根据内购券发放设置发放
	 * @param memberId
	 * @param ts
	 */
	public TicketCache(Long memberId,TicketSet ts) {
		super();
		this.memberId = memberId;
		this.tenantId = ts.getTenantId();
		this.num = ts.getSendNum();
		this.model = ts.getSendType();
		this.receiveStatus = TicketCache.TICKETCACHE_NORECEIVESTATUS;
		this.effectiveDate = new Date();
		this.expiryDate = DateUtil.getLastDateOfMonth();
	}
	
	/**
	 * 根据VIP等级发券
	 * @param memberId
	 * @param ts
	 */
	public TicketCache(Long memberId,VipLevel vipLevel,String sendModel) {
		super();
		this.memberId = memberId;
		this.tenantId = vipLevel.getTenant().getId();
		this.num = vipLevel.getTicketNum();
		this.model = sendModel;
		this.receiveStatus = TicketCache.TICKETCACHE_NORECEIVESTATUS;
		this.effectiveDate = new Date();
		this.expiryDate = DateUtil.getLastDateOfMonth();
	}
	
	/**
	 * 人工发放模式
	 * @param memberId
	 * @param tenantId
	 * @param num
	 */
	public TicketCache(Long memberId, Long tenantId, int num) {
		super();
		this.memberId = memberId;
		this.tenantId = tenantId;
		this.num = num;
		this.model =TicketCache.TICKETCACHE_MENUAL_MODEL;
		this.receiveStatus = TicketCache.TICKETCACHE_NORECEIVESTATUS;
		this.effectiveDate = new Date();
		this.expiryDate = DateUtil.getLastDateOfMonth();
	}
	
	
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public Long getTenantId() {
		return tenantId;
	}
	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getReceiveStatus() {
		return receiveStatus;
	}
	public void setReceiveStatus(String receiveStatus) {
		this.receiveStatus = receiveStatus;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	












}
