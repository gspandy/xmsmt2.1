/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSet.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
/**
 * Entity - 券发放表
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_share_set")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_share_set_sequence")
public class ShareSet extends BaseEntity{
	private static final long serialVersionUID = 1L;
	
	public enum Type{
		/**券分享标题 0*/
		ticketShareTitle,   
		/**邀请函分享标题 1*/
		invitationShareTitle,
		/** 分享券 2*/
		ticketShare,
		/**
		 * 好友邀请 3
		 */
		invitaionShare
	}

	private Tenant tenant;
	
	private String content;
	
	private Type type;
	
	

	/**
	 * 获取 tenant
	 * @return tenant
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
