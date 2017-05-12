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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import net.wit.dao.TicketSetDao;
import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.Tenant;
import net.wit.entity.TenantCategory;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.TicketSet;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
@Repository("ticketSetDaoImpl")
public class TicketSetDaoImpl extends BaseDaoImpl<TicketSet, Long> implements TicketSetDao{
	
	public List<TicketSet> getTicketSetByTenantId(Long tenantId){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TicketSet> criteriaQuery = criteriaBuilder.createQuery(TicketSet.class);
		Root<TicketSet> root = criteriaQuery.from(TicketSet.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenantId"), tenantId));
		criteriaQuery.where(restrictions);
		List<TicketSet> list = super.findList(criteriaQuery);
		return list;
	}
	
	public List<TicketSet> getTicketSet(Long tenantId,String sendType,String useFlag){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TicketSet> criteriaQuery = criteriaBuilder.createQuery(TicketSet.class);
		Root<TicketSet> root = criteriaQuery.from(TicketSet.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if(tenantId != null){
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenantId"), tenantId));
		}
		if(StringUtils.isNotEmpty(sendType)){
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("sendType"), sendType));
		}
		if(StringUtils.isNotEmpty(useFlag)){
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("useFlag"), useFlag));
		}
		
		criteriaQuery.where(restrictions);
		List<TicketSet> list = super.findList(criteriaQuery);
		return list;
	}

}
