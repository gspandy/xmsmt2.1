/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import net.wit.Setting;
import net.wit.dao.ProductCategoryTenantDao;
import net.wit.entity.Attribute;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Product;
import net.wit.entity.ProductCategory;
import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.util.SettingUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Dao - 商家商品分类
 * @author rsico Team
 * @version 3.0
 */
@Repository("productCategoryTenantDaoImpl")
public class ProductCategoryTenantDaoImpl extends BaseDaoImpl<ProductCategoryTenant, Long>implements ProductCategoryTenantDao {

	public List<ProductCategoryTenant> findRoots(Integer count, Tenant tenant) {
		if (tenant == null) {
			return new ArrayList<ProductCategoryTenant>();
		}
		String jpql = "select ProductCategoryTenant from ProductCategoryTenant ProductCategoryTenant where ProductCategoryTenant.parent is null and ProductCategoryTenant.tenant=" + tenant.getId() + " order by ProductCategoryTenant.order asc";
		TypedQuery<ProductCategoryTenant> query = entityManager.createQuery(jpql, ProductCategoryTenant.class).setFlushMode(FlushModeType.COMMIT);
		if (count != null) {
			query.setMaxResults(count);
		}
		return query.getResultList();
	}

	public List<ProductCategoryTenant> findParents(ProductCategoryTenant ProductCategoryTenant, Integer count, Tenant tenant) {
		if (tenant == null) {
			return Collections.<ProductCategoryTenant> emptyList();
		}
		if (ProductCategoryTenant == null || ProductCategoryTenant.getParent() == null) {
			return Collections.<ProductCategoryTenant> emptyList();
		}
		String jpql = "select ProductCategoryTenant from ProductCategoryTenant ProductCategoryTenant where ProductCategoryTenant.id in (:ids) and ProductCategoryTenant.tenant=" + tenant.getId() + " order by ProductCategoryTenant.grade asc";
		TypedQuery<ProductCategoryTenant> query = entityManager.createQuery(jpql, ProductCategoryTenant.class).setFlushMode(FlushModeType.COMMIT).setParameter("ids", ProductCategoryTenant.getTreePaths());
		if (count != null) {
			query.setMaxResults(count);
		}
		return query.getResultList();
	}

	public List<ProductCategoryTenant> findChildren(ProductCategoryTenant ProductCategoryTenant, Integer count, Tenant tenant) {
		if (tenant == null) {
			return new ArrayList<ProductCategoryTenant>();
		}
		TypedQuery<ProductCategoryTenant> query;
		if (ProductCategoryTenant != null) {
			String jpql = "select ProductCategoryTenant from ProductCategoryTenant ProductCategoryTenant where ProductCategoryTenant.treePath like :treePath and grade=:grade and ProductCategoryTenant.tenant=" + tenant.getId()
					+ " order by ProductCategoryTenant.order asc";
			query = entityManager.createQuery(jpql, ProductCategoryTenant.class).setFlushMode(FlushModeType.COMMIT)
					.setParameter("treePath", "%" + ProductCategoryTenant.TREE_PATH_SEPARATOR + ProductCategoryTenant.getId() + ProductCategoryTenant.TREE_PATH_SEPARATOR + "%").setParameter("grade", ProductCategoryTenant.getGrade() + 1);
		} else {
			String jpql = "select ProductCategoryTenant from ProductCategoryTenant ProductCategoryTenant where ProductCategoryTenant.tenant=" + tenant.getId() + "  order by ProductCategoryTenant.order asc";
			query = entityManager.createQuery(jpql, ProductCategoryTenant.class).setFlushMode(FlushModeType.COMMIT);
		}
		if (count != null) {
			query.setMaxResults(count);
		}
		return sort(query.getResultList(), ProductCategoryTenant);
	}

