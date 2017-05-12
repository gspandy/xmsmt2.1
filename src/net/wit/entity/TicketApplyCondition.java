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
@Table(name = "t_ticket_apply_condition")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_ticket_apply_condition_sequence")
public class TicketApplyCondition extends BaseEntity {
	private static final long serialVersionUID = 1L;

	
	
	private Integer ticketUsedTimes;
	private Integer invations;
	private Tenant tenant;
	private Integer autoRejectDays;

	
	@ManyToOne(fetch = FetchType.LAZY)
	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
	public Integer getTicketUsedTimes() {
		return ticketUsedTimes;
	}
	public void setTicketUsedTimes(Integer ticketUsedTimes) {
		this.ticketUsedTimes = ticketUsedTimes;
	}
	public Integer getInvations() {
		return invations;
	}
	public void setInvations(Integer invations) {
		this.invations = invations;
	}
	public Integer getAutoRejectDays() {
		return autoRejectDays;
	}
	public void setAutoRejectDays(Integer autoRejectDays) {
		this.autoRejectDays = autoRejectDays;
	}

}
