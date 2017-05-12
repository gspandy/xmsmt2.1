/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.ReturnsDao;
import net.wit.entity.Returns;
import net.wit.entity.Returns.ReturnStatus;
import net.wit.entity.Tenant;

import java.util.ArrayList;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

/**
 * Dao - 退货单
 * @author rsico Team
 * @version 3.0
 */
@Repository("returnsDaoImpl")
public class ReturnsDaoImpl extends BaseDaoImpl<Returns, Long>implements ReturnsDao {

	public Page<Returns> findPage(Tenant tenant, ReturnStatus returnStatus, Pageable pageable) {
		if (tenant == null) {
			return new Page<Returns>(new ArrayList<Returns>(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Returns> createQuery = criteriaBuilder.createQuery(Returns.class);
		Root<Returns> root = createQuery.from(Returns.class);
		createQuery.select(root);
		Predicate conjunction = criteriaBuilder.conjunction();
		conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("order").get("tenant"), tenant));
		if (returnStatus != null) {
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("orderStat"), returnStatus));
		}
		createQuery.where(conjunction);
		return super.findPage(createQuery, pageable);
	}
}