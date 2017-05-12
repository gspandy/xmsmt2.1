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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PreRemove;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity - 商家商品分类
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_product_category_tenant")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_product_category_tenant_sequence")
public class ProductCategoryTenant extends OrderEntity {

	private static final long serialVersionUID = 5095521437302782717L;

	/** 树路径分隔符 */
	public static final String TREE_PATH_SEPARATOR = ",";

	/** 访问路径前缀 */
	private static final String PATH_PREFIX = "/product/tenant/list";

	/** 访问路径后缀 */
	private static final String PATH_SUFFIX = ".jhtml";

	/** 名称 */
	private String name;

	/** 图标 */
	private String image;

	/** 页面标题 */
	private String seoTitle;

	/** 页面关键词 */
	private String seoKeywords;

	/** 页面描述 */
	private String seoDescription;

	/** 树路径 */
	private String treePath;

	/** 层级 */
	private Integer grade;

	/** 上级分类 */
	private ProductCategoryTenant parent;

	/** 下级分类 */
	private Set<ProductCategoryTenant> children = new HashSet<ProductCategoryTenant>();

	/** 所属商家 */
	private Tenant tenant;

	private List<Product> products = new ArrayList<Product>();

	/**
	 * 获取名称
	 * @return 名称
	 */
	@JsonProperty
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

	@JsonProperty
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * 获取页面标题
	 * @return 页面标题
	 */
	@Length(max = 200)
	public String getSeoTitle() {
		return seoTitle;
	}

	/**
	 * 设置页面标题
	 * @param seoTitle 页面标题
	 */
	public void setSeoTitle(String seoTitle) {
		this.seoTitle = seoTitle;
	}

	/**
	 * 获取页面关键词
	 * @return 页面关键词
	 */
	@Length(max = 200)
	public String getSeoKeywords() {
		return seoKeywords;
	}

	/**
	 * 设置页面关键词
	 * @param seoKeywords 页面关键词
	 */
	public void setSeoKeywords(String seoKeywords) {
		this.seoKeywords = seoKeywords;
	}

	/**
	 * 获取页面描述
	 * @return 页面描述
	 */
	@Length(max = 200)
	public String getSeoDescription() {
		return seoDescription;
	}

	/**
	 * 设置页面描述
	 * @param seoDescription 页面描述
	 */
	public void setSeoDescription(String seoDescription) {
		this.seoDescription = seoDescription;
	}

	/**
	 * 获取树路径
	 * @return 树路径
	 */
	@Column(nullable = false)
	public String getTreePath() {
		return treePath;
	}

	/**
	 * 设置树路径
	 * @param treePath 树路径
	 */
	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}

	/**
	 * 获取层级
	 * @return 层级
	 */
	@Column(nullable = false)
	public Integer getGrade() {
		return grade;
	}

	/**
	 * 设置层级
	 * @param grade 层级
	 */
	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	/**
	 * 获取上级分类
	 * @return 上级分类
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public ProductCategoryTenant getParent() {
		return parent;
	}

	/**
	 * 设置上级分类
	 * @param parent 上级分类
	 */
	public void setParent(ProductCategoryTenant parent) {
		this.parent = parent;
	}

	/**
	 * 获取下级分类
	 * @return 下级分类
	 */
	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
	@OrderBy("order asc")
	public Set<ProductCategoryTenant> getChildren() {
		return children;
	}

	/**
	 * 设置下级分类
	 * @param children 下级分类
	 */
	public void setChildren(Set<ProductCategoryTenant> children) {
		this.children = children;
	}

	/**
	 * 获取树路径
	 * @return 树路径
	 */
	@Transient
	public List<Long> getTreePaths() {
		List<Long> treePaths = new ArrayList<Long>();
		String[] ids = StringUtils.split(getTreePath(), TREE_PATH_SEPARATOR);
		if (ids != null) {
			for (String id : ids) {
				treePaths.add(Long.valueOf(id));
			}
		}
		return treePaths;
	}

	/**
	 * 获取访问路径
	 * @return 访问路径
	 */
	@Transient
	public String getPath() {
		if (getId() != null) {
			return PATH_PREFIX + "/" + getId() + PATH_SUFFIX;
		}
		return null;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Tenant getTenant() {
		return tenant;
	}

	/**
	 * 获取 products
	 * @return products
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productCategoryTenant")
	public List<Product> getProducts() {
		return products;
	}

	/**
	 * 设置 products
	 * @param products products
	 */
	public void setProducts(List<Product> products) {
		this.products = products;
	}

	@PreRemove
	public void preRemove() {
		if (getProducts().size() > 0) {
			for (Product p : getProducts()) {
				p.setProductCategoryTenant(null);
			}
		}
	}

}