/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantSellCondition.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月8日
 */
package net.wit.entity;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 企业VIP门槛设置表
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月8日
 */
@Entity
@Table(name = "t_tenant_sell_condition")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_tenant_sell_condition_sequence")
public class TenantSellCondition extends BaseEntity{
	private static final long serialVersionUID = 1L;
	private Long tenantId;
	private Integer tradeNum;
	private String  applyEnabled;
	private String  remark;
	
	public Long getTenantId() {
		return tenantId;
	}
	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}
	public Integer getTradeNum() {
		return tradeNum;
	}
	public void setTradeNum(Integer tradeNum) {
		this.tradeNum = tradeNum;
	}
	public String getApplyEnabled() {
		return applyEnabled;
	}
	public void setApplyEnabled(String applyEnabled) {
		this.applyEnabled = applyEnabled;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
