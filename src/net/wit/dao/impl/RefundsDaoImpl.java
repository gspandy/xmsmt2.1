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
import net.wit.dao.RefundsDao;
import net.wit.entity.Order;
import net.wit.entity.Refunds;
import net.wit.entity.Tenant;

/**
 * Dao - 退款单
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("refundsDaoImpl")
public class RefundsDaoImpl extends BaseDaoImpl<Refunds, Long> implements
		RefundsDao {

	public Page<Refunds> findPage(Tenant tenant, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Refunds> criteriaQuery = criteriaBuilder
				.createQuery(Refunds.class);
		Root<Refunds> root = criteriaQuery.from(Refunds.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (tenant != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder
					.equal(root.get("order").get("tenant"), tenant));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	@Override
	public List<Refunds> findByOrder(Order order) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Refunds> criteriaQuery = criteriaBuilder
				.createQuery(Refunds.class);
		Root<Refunds> root = criteriaQuery.from(Refunds.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (order != null) {
			restrictions = criteriaBuilder.and(restrictions,
					criteriaBuilder.equal(root.get("order"), order));
		}
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery);
	}

}