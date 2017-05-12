/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantSellConditionDaoImpl.java
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

import net.wit.dao.TenantSellConditionDao;
import net.wit.entity.TenantSellCondition;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月8日
 */
@Repository("tenantSellConditionDaoImpl")
public class TenantSellConditionDaoImpl extends BaseDaoImpl<TenantSellCondition, Long> implements TenantSellConditionDao{

	@Override
	public List<TenantSellCondition> getTenantSellConditionByTenantId(Long tenantId) {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<TenantSellCondition> criteriaQuery = criteriaBuilder.createQuery(TenantSellCondition.class);
			Root<TenantSellCondition> root = criteriaQuery.from(TenantSellCondition.class);
			criteriaQuery.select(root);
			Predicate restrictions = criteriaBuilder.conjunction();
			restrictions = criteriaBuilder.equal(root.get("tenantId"), tenantId);
			criteriaQuery.where(restrictions);
			List<TenantSellCondition> list = super.findList(criteriaQuery);
			return list;
		
	}

}
