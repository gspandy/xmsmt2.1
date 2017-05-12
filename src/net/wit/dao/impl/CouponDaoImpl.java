/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.CouponDao;
import net.wit.entity.Coupon;
import net.wit.entity.Order;
import net.wit.entity.Product;
import net.wit.entity.Refunds;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao - 优惠券
 * @author rsico Team
 * @version 3.0
 */
@Repository("couponDaoImpl")
public class CouponDaoImpl extends BaseDaoImpl<Coupon, Long> implements CouponDao {

	public Page<Coupon> findPage(Boolean isEnabled, Boolean isExchange, Boolean hasExpired, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Coupon> criteriaQuery = criteriaBuilder.createQuery(Coupon.class);
		Root<Coupon> root = criteriaQuery.from(Coupon.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (isEnabled != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isEnabled"), isEnabled));
		}
		if (isExchange != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isExchange"), isExchange));
		}
		if (hasExpired != null) {
			if (hasExpired) {
				restrictions = criteriaBuilder.and(restrictions, root.get("endDate").isNotNull(), criteriaBuilder.lessThan(root.<Date> get("endDate"), new Date()));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("endDate").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("endDate"), new Date())));
			}
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Coupon findSystemPointExchange() {
		return find(1L);
	}
	
	@Override
	public List<Coupon> findByOrder(List<Order> orders) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Coupon> criteriaQuery = criteriaBuilder
				.createQuery(Coupon.class);
		Root<Coupon> root = criteriaQuery.from(Coupon.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (orders != null) {
			Subquery<Coupon> subquery = criteriaQuery.subquery(Coupon.class);
			Root<Coupon> subqueryRoot = subquery.from(Coupon.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.equal(subqueryRoot, root), subqueryRoot.join("orders").in(orders));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery);
	}

}