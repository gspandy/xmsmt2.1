/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao.impl;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.TenantSmDetailsDao;
import net.wit.entity.Tenant;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.TenantSmDetails;


/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
@Repository("tenantSmDetailsDaoImpl")
public class TenantSmDetailsDaoImpl extends BaseDaoImpl<TenantSmDetails, Long> implements TenantSmDetailsDao{

	@Override
	public Page<TenantSmDetails> findPage(Tenant tenant, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantSmDetails> criteriaQuery = criteriaBuilder.createQuery(TenantSmDetails.class);
		Root<TenantSmDetails> root = criteriaQuery.from(TenantSmDetails.class);
		Predicate restriction = criteriaBuilder.conjunction();
		if(tenant!=null){
			restriction = criteriaBuilder.and(restriction, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		criteriaQuery.select(root);
		criteriaQuery.where(restriction);
		return super.findPage(criteriaQuery, pageable);
	}

}
