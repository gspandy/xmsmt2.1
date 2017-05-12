/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao.impl;


import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;


import net.wit.dao.TenantTicketDao;
import net.wit.entity.TenantRenovation;
import net.wit.entity.TenantTicket;

import org.springframework.stereotype.Repository;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
@Repository("tenantTicketDaoImpl")
public class TenantTicketDaoImpl extends BaseDaoImpl<TenantTicket, Long> implements TenantTicketDao{

	public TenantTicket findByTanentId(Long tanentId) {
		if (tanentId == null) {
			return null;
		}
		String jpql = "select tt from TenantTicket tt where tt.tanentId = :tanentId";
		try {
			return entityManager.createQuery(jpql, TenantTicket.class)
					.setFlushMode(FlushModeType.COMMIT)
					.setParameter("tanentId", tanentId).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<TenantTicket> getTenantTicketByTenantId(Long tenantId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantTicket> criteriaQuery = criteriaBuilder.createQuery(TenantTicket.class);
		Root<TenantTicket> root = criteriaQuery.from(TenantTicket.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tanentId"), tenantId));
		criteriaQuery.where(restrictions);
		List<TenantTicket> list = super.findList(criteriaQuery);
		return list;
	}
	
}
