/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity - 社区
 * 
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_community")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_community_sequence")
public class Community extends BaseEntity {

	private static final long serialVersionUID = 1472373494571485132L;

	/**
	 * 状态
	 */
	public enum Status {

		/** 申请 */
		wait,

		/** 开通 */
		open,

		/** 关闭 */
		close
	}

	/** 编码 */
	private String code;
	
	/** 名称 */
	private String name;

	/** 描述*/
	private String descr;

	
	/** 地区 */
	private Area area;
	
	/** 状态 */
	private Status status ;
	
	/** 展示图片 */
	private String image;
	
	/** 标签 */
	private Set<Tag> tags = new HashSet<Tag>();
	
	/** 经纬度 */
	private Location location;
	
	/**
	 * 编码
	 * 
	 * @return 编码
	 */
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 名称
	 * 
	 * @return 名称
	 */
	@Length(max = 255)
	@JsonProperty
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 *            名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取页面描述
	 * 
	 * @return 页面描述
	 */
	@Lob
	public String getDescr() {
		return descr;
	}

	/**
	 * 设置页面描述
	 * 
	 * @param Descr
	 *            页面描述
	 */
	public void setDescr(String descr) {
		this.descr = descr;
	}
	
	/**
	 * 状态
	 * 
	 * @return 状态
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * 设置状态
	 * 
	 * @param status
	 *            状态
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * 获取地区
	 * 
	 * @return 地区
	 */
	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	public Area getArea() {
		return area;
	}

	/**
	 * 设置地区
	 * 
	 * @param area
	 *            地区
	 */
	public void setArea(Area area) {
		this.area = area;
	}
	
	/**
	 * 获取展示图片
	 * 
	 * @return 展示图片
	 */
	@Length(max = 200)
	public String getImage() {
		return image;
	}

	/**
	 * 设置展示图片
	 * 
	 * @param image
	 *            展示图片
	 */
	public void setImage(String image) {
		this.image = image;
	}


	/**
	 * 获取标签
	 * 
	 * @return 标签
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_community_tag")
	@OrderBy("order asc")
	public Set<Tag> getTags() {
		return tags;
	}

	/**
	 * 设置标签
	 * 
	 * @param tags
	 *            标签
	 */
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
	
	/**
	 * 获取经纬度
	 * 
	 * @return 经纬度
	 */
	@Embedded
	public Location getLocation() {
		return location;
	}

	/**
	 * 设置经纬度
	 * 
	 * @param location
	 *            经纬度
	 */
	public void setLocation(Location location) {
		this.location = location;
	}
}