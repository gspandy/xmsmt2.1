/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.BrandDao;
import net.wit.entity.Brand;
import net.wit.entity.Tenant;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

/**
 * Dao - 品牌
 * @author rsico Team
 * @version 3.0
 */
@Repository("brandDaoImpl")
public class BrandDaoImpl extends BaseDaoImpl<Brand, Long>implements BrandDao {

	public Page<Brand> findPage(Tenant tenant, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Brand> createQuery = criteriaBuilder.createQuery(Brand.class);
		Root<Brand> root = createQuery.from(Brand.class);
		createQuery.select(root);
		if (tenant != null) {
			createQuery.where(criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		return super.findPage(createQuery, pageable);
	}

	public List<Brand> findList(Tenant tenant) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Brand> createQuery = criteriaBuilder.createQuery(Brand.class);
		Root<Brand> root = createQuery.from(Brand.class);
		createQuery.select(root);
		if (tenant != null) {
			createQuery.where(criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		return super.findList(createQuery);
	}

}