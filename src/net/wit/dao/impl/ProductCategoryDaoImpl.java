/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import net.wit.dao.ProductCategoryDao;
import net.wit.entity.Product;
import net.wit.entity.ProductCategory;
import net.wit.entity.ProductChannel;
import net.wit.entity.Tag;
import net.wit.entity.VersionUpdate;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Dao - 商品分类
 * @author rsico Team
 * @version 3.0
 */
@Repository("productCategoryDaoImpl")
public class ProductCategoryDaoImpl extends BaseDaoImpl<ProductCategory, Long>implements ProductCategoryDao {

	public List<ProductCategory> findRoots(Integer count) {
		String jpql = "select productCategory from ProductCategory productCategory where productCategory.parent is null order by productCategory.order asc";
		TypedQuery<ProductCategory> query = entityManager.createQuery(jpql, ProductCategory.class).setFlushMode(FlushModeType.COMMIT);
		if (count != null) {
			query.setMaxResults(count);
		}
		return query.getResultList();
	}

	public List<ProductCategory> findParents(ProductCategory productCategory, Integer count) {
		if (productCategory == null || productCategory.getParent() == null) {
			return Collections.<ProductCategory> emptyList();
		}
		String jpql = "select productCategory from ProductCategory productCategory where productCategory.id in (:ids) order by productCategory.grade asc";
		TypedQuery<ProductCategory> query = entityManager.createQuery(jpql, ProductCategory.class).setFlushMode(FlushModeType.COMMIT).setParameter("ids", productCategory.getTreePaths());
		if (count != null) {
			query.setMaxResults(count);
		}
		return query.getResultList();
	}

	public List<ProductCategory> findChildren(ProductCategory productCategory, Integer count, List<Tag> tags) {
		TypedQuery<ProductCategory> query;
		if (productCategory != null) {
			if (tags != null && tags.size() > 0) {
				String jpql = "select productCategory from ProductCategory productCategory left outer join productCategory.tags tag where tag in (:tags) and productCategory.treePath like :treePath and grade=:grade  order by productCategory.order asc";
				query = entityManager.createQuery(jpql, ProductCategory.class).setFlushMode(FlushModeType.COMMIT).setParameter("treePath", "%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%")
						.setParameter("grade", productCategory.getGrade() + 1).setParameter("tags", tags);
			} else {
				String jpql = "select productCategory from ProductCategory productCategory where productCategory.treePath like :treePath and grade=:grade  order by productCategory.order asc";
				query = entityManager.createQuery(jpql, ProductCategory.class).setFlushMode(FlushModeType.COMMIT).setParameter("treePath", "%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%")
						.setParameter("grade", productCategory.getGrade() + 1);
			}
		} else {
			if (tags != null && tags.size() > 0) {
				String jpql = "select productCategory from ProductCategory productCategory left outer join productCategory.tags tag where tag in (:tags) order by productCategory.order asc";
				query = entityManager.createQuery(jpql, ProductCategory.class).setFlushMode(FlushModeType.COMMIT).setParameter("tags", tags);
			} else {
				String jpql = "select productCategory from ProductCategory productCategory order by productCategory.order asc";
				query = entityManager.createQuery(jpql, ProductCategory.class).setFlushMode(FlushModeType.COMMIT);
			}
		}
		if (count != null) {
			query.setMaxResults(count);
		}
		return sort(query.getResultList(), productCategory);
	}

	public List<ProductCategory> findRoots(Tag tag, Integer count) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProductCategory> criteriaQuery = criteriaBuilder.createQuery(ProductCategory.class);
		Root<ProductCategory> root = criteriaQuery.from(ProductCategory.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (tag != null) {
			Subquery<ProductCategory> subquery = criteriaQuery.subquery(ProductCategory.class);
			Root<ProductCategory> subqueryRoot = subquery.from(ProductCategory.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tag));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		criteriaQuery.where(restrictions);
		criteriaQuery.orderBy(criteriaBuilder.asc(root.get("order")));
		return super.findList(criteriaQuery, null, count, null, null);
	}

	/**
	 * 设置treePath、grade并保存
	 * @param productCategory 商品分类
	 */
	@Override
	public void persist(ProductCategory productCategory) {
		Assert.notNull(productCategory);
		setValue(productCategory);
		super.persist(productCategory);
	}

	/**
	 * 设置treePath、grade并更新
	 * @param productCategory 商品分类
	 * @return 商品分类
	 */
	@Override
	public ProductCategory merge(ProductCategory productCategory) {
		Assert.notNull(productCategory);
		setValue(productCategory);
		for (ProductCategory category : findChildren(productCategory, null, null)) {
			setValue(category);
		}
		return super.merge(productCategory);
	}

