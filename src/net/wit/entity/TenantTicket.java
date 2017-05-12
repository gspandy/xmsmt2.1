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
/**
 * Entity - 券发放表
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_tenant_ticket")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_tenant_ticket_sequence")
public class TenantTicket extends BaseEntity{
	private static final long serialVersionUID = 1L;

	
	private Long tanentId;
	private String effectiveImage;
	private String expiryImage;
	private String tailImage;
	private String tailExpiredImage;
	private String content;
	private String remark;

	
	public Long getTanentId() {
		return tanentId;
	}
	public void setTanentId(Long tanentId) {
		this.tanentId = tanentId;
	}
	public String getEffectiveImage() {
		return effectiveImage;
	}
	public void setEffectiveImage(String effectiveImage) {
		this.effectiveImage = effectiveImage;
	}
	public String getExpiryImage() {
		return expiryImage;
	}
	public void setExpiryImage(String expiryImage) {
		this.expiryImage = expiryImage;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getTailImage() {
		return tailImage;
	}
	public void setTailImage(String tailImage) {
		this.tailImage = tailImage;
	}
	public String getTailExpiredImage() {
		return tailExpiredImage;
	}
	public void setTailExpiredImage(String tailExpiredImage) {
		this.tailExpiredImage = tailExpiredImage;
	}

}
