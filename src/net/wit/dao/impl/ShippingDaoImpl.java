/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.ShippingDao;
import net.wit.entity.Shipping;
import net.wit.entity.Tenant;

/**
 * Dao - 发货单
 * @author rsico Team
 * @version 3.0
 */
@Repository("shippingDaoImpl")
public class ShippingDaoImpl extends BaseDaoImpl<Shipping, Long>implements ShippingDao {

	public Shipping findBySn(String sn) {
		if (sn == null) {
			return null;
		}
		String jpql = "select shipping from Shipping shipping where lower(shipping.sn) = lower(:sn)";
		try {
			return entityManager.createQuery(jpql, Shipping.class).setFlushMode(FlushModeType.COMMIT).setParameter("sn", sn).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Page<Shipping> findPage(Tenant tenant, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Shipping> criteriaQuery = criteriaBuilder.createQuery(Shipping.class);
		Root<Shipping> root = criteriaQuery.from(Shipping.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (tenant != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("order").get("tenant"), tenant));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

}