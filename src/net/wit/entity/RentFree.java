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
import javax.persistence.Transient;

/**
 * Entity - 免租活动表
 * 
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_rent_free")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_rent_free_sequence")
public class RentFree extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

	private Long tenantId;// 企业ID
	private Date startMonth;// 起始月份
	private Date endMonth;// 维护时间
	private String tenantName;//企业名称

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Date getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(Date startMonth) {
		this.startMonth = startMonth;
	}

	public Date getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(Date endMonth) {
		this.endMonth = endMonth;
	}
	@Transient
	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	
}