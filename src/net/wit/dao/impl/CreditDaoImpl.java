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
import net.wit.dao.CreditDao;
import net.wit.entity.Credit;
import net.wit.entity.Member;

import org.springframework.stereotype.Repository;

/**
 * Dao - 收款单
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("creditDaoImpl")
public class CreditDaoImpl extends BaseDaoImpl<Credit, Long> implements CreditDao {

	public Credit findBySn(String sn) {
		if (sn == null) {
			return null;
		}
		String jpql = "select cedit from Cedit cedit where cedit.sn = :sn";
		try {
			return entityManager.createQuery(jpql, Credit.class).setFlushMode(FlushModeType.COMMIT).setParameter("sn", sn).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Page<Credit> findPage(Member member, Pageable pageable, Credit.Type type) {
		if (member == null) {
			return new Page<Credit>(Collections.<Credit> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Credit> criteriaQuery = criteriaBuilder.createQuery(Credit.class);
		Root<Credit> root = criteriaQuery.from(Credit.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("type"), type));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
}