	public List<ProductCategoryTenant> findRoots(Tag tag, Integer count, Tenant tenant) {
		if (tenant == null) {
			return new ArrayList<ProductCategoryTenant>();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProductCategoryTenant> criteriaQuery = criteriaBuilder.createQuery(ProductCategoryTenant.class);
		Root<ProductCategoryTenant> root = criteriaQuery.from(ProductCategoryTenant.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Tenant> get("tenant"), tenant));
		if (tag != null) {
			Subquery<ProductCategoryTenant> subquery = criteriaQuery.subquery(ProductCategoryTenant.class);
			Root<ProductCategoryTenant> subqueryRoot = subquery.from(ProductCategoryTenant.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tag));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		criteriaQuery.where(restrictions);
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("order")));
		return super.findList(criteriaQuery, null, count, null, null);
	}

	/**
	 * 设置treePath、grade并保存
	 * @param ProductCategoryTenant 商品分类
	 */
	@Override
	public void persist(ProductCategoryTenant ProductCategoryTenant) {
		Assert.notNull(ProductCategoryTenant);
		setValue(ProductCategoryTenant);
		super.persist(ProductCategoryTenant);
	}

	/**
	 * 设置treePath、grade并更新
	 * @param ProductCategoryTenant 商品分类
	 * @return 商品分类
	 */
	@Override
	public ProductCategoryTenant merge(ProductCategoryTenant ProductCategoryTenant) {
		Assert.notNull(ProductCategoryTenant);
		setValue(ProductCategoryTenant);
		for (ProductCategoryTenant category : findChildren(ProductCategoryTenant, null, ProductCategoryTenant.getTenant())) {
			setValue(category);
		}
		return super.merge(ProductCategoryTenant);
	}

	// /**
	// * 清除商品属性值并删除
	// *
	// * @param ProductCategoryTenant
	// * 商品分类
	// */
	// @Override
	// public void remove(ProductCategoryTenant ProductCategoryTenant) {
	// if (ProductCategoryTenant != null) {
	// StringBuffer jpql = new StringBuffer("update Product product set ");
	// for (int i = 0; i < Product.ATTRIBUTE_VALUE_PROPERTY_COUNT; i++) {
	// String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + i;
	// if (i == 0) {
	// jpql.append("product." + propertyName + " = null");
	// } else {
	// jpql.append(", product." + propertyName + " = null");
	// }
	// }
	// jpql.append(" where product.productCategoryTenants = :ProductCategoryTenant");
	// entityManager.createQuery(jpql.toString()).setFlushMode(FlushModeType.COMMIT).setParameter("ProductCategoryTenant",
	// ProductCategoryTenant).executeUpdate();
	// super.remove(ProductCategoryTenant);
	// }
	// }

	/**
	 * 排序商品分类
	 * @param productCategories 商品分类
	 * @param parent 上级商品分类
	 * @return 商品分类
	 */
	private List<ProductCategoryTenant> sort(List<ProductCategoryTenant> productCategories, ProductCategoryTenant parent) {
		List<ProductCategoryTenant> result = new ArrayList<ProductCategoryTenant>();
		if (productCategories != null) {
			for (ProductCategoryTenant ProductCategoryTenant : productCategories) {
				if ((ProductCategoryTenant.getParent() != null && ProductCategoryTenant.getParent().equals(parent)) || (ProductCategoryTenant.getParent() == null && parent == null)) {
					result.add(ProductCategoryTenant);
					result.addAll(sort(productCategories, ProductCategoryTenant));
				}
			}
		}
		return result;
	}

	/**
	 * 设置值
	 * @param ProductCategoryTenant 商品分类
	 */
	private void setValue(ProductCategoryTenant ProductCategoryTenant) {
		if (ProductCategoryTenant == null) {
			return;
		}
		ProductCategoryTenant parent = ProductCategoryTenant.getParent();
		if (parent != null) {
			ProductCategoryTenant.setTreePath(parent.getTreePath() + parent.getId() + ProductCategoryTenant.TREE_PATH_SEPARATOR);
		} else {
			ProductCategoryTenant.setTreePath(ProductCategoryTenant.TREE_PATH_SEPARATOR);
		}
		ProductCategoryTenant.setGrade(ProductCategoryTenant.getTreePaths().size());
	}

	public List<ProductCategoryTenant> findList(Tenant tenant, Integer count) {
		if (tenant == null)
			return new ArrayList<ProductCategoryTenant>();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProductCategoryTenant> criteriaQuery = criteriaBuilder.createQuery(ProductCategoryTenant.class);
		Root<ProductCategoryTenant> root = criteriaQuery.from(ProductCategoryTenant.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));

		return super.findList(criteriaQuery, null, count, null, null);
	}

}