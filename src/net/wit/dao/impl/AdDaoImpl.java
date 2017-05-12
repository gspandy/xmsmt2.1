/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.AdDao;
import net.wit.entity.Ad;
import net.wit.entity.Tenant;

import org.springframework.stereotype.Repository;

/**
 * Dao - 广告
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("adDaoImpl")
public class AdDaoImpl extends BaseDaoImpl<Ad, Long> implements AdDao {
	public Page<Ad> findMyPage(Tenant tenant,Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Ad> criteriaQuery = criteriaBuilder.createQuery(Ad.class);
		Root<Ad> root = criteriaQuery.from(Ad.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
}