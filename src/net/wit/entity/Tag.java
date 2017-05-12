/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.PreRemove;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity - 标签
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_tag")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_tag_sequence")
public class Tag extends OrderEntity {

	private static final long serialVersionUID = -2735037966597250149L;

	/**
	 * 类型
	 */
	public enum Type {

		/** 文章标签 */
		article,

		/** 商品标签 */
		product,

		/** 企业标签 */
		tenant,

		/** 社区标签 */
		community,

		/** 商品分类 */
		productCategory,

		/** 商盟分类 */
		tenantUnion
	};

	/** 名称 */
	private String name;

	/** 类型 */
	private Type type;

	/** 图标 */
	private String icon;

	/** 备注 */
	private String memo;

	/** 文章 */
	private Set<Article> articles = new HashSet<Article>();

	/** 商品 */
	private Set<Product> products = new HashSet<Product>();

	/** 企业 */
	private Set<Tenant> tenants = new HashSet<Tenant>();

	/** 企业 */
	private Set<Tenant> unionTenants = new HashSet<Tenant>();

	/** 社区 */
	private Set<Community> communitys = new HashSet<Community>();

	/** 商品分类 */
	private Set<ProductCategory> productCategorys = new HashSet<ProductCategory>();

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
	 * 获取类型
	 * @return 类型
	 */
	@NotNull(groups = Save.class)
	@Column(nullable = false, updatable = false)
	public Type getType() {
		return type;
	}

	/**
	 * 设置类型
	 * @param type 类型
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * 获取图标
	 * @return 图标
	 */
	@Length(max = 200)
	public String getIcon() {
		return icon;
	}

	/**
	 * 设置图标
	 * @param icon 图标
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * 获取备注
	 * @return 备注
	 */
	@Length(max = 200)
	public String getMemo() {
		return memo;
	}

	/**
	 * 设置备注
	 * @param memo 备注
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * 获取文章
	 * @return 文章
	 */
	@ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
	public Set<Article> getArticles() {
		return articles;
	}

	/**
	 * 设置文章
	 * @param articles 文章
	 */
	public void setArticles(Set<Article> articles) {
		this.articles = articles;
	}

	/**
	 * 获取商品
	 * @return 商品
	 */
	@ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
	public Set<Product> getProducts() {
		return products;
	}

	/**
	 * 设置商品
	 * @param products 商品
	 */
	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	/**
	 * 获取企业
	 * @return 企业
	 */
	@ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
	public Set<Tenant> getTenants() {
		return tenants;
	}

	/**
	 * 设置企业
	 * @param tenants 企业
	 */
	public void setTenants(Set<Tenant> tenants) {
		this.tenants = tenants;
	}

	/**
	 * 获取联盟企业
	 * @return 联盟企业
	 */
	@ManyToMany(mappedBy = "unionTags", fetch = FetchType.LAZY)
	public Set<Tenant> getUnionTenants() {
		return unionTenants;
	}

	/**
	 * 设置联盟企业
	 * @param unionTenants 联盟企业
	 */
	public void setUnionTenants(Set<Tenant> unionTenants) {
		this.unionTenants = unionTenants;
	}

	/**
	 * 获取社区
	 * @return 社区
	 */
	@ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
	public Set<Community> getCommunitys() {
		return communitys;
	}

	/**
	 * 设置社区
	 * @param communitys 社区
	 */
	public void setCommunitys(Set<Community> communitys) {
		this.communitys = communitys;
	}

	/**
	 * 获取商品分类
	 * @return 商品分类
	 */
	@ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
	public Set<ProductCategory> getProductCategorys() {
		return productCategorys;
	}

	/**
	 * 设置商品分类
	 * @param communitys 商品分类
	 */
	public void setProductCategorys(Set<ProductCategory> productCategorys) {
		this.productCategorys = productCategorys;
	}

	/**
	 * 删除前处理
	 */
	@PreRemove
	public void preRemove() {
		Set<Article> articles = getArticles();
		if (articles != null) {
			for (Article article : articles) {
				article.getTags().remove(this);
			}
		}
		Set<Product> products = getProducts();
		if (products != null) {
			for (Product product : products) {
				product.getTags().remove(this);
			}
		}
		Set<Tenant> tenants = getTenants();
		if (tenants != null) {
			for (Tenant tenant : tenants) {
				tenant.getTags().remove(this);
			}
		}
		Set<Community> communitys = getCommunitys();
		if (communitys != null) {
			for (Community community : communitys) {
				community.getTags().remove(this);
			}
		}
	}

}