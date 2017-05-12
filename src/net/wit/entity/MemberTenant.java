/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

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
@Table(name = "t_member_tenant")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_member_tenant_sequence")
public class MemberTenant extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

	private Long memberId;
	private Long tenantId;
	private Date relativeDate;
	private String phone;

	public MemberTenant(){
		
	}
	public MemberTenant(Long memberId,Long tenantId,String phone){
		this.memberId = memberId;
		this.tenantId = tenantId;
		this.phone = phone;
		this.relativeDate = new Date();
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

	public Date getRelativeDate() {
		return relativeDate;
	}

	public void setRelativeDate(Date relativeDate) {
		this.relativeDate = relativeDate;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	
}