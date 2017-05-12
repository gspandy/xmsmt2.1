/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity - 广告内容
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "t_message_details")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_message_details_sequence")
public class MessageDetails extends BaseEntity {

	private static final long serialVersionUID = -1307743303786909390L;

	public enum MessageType{
		/**
		 * 交易消息类型
		 */
		order,
		/**
		 * 建议消息类型
		 */
		suggestion,
		/**
		 * 系统消息类型
		 */
		system
	}
	
	private MessageType type;
	
	/**
	 * false 平台消息
	 * true 企业消息
	 */
	private boolean level;  // 0 平台, 1为企业
	/**
	 * 关联企业
	 */
	private Tenant tenant;
	/**
	 * 消息内容
	 */
	private String messageContent;  
	/**
	 * 如为交易消息
	 */
	private Order order;
	/**
	 * 消息处理状态
	 * true 已读
	 * false 未读
	 */
	private boolean messageStat;

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public boolean getLevel() {
		return level;
	}

	public void setLevel(boolean level) {
		this.level = level;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tenant_id")
	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public boolean getMessageStat() {
		return messageStat;
	}

	public void setMessageStat(boolean messageStat) {
		this.messageStat = messageStat;
	}

}