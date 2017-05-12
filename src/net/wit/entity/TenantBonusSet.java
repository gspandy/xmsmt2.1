/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

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
@Table(name = "t_tenant_bonus_set")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_tenant_bonus_set_sequence")
public class TenantBonusSet extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;

	
	private Long tenantId;
	private Integer tenantSellBonusRate;
	private Integer relativeSellBonusRate;
	
	private String status;

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getTenantSellBonusRate() {
		return tenantSellBonusRate;
	}

	public void setTenantSellBonusRate(Integer tenantSellBonusRate) {
		this.tenantSellBonusRate = tenantSellBonusRate;
	}

	public Integer getRelativeSellBonusRate() {
		return relativeSellBonusRate;
	}

	public void setRelativeSellBonusRate(Integer relativeSellBonusRate) {
		this.relativeSellBonusRate = relativeSellBonusRate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}