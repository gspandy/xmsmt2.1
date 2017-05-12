/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.SpecificationDao;
import net.wit.entity.ProductCategory;
import net.wit.entity.Specification;
import net.wit.entity.Tenant;

/**
 * Dao - 规格
 * @author rsico Team
 * @version 3.0
 */
@Repository("specificationDaoImpl")
public class SpecificationDaoImpl extends BaseDaoImpl<Specification, Long>implements SpecificationDao {

	public Page<Specification> findPage(ProductCategory productCategory, Tenant tenant, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Specification> createQuery = criterBuilder(productCategory, tenant, criteriaBuilder);
		return super.findPage(createQuery, pageable);
	}

	public List<Specification> findList(ProductCategory curproductCategory, Tenant tenant) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Specification> createQuery = criterBuilder(curproductCategory, tenant, criteriaBuilder);
		return super.findList(createQuery);
	}

	protected CriteriaQuery<Specification> criterBuilder(ProductCategory productCategory, Tenant tenant, CriteriaBuilder criteriaBuilder) {
		CriteriaQuery<Specification> createQuery = criteriaBuilder.createQuery(Specification.class);
		Root<Specification> root = createQuery.from(Specification.class);
		Predicate conjunction = criteriaBuilder.conjunction();
		createQuery.select(root);
		if (productCategory != null) {
			String categoryTreePath = ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR;
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.or(criteriaBuilder.equal(root.get("productCategory"), productCategory), criteriaBuilder.like(root.get("productCategory").<String> get("treePath"), "%" + categoryTreePath + "%")));
		}
		if (tenant != null) {
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("tenant"), tenant));
		} else {
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.isNull(root.get("tenant")));
		}
		createQuery.where(conjunction);
		return createQuery;
	}
}