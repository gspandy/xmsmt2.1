/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity - 广告内容
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_ad")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_ad_sequence")
public class Ad extends OrderEntity {

	private static final long serialVersionUID = -1307743303786909390L;

	/**
	 * 类型
	 */
	public enum Type {

		/** 文本 */
		text,

		/** 图片 */
		image,

		/** flash */
		flash
	}

	/** 标题 */
	private String title;

	/** 类型 */
	private Type type;

	/** 内容 */
	private String content;

	/** 路径 */
	private String path;

	/** 开始时间 */
	private Date beginDate;

	/** 结束时间 */
	private Date endDate;

	/** 连接地址 */
	private String url;

	/** 广告位 */
	private AdPosition adPosition;

	/** 所属企业 */
	private Tenant tenant;

	/** 频道信息 */
	private Set<ProductChannel> productChannels = new HashSet<ProductChannel>();

	/**
	 * 标题
	 * @return 标题
	 */
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getTitle() {
		return title;
	}

	/**
	 * 标题
	 * @param title 标题
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 类型
	 * @return 类型
	 */
	@NotNull
	@Column(nullable = false)
	public Type getType() {
		return type;
	}

	/**
	 * 类型
	 * @param type 类型
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * 内容
	 * @return 内容
	 */
	@Lob
	public String getContent() {
		return content;
	}

	/**
	 * 内容
	 * @param content 内容
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 路径
	 * @return 路径
	 */
	@Length(max = 200)
	public String getPath() {
		return path;
	}

	/**
	 * 路径
	 * @param path 路径
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 开始时间
	 * @return 开始时间
	 */
	public Date getBeginDate() {
		return beginDate;
	}

	/**
	 * 开始时间
	 * @param beginDate 开始时间
	 */
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	/**
	 * 结束时间
	 * @return 结束时间
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * 结束时间
	 * @param endDate 结束时间
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * 连接地址
	 * @return 连接地址
	 */
	@Length(max = 200)
	public String getUrl() {
		return url;
	}

	/**
	 * 连接地址
	 * @param url 连接地址
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 广告位
	 * @return 广告位
	 */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public AdPosition getAdPosition() {
		return adPosition;
	}

	/**
	 * 广告位
	 * @param adPosition 广告位
	 */
	public void setAdPosition(AdPosition adPosition) {
		this.adPosition = adPosition;
	}

	/**
	 * 是否开始
	 * @return 是否开始
	 */
	@Transient
	public boolean hasBegun() {
		return getBeginDate() == null || new Date().after(getBeginDate());
	}

	/**
	 * 是否已经结束
	 * @return 是否已经结束
	 */
	@Transient
	public boolean hasEnded() {
		return getEndDate() != null && new Date().after(getEndDate());
	}

	/**
	 * 所属企业
	 * @return 企业
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public Tenant getTenant() {
		return tenant;
	}

	/**
	 * 设置企业
	 * @param member 企业
	 */
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_ad_product_channel")
	public Set<ProductChannel> getProductChannels() {
		return productChannels;
	}

	public void setProductChannels(Set<ProductChannel> productChannels) {
		this.productChannels = productChannels;
	}

}