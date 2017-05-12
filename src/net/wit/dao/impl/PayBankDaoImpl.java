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
import net.wit.dao.PayBankDao;
import net.wit.entity.Member;
import net.wit.entity.PayBank;

import org.springframework.stereotype.Repository;

/**
 * Dao - 收款单
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("payBankDaoImpl")
public class PayBankDaoImpl extends BaseDaoImpl<PayBank, Long> implements PayBankDao {

	public Page<PayBank> findPage(Member member, Pageable pageable) {
		if (member == null) {
			return new Page<PayBank>(Collections.<PayBank> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PayBank> criteriaQuery = criteriaBuilder.createQuery(PayBank.class);
		Root<PayBank> root = criteriaQuery.from(PayBank.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
	public PayBank findbyOrderNo(String sn) {
		if (sn == null) {
			return null;
		}
		String jpql = "select payBank from PayBank payBank where payBank.orderNo = :sn";
		try {
			return entityManager.createQuery(jpql, PayBank.class).setFlushMode(FlushModeType.COMMIT).setParameter("sn", sn).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}