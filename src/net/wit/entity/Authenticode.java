/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
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

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity - 订单项
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_authenticode")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_authenticode_sequence")
public class Authenticode extends BaseEntity {

	private static final long serialVersionUID = -4999926022604479334L;

	/**
	 * 状态
	 */
	public enum Status {

		/** 待发货 */
		unshipped,

		/** 已发货 */
		shipped,

		/** 已退货 */
		returned,

		/** 已受理 */
		accepted,

		/** 已安装 */
		install,

		/** 已验收 */
		finish,

		/** 已作废 */
		cancel
	}

	/** 验证码 */
	private String sn;

	/** 协议安装时间 */
	private Date installDate;

	/** 设备号 */
	private String mac;

	/** 商品 */
	private OrderItem orderItem;

	/** 服务商家 */
	private Tenant tenant;

	/** 状态 */
	private Status status;

	private Member member;

	/**
	 * 获取验证码
	 * @return 验证码
	 */
	@JsonProperty
	@NotEmpty
	@Column(nullable = false, updatable = false)
	public String getSn() {
		return sn;
	}

	/**
	 * 设置验证码
	 * @param sn 验证码
	 */
	public void setSn(String sn) {
		this.sn = sn;
	}

	@Column(nullable = true)
	public Date getInstallDate() {
		return installDate;
	}

	public void setInstallDate(Date installDate) {
		this.installDate = installDate;
	}

	/**
	 * 获取订单明细
	 * @return 订单明细
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orderItem", nullable = false, updatable = false)
	public OrderItem getOrderItem() {
		return orderItem;
	}

	/**
	 * 设置订单明细
	 * @param orderItem 订单明细
	 */
	public void setOrderItem(OrderItem orderItem) {
		this.orderItem = orderItem;
	}

	@JsonProperty
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * 获取企业
	 * @return 所属企业
	 */
	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	public Tenant getTenant() {
		return tenant;
	}

	/**
	 * 设置企业
	 * @param tenant 所属企业
	 */
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	/**
	 * 获取设备号
	 * @return 所属设备号
	 */
	@JsonProperty
	public String getMac() {
		return mac;
	}

	/**
	 * 设置设备号
	 * @param mac 所属设备号
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Member getMember() {
		return member;
	}

}