/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity - 广告位
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_ad_position")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_ad_position_sequence")
@FilterDef(name = "tenantFilter", defaultCondition = "tenant = :tenantId", parameters = @ParamDef(name = "tenantId", type = "long"))
public class AdPosition extends BaseEntity {

	private static final long serialVersionUID = -7849848867030199578L;

	public static enum Type {
		/** 常规类型 */
		common,

		/** 推广类型 */
		spread
	}

	/** 广告位类型 */
	private Type type;

	/** 名称 */
	private String name;

	/** 宽度 */
	private Integer width;

	/** 高度 */
	private Integer height;

	/** 描述 */
	private String description;

	/** 模板 */
	private String template;

	/** 推广信息 */
	private String spreadmemo;

	/** 广告 */
	private Set<Ad> ads = new HashSet<Ad>();

	/** 参与商家 */
	private Set<AdPositionTenant> adPositionTenants = new HashSet<AdPositionTenant>();

	/**
	 * 获取广告位类型
	 * @return 广告位类型
	 */
	public Type getType() {
		return type;
	}

	/**
	 * 设置广告位类型
	 * @param type 广告位类型
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * 获取名称
	 * @return 名称
	 */
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * @param name 名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取宽度
	 * @return 宽度
	 */
	@NotNull
	@Min(1)
	@Column(nullable = false)
	public Integer getWidth() {
		return width;
	}

	/**
	 * 设置宽度
	 * @param width 宽度
	 */
	public void setWidth(Integer width) {
		this.width = width;
	}

	/**
	 * 获取高度
	 * @return 高度
	 */
	@NotNull
	@Min(1)
	@Column(nullable = false)
	public Integer getHeight() {
		return height;
	}

	/**
	 * 设置高度
	 * @param height 高度
	 */
	public void setHeight(Integer height) {
		this.height = height;
	}

	/**
	 * 获取描述
	 * @return 描述
	 */
	@Length(max = 200)
	public String getDescription() {
		return description;
	}

	/**
	 * 设置描述
	 * @param description 描述
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 推广描述
	 * @return 推广描述
	 */
	@Length(max = 200)
	public String getSpreadmemo() {
		return spreadmemo;
	}

	/**
	 * 设置推广描述
	 * @param spreadmemo 推广描述
	 */
	public void setSpreadmemo(String spreadmemo) {
		this.spreadmemo = spreadmemo;
	}

	/**
	 * 获取模板
	 * @return 模板
	 */
	@NotEmpty
	@Lob
	@Column(nullable = false)
	public String getTemplate() {
		return template;
	}

	/**
	 * 设置模板
	 * @param template 模板
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * 获取广告
	 * @return 广告
	 */
	@OneToMany(mappedBy = "adPosition", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	@Filters({ @Filter(name = "tenantFilter") })
	@OrderBy("order asc")
	public Set<Ad> getAds() {
		return ads;
	}

	/**
	 * 设置广告
	 * @param ads 广告
	 */
	public void setAds(Set<Ad> ads) {
		this.ads = ads;
	}

	/**
	 * 获取申请商家
	 * @return 申请商家
	 */
	@OneToMany(mappedBy = "adPosition", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<AdPositionTenant> getAdPositionTenants() {
		return adPositionTenants;
	}

	/**
	 * 设置申请商家
	 * @param adPositionTenants 申请商家
	 */
	public void setAdPositionTenants(Set<AdPositionTenant> adPositionTenants) {
		this.adPositionTenants = adPositionTenants;
	}

}