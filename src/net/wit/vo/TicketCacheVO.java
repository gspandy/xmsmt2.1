/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSet.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.vo;

import java.math.BigInteger;
import java.util.Date;

/**
 * VO - 券发放表
 * @author rsico Team
 * @version 3.0
 */


public class TicketCacheVO {

	
	private BigInteger memberId;
	private	BigInteger tenantId;
	private BigInteger id;
	private Date createDate;
	private String model;
	private Integer ticketNum;
	private String name;
	private String mobile;
	
	public BigInteger getMemberId() {
		return memberId;
	}
	public void setMemberId(BigInteger memberId) {
		this.memberId = memberId;
	}
	public BigInteger getTenantId() {
		return tenantId;
	}
	public void setTenantId(BigInteger tenantId) {
		this.tenantId = tenantId;
	}
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public Integer getTicketNum() {
		return ticketNum;
	}
	public void setTicketNum(Integer ticketNum) {
		this.ticketNum = ticketNum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	













}
