/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantBonusSetDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月8日
 */
package net.wit.dao.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.wit.dao.TenantBonusSetDao;
import net.wit.entity.TenantBonusSet;
import net.wit.entity.TenantSellCondition;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月8日
 */
@Repository("tenantBonusSetDaoImpl")
public class TenantBonusSetDaoImpl extends BaseDaoImpl<TenantBonusSet, Long> implements TenantBonusSetDao{
	public List<TenantBonusSet> getTenantBonusSetByTenantId(Long tenantId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantBonusSet> criteriaQuery = criteriaBuilder.createQuery(TenantBonusSet.class);
		Root<TenantBonusSet> root = criteriaQuery.from(TenantBonusSet.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenantId"), tenantId));
		criteriaQuery.where(restrictions);
		List<TenantBonusSet> list = super.findList(criteriaQuery);
		return list;
	
}
}