	/**
	 * 清除商品属性值并删除
	 * @param productCategory 商品分类
	 */
	@Override
	public void remove(ProductCategory productCategory) {
		if (productCategory != null) {
			StringBuffer jpql = new StringBuffer("update Product product set ");
			for (int i = 0; i < Product.ATTRIBUTE_VALUE_PROPERTY_COUNT; i++) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + i;
				if (i == 0) {
					jpql.append("product." + propertyName + " = null");
				} else {
					jpql.append(", product." + propertyName + " = null");
				}
			}
			jpql.append(" where product.productCategory = :productCategory");
			entityManager.createQuery(jpql.toString()).setFlushMode(FlushModeType.COMMIT).setParameter("productCategory", productCategory).executeUpdate();
			super.remove(productCategory);
		}
	}

	/**
	 * 排序商品分类
	 * @param productCategories 商品分类
	 * @param parent 上级商品分类
	 * @return 商品分类
	 */
	private List<ProductCategory> sort(List<ProductCategory> productCategories, ProductCategory parent) {
		List<ProductCategory> result = new ArrayList<ProductCategory>();
		if (productCategories != null) {
			for (ProductCategory productCategory : productCategories) {
				if ((productCategory.getParent() != null && productCategory.getParent().equals(parent)) || (productCategory.getParent() == null && parent == null)) {
					result.add(productCategory);
					result.addAll(sort(productCategories, productCategory));
				}
			}
		}
		return result;
	}

	/**
	 * 设置值
	 * @param productCategory 商品分类
	 */
	private void setValue(ProductCategory productCategory) {
		if (productCategory == null) {
			return;
		}
		ProductCategory parent = productCategory.getParent();
		if (parent != null) {
			productCategory.setTreePath(parent.getTreePath() + parent.getId() + ProductCategory.TREE_PATH_SEPARATOR);
		} else {
			productCategory.setTreePath(ProductCategory.TREE_PATH_SEPARATOR);
		}
		productCategory.setGrade(productCategory.getTreePaths().size());
	}

	public List<ProductCategory> findParentsByChannel(ProductChannel productChannel, ProductCategory productCategory, Integer count) {
		List<Long> treeByChannelPaths = productCategory.getTreeByChannelPaths(productChannel);
		if (productCategory.getParent() == null || treeByChannelPaths == null || treeByChannelPaths.size() == 0) {
			return Collections.<ProductCategory> emptyList();
		}
		String jpql = "select productCategory from ProductCategory productCategory where productCategory.id in (:ids) order by productCategory.grade asc";
		TypedQuery<ProductCategory> query = entityManager.createQuery(jpql, ProductCategory.class).setFlushMode(FlushModeType.COMMIT).setParameter("ids", treeByChannelPaths);
		if (count != null) {
			query.setMaxResults(count);
		}
		return query.getResultList();
	}

	public List<ProductCategory> findRootsByChannel(ProductChannel productChannel, Integer count) {
		Set<ProductCategory> categorys = productChannel.getProductCategorys();
		List<Long> ids = new ArrayList<Long>();
		for (ProductCategory productCategory : categorys) {
			ids.add(productCategory.getId());
		}
		if (ids.size() > 0) {
			String jpql = "select productCategory from ProductCategory productCategory where productCategory.parent is null and productCategory.id in (:ids) order by productCategory.order asc";
			TypedQuery<ProductCategory> query = entityManager.createQuery(jpql, ProductCategory.class).setFlushMode(FlushModeType.COMMIT).setParameter("ids", ids);
			if (count != null) {
				query.setMaxResults(count);
			}
			return query.getResultList();
		} else {
			String jpql = "select productCategory from ProductCategory productCategory where productCategory.parent is null order by productCategory.order asc";
			TypedQuery<ProductCategory> query = entityManager.createQuery(jpql, ProductCategory.class).setFlushMode(FlushModeType.COMMIT);
			if (count != null) {
				query.setMaxResults(count);
			}
			return query.getResultList();
		}
	}

	public List<ProductCategory> findRootsByChannel(ProductChannel productChannel, Tag tags, Integer count) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProductCategory> criteriaQuery = criteriaBuilder.createQuery(ProductCategory.class);
		Root<ProductCategory> root = criteriaQuery.from(ProductCategory.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();

		if (productChannel != null) {
			Subquery<ProductCategory> subquery = criteriaQuery.subquery(ProductCategory.class);
			Root<ProductCategory> subqueryRoot = subquery.from(ProductCategory.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("productChannels").in(productChannel));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		if (tags != null) {
			Subquery<ProductCategory> subquery = criteriaQuery.subquery(ProductCategory.class);
			Root<ProductCategory> subqueryRoot = subquery.from(ProductCategory.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("tags").in(tags));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		criteriaQuery.where(restrictions);
		criteriaQuery.orderBy(criteriaBuilder.asc(root.get("order")));
		return super.findList(criteriaQuery, null, count, null, null);
	}

}