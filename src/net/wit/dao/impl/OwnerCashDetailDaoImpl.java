/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao.impl;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.wit.dao.OwnerCashDetailDao;
import net.wit.dao.TicketSetDao;
import net.wit.entity.Order;
import net.wit.entity.OwnerCashDetail;
import net.wit.entity.PayBank;
import net.wit.entity.TenantCategory;
import net.wit.entity.TicketSet;
import net.wit.entity.VersionUpdate;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
@Repository("ownerCashDetailDaoImpl")
public class OwnerCashDetailDaoImpl extends BaseDaoImpl<OwnerCashDetail, Long> implements OwnerCashDetailDao{

	@Override
	public List<OwnerCashDetail> getOwnerCashDetailByMemberId(Long memberId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OwnerCashDetail> criteriaQuery = criteriaBuilder.createQuery(OwnerCashDetail.class);
		Root<OwnerCashDetail> root = criteriaQuery.from(OwnerCashDetail.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("memberId"), memberId));
		criteriaQuery.where(restrictions);
		List<OwnerCashDetail> list = super.findList(criteriaQuery);
		return list;
	}

	
	@Override
	public OwnerCashDetail findByCashRequestId(String Ccashrequestid) {
		if (Ccashrequestid == null) {
			return null;
		}
		String jpql = "select ownerCashDetail from OwnerCashDetail ownerCashDetail where ownerCashDetail.cashrequestid = :Ccashrequestid";
		try {
			return entityManager.createQuery(jpql, OwnerCashDetail.class).setFlushMode(FlushModeType.COMMIT).setParameter("Ccashrequestid", Ccashrequestid).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
