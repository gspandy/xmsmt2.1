/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity - 角色
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_role")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_role_sequence")
public class Role extends BaseEntity {

	private static final long serialVersionUID = -6614052029623997372L;

	/** 名称 */
	private String name;

	/** 是否内置 */
	private Boolean isSuper;

	/** 描述 */
	private String description;

	/** 所属企业ID */
	private Tenant tenant;

	/** 权限 */
	private List<Authority> authorities = new ArrayList<Authority>();

	/** 管理员 */
	private Set<Admin> admins = new HashSet<Admin>();

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
	 * 获取 authorities
	 * @return authorities
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_role_authority")
	public List<Authority> getAuthorities() {
		return authorities;
	}

	/**
	 * 设置 authorities
	 * @param authorities authorities
	 */
	public void setAuthorities(List<Authority> authorities) {
		this.authorities = authorities;
	}

	/**
	 * 获取管理员
	 * @return 管理员
	 */
	@ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
	public Set<Admin> getAdmins() {
		return admins;
	}

	/**
	 * 设置管理员
	 * @param admins 管理员
	 */
	public void setAdmins(Set<Admin> admins) {
		this.admins = admins;
	}

	/**
	 * 获取 tenant
	 * @return tenant
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	public Tenant getTenant() {
		return tenant;
	}

	/**
	 * 设置 tenant
	 * @param tenant tenant
	 */
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	/**
	 * 获取 isSuper
	 * @return isSuper
	 */
	@Column(name = "is_super")
	public Boolean getIsSuper() {
		if (isSuper != null) {
			return isSuper;
		}
		return false;
	}

	/**
	 * 设置 isSuper
	 * @param isSuper isSuper
	 */
	public void setIsSuper(Boolean isSuper) {
		this.isSuper = isSuper;
	}
	

}