/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PreRemove;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;

/**
 * Entity - 评论
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_review")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_review_sequence")
public class Review extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/** 访问路径前缀 */
	private static final String PATH_PREFIX = "/review/content";

	/** 访问路径后缀 */
	private static final String PATH_SUFFIX = ".jhtml";

	/** 对象 */
	public enum Flag {
		/** 商品评论 */
		product,
		/** 商家评论 */
		tenant,
		/** 订单评论 */
		trade
	}

	/** 类型 */
	public enum Type {
		/** 好评 */
		positive,
		/** 中评 */
		moderate,
		/** 差评 */
		negative
	}

	@Expose
	private Type type;

	/** 评分(商品评分/商家诚信服务评分/会员诚信评分) */
	@Expose
	private Integer score;

	/** 评分(商家配送评分) */
	@Expose
	private Integer assistant;

	/** 内容 */
	@Expose
	private String content;

	/** 是否显示 */
	private Boolean isShow;

	/** IP */
	private String ip;

	/** 对象 */
	private Flag flag;

	/** 会员 */
	@Expose
	private Member member;

	/** 商家 */
	private Tenant tenant;

	/** 商品 */
	private Product product;

	/** 订单买家评论子订单 */
	private Trade memberTrade;

	/** 订单卖家评论子订单 */
	private Trade tenantTrade;

	/** 订单项 */
	private OrderItem orderItem;

	/**
	 * 获取评分
	 * @return 评分
	 */
	@JsonProperty
	@NotNull
	@Min(1)
	@Max(5)
	@Column(nullable = false, updatable = false)
	public Integer getScore() {
		return score;
	}

	/**
	 * 好中差
	 * @return
	 */
	@JsonProperty
	@Column(updatable = false)
	public Type getType() {
		return type;
	}

	/**
	 * 设置评价类型(好评、中评、差评)
	 * @param type
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * 设置评分
	 * @param score 评分
	 */
	public void setScore(Integer score) {
		this.score = score;
	}

	@JsonProperty
	@Max(5)
	@Column(updatable = false)
	public Integer getAssistant() {
		return assistant;
	}

	/**
	 * 设置评分(商家配送评分)
	 * @param assistant
	 */
	public void setAssistant(Integer assistant) {
		this.assistant = assistant;
	}

	/**
	 * 获取内容
	 * @return 内容
	 */

	@JsonProperty
	@Length(max = 200)
	@Column(nullable = false, updatable = false)
	public String getContent() {
		return content;
	}

	/**
	 * 设置内容
	 * @param content 内容
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 获取是否显示
	 * @return 是否显示
	 */
	@Column(nullable = false)
	public Boolean getIsShow() {
		return isShow;
	}

	/**
	 * 设置是否显示
	 * @param isShow 是否显示
	 */
	public void setIsShow(Boolean isShow) {
		this.isShow = isShow;
	}

	/**
	 * 获取IP
	 * @return IP
	 */
	@Column(nullable = false, updatable = false)
	public String getIp() {
		return ip;
	}

	/**
	 * 设置IP
	 * @param ip IP
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * 获取会员
	 * @return 会员
	 */

	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false)
	public Member getMember() {
		return member;
	}

	/**
	 * 设置会员
	 * @param member 会员
	 */
	public void setMember(Member member) {
		this.member = member;
	}

	/**
	 * 获取对象
	 * @return 对象
	 */
	public Flag getFlag() {
		return flag;
	}

	public void setFlag(Flag flag) {
		this.flag = flag;
	}

	/**
	 * 获取商品
	 * @return 商品
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false)
	public Product getProduct() {
		return product;
	}

	/**
	 * 设置商品
	 * @param product 商品
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * 获取商家
	 * @return 商家
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	public Tenant getTenant() {
		return tenant;
	}

	/**
	 * 设置商家
	 * @param product 商家
	 */
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	/**
	 * 获取买家评价子订单
	 * @return 买家评价子订单
	 */
	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "member_trade", updatable = false)
	public Trade getMemberTrade() {
		return memberTrade;
	}

	/**
	 * 设置买家评价子订单
	 * @param memberTrade 买家评价子订单
	 */
	public void setMemberTrade(Trade memberTrade) {
		this.memberTrade = memberTrade;
	}

	/**
	 * 获取卖家评价子订单
	 * @return 卖家评价子订单
	 */
	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "tenant_trade", updatable = false)
	public Trade getTenantTrade() {
		return tenantTrade;
	}

	/**
	 * 设置卖家评价子订单
	 * @param tenantTrade 卖家评价子订单
	 */
	public void setTenantTrade(Trade tenantTrade) {
		this.tenantTrade = tenantTrade;
	}

	/**
	 * 获取订单项
	 * @return 订单项
	 */
	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "order_item", updatable = false)
	public OrderItem getOrderItem() {
		return orderItem;
	}

	/**
	 * 设置订单项
	 * @param tenantTrade 卖家订单项
	 */
	public void setOrderItem(OrderItem orderItem) {
		this.orderItem = orderItem;
	}

	/**
	 * 获取访问路径
	 * @return 访问路径
	 */
	@Transient
	public String getPath() {
		if (getProduct() != null && getProduct().getId() != null) {
			return PATH_PREFIX + "/" + getProduct().getId() + PATH_SUFFIX;
		}
		return null;
	}

	@PreRemove
	public void preRemove() {
		if (getOrderItem() != null) {
			getOrderItem().setReview(null);
		}
		if (getMemberTrade() != null) {
			getMemberTrade().setMemberReview(null);
		}
		if (getTenantTrade() != null) {
			getTenantTrade().setTenantReview(null);
		}
	}

}