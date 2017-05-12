/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;

import net.wit.dao.TenantCategoryDao;
import net.wit.entity.ProductChannel;
import net.wit.entity.TenantCategory;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Dao - 企业分类
 * @author rsico Team
 * @version 3.0
 */
@Repository("tenantCategoryDaoImpl")
public class TenantCategoryDaoImpl extends BaseDaoImpl<TenantCategory, Long> implements TenantCategoryDao {

	public List<TenantCategory> findRoots(Integer count) {
		String jpql = "select tenantCategory from TenantCategory tenantCategory where tenantCategory.parent is null order by tenantCategory.order asc";
		TypedQuery<TenantCategory> query = entityManager.createQuery(jpql, TenantCategory.class).setFlushMode(FlushModeType.COMMIT);
		if (count != null) {
			query.setMaxResults(count);
		}
		return query.getResultList();
	}

	public List<TenantCategory> findParents(ProductChannel productChannel, TenantCategory tenantCategory, Integer count) {
		if (tenantCategory == null || tenantCategory.getParent() == null) {
			return Collections.<TenantCategory> emptyList();
		}
		String jpql = "select productCategory from TenantCategory tenantCategory where tenantCategory.id in (:ids) order by tenantCategory.grade asc";
		TypedQuery<TenantCategory> query = entityManager.createQuery(jpql, TenantCategory.class).setFlushMode(FlushModeType.COMMIT).setParameter("ids", tenantCategory.getTreeByChannelPaths(productChannel));
		if (count != null) {
			query.setMaxResults(count);
		}
		return query.getResultList();
	}

	public List<TenantCategory> findChildren(TenantCategory tenantCategory, Integer count) {
		TypedQuery<TenantCategory> query;
		if (tenantCategory != null) {
			String jpql = "select tenantCategory from TenantCategory tenantCategory where tenantCategory.treePath like :treePath order by tenantCategory.order asc";
			query = entityManager.createQuery(jpql, TenantCategory.class).setFlushMode(FlushModeType.COMMIT).setParameter("treePath", "%" + TenantCategory.TREE_PATH_SEPARATOR + tenantCategory.getId() + TenantCategory.TREE_PATH_SEPARATOR + "%");
		} else {
			String jpql = "select tenantCategory from TenantCategory tenantCategory order by tenantCategory.order asc";
			query = entityManager.createQuery(jpql, TenantCategory.class).setFlushMode(FlushModeType.COMMIT);
		}
		if (count != null) {
			query.setMaxResults(count);
		}
		return sort(query.getResultList(), tenantCategory);
	}

	/**
	 * 设置treePath、grade并保存
	 * @param productCategory 企业分类
	 */
	@Override
	public void persist(TenantCategory tenantCategory) {
		Assert.notNull(tenantCategory);
		setValue(tenantCategory);
		super.persist(tenantCategory);
	}

	/**
	 * 设置treePath、grade并更新
	 * @param productCategory 企业分类
	 * @return 企业分类
	 */
	@Override
	public TenantCategory merge(TenantCategory tenantCategory) {
		Assert.notNull(tenantCategory);
		setValue(tenantCategory);
		for (TenantCategory category : findChildren(tenantCategory, null)) {
			setValue(category);
		}
		return super.merge(tenantCategory);
	}

	/**
	 * 清除企业属性值并删除
	 * @param productCategory 企业分类
	 */
	@Override
	public void remove(TenantCategory tenantCategory) {
		if (tenantCategory != null) {
			super.remove(tenantCategory);
		}
	}

	/**
	 * 排序企业分类
	 * @param productCategories 企业分类
	 * @param parent 上级企业分类
	 * @return 企业分类
	 */
	private List<TenantCategory> sort(List<TenantCategory> tenantCategories, TenantCategory parent) {
		List<TenantCategory> result = new ArrayList<TenantCategory>();
		if (tenantCategories != null) {
			for (TenantCategory tenantCategory : tenantCategories) {
				if ((tenantCategory.getParent() != null && tenantCategory.getParent().equals(parent)) || (tenantCategory.getParent() == null && parent == null)) {
					result.add(tenantCategory);
					result.addAll(sort(tenantCategories, tenantCategory));
				}
			}
		}
		return result;
	}

	/**
	 * 设置值
	 * @param productCategory 企业分类
	 */
	private void setValue(TenantCategory tenantCategory) {
		if (tenantCategory == null) {
			return;
		}
		TenantCategory parent = tenantCategory.getParent();
		if (parent != null) {
			tenantCategory.setTreePath(parent.getTreePath() + parent.getId() + TenantCategory.TREE_PATH_SEPARATOR);
		} else {
			tenantCategory.setTreePath(TenantCategory.TREE_PATH_SEPARATOR);
		}
		tenantCategory.setGrade(tenantCategory.getTreePaths().size());
	}

	public List<TenantCategory> findParents(TenantCategory tenantCategory, Integer count) {
		if (tenantCategory == null || tenantCategory.getParent() == null) {
			return Collections.<TenantCategory> emptyList();
		}
		String jpql = "select productCategory from TenantCategory tenantCategory where tenantCategory.id in (:ids) order by tenantCategory.grade asc";
		TypedQuery<TenantCategory> query = entityManager.createQuery(jpql, TenantCategory.class).setFlushMode(FlushModeType.COMMIT).setParameter("ids", tenantCategory.getTreePaths());
		if (count != null) {
			query.setMaxResults(count);
		}
		return query.getResultList();
	}

}