/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantSellConditionDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月8日
 */
package net.wit.dao.impl;

import net.wit.entity.OwnerIncome;
import net.wit.entity.Ticket;

import org.springframework.stereotype.Repository;

import net.wit.dao.TenantRenovationDao;
import net.wit.entity.TenantRenovation;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月8日
 */
@Repository("tenantRenovationDaoImpl")
public class TenantRenovationDaoImpl extends BaseDaoImpl<TenantRenovation, Long> implements TenantRenovationDao{

    public List<TenantRenovation> getTenantRenovationByTenantId(Long tenantId) {
        if (tenantId == null) {
            return new ArrayList<TenantRenovation>();
        }
        try {
            String jpql = "select tenantRenovation from TenantRenovation tenantRenovation where lower(tenantRenovation.tenantId) = lower(:tenantId)   order by tenantRenovation.bannerNum";
            return entityManager.createQuery(jpql, TenantRenovation.class).setFlushMode(FlushModeType.COMMIT).setParameter("tenantId", tenantId).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    
    
	public List<TenantRenovation> getTenantRenovationByTenant(Long tenantId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantRenovation> criteriaQuery = criteriaBuilder.createQuery(TenantRenovation.class);
		Root<TenantRenovation> root = criteriaQuery.from(TenantRenovation.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenantId"), tenantId));
		criteriaQuery.where(restrictions);
		List<TenantRenovation> list = super.findList(criteriaQuery);
		return list;
	
}

}
