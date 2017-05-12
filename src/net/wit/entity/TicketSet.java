/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSet.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.entity;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
/**
 * Entity - 券发放表
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_tenant_ticket_set")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_tenant_ticket_set_sequence")
public class TicketSet extends BaseEntity{
	private static final long serialVersionUID = 1L;
	private Long tenantId;
	private Integer sendNum;
	private String useFlag;
	private String sendType;
	private String remark;
	
	public static final String FLAG_USE = "1";
	
	public static final String FLAG_NO_USE = "0";
	
	public static final String SYSTEM_SEND_TYPE = "0";
	
	public static final String NEW_SEND_TYPE = "1";
	
	public static final String APPLY_SEND_TYPE = "2";
	
	public Long getTenantId() {
		return tenantId;
	}
	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}
	public Integer getSendNum() {
		return sendNum;
	}
	public void setSendNum(Integer sendNum) {
		this.sendNum = sendNum;
	}
	public String getUseFlag() {
		return useFlag;
	}
	public void setUseFlag(String useFlag) {
		this.useFlag = useFlag;
	}
	public String getSendType() {
		return sendType;
	}
	public void setSendType(String sendType) {
		this.sendType = sendType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}


}
