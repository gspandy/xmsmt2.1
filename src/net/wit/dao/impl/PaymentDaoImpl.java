/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.Collections;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.PaymentDao;
import net.wit.entity.Member;
import net.wit.entity.Payment;
import net.wit.entity.Tenant;

import org.springframework.stereotype.Repository;

/**
 * Dao - 收款单
 * @author rsico Team
 * @version 3.0
 */
@Repository("paymentDaoImpl")
public class PaymentDaoImpl extends BaseDaoImpl<Payment, Long>implements PaymentDao {

	public Payment findBySn(String sn) {
		if (sn == null) {
			return null;
		}
		String jpql = "select payment from Payment payment where lower(payment.sn) = lower(:sn)";
		try {
			return entityManager.createQuery(jpql, Payment.class).setFlushMode(FlushModeType.COMMIT).setParameter("sn", sn).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Page<Payment> findPage(Member member, Pageable pageable, Payment.Type type) {
		if (member == null) {
			return new Page<Payment>(Collections.<Payment> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Payment> criteriaQuery = criteriaBuilder.createQuery(Payment.class);
		Root<Payment> root = criteriaQuery.from(Payment.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("type"), type));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(root.get("status"), Payment.Status.wait));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	/*
	 * @Title: findPage
	 * @Description:
	 * @param tenant
	 * @param pageable
	 * @return
	 */

	@Override
	public Page<Payment> findPage(Tenant tenant, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Payment> criteriaQuery = criteriaBuilder.createQuery(Payment.class);
		Root<Payment> root = criteriaQuery.from(Payment.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (tenant != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("order").get("tenant"), tenant));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
}