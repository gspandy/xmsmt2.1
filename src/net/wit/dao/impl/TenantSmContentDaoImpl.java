/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao.impl;

import java.util.Collections;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.TenantSmContentDao;
import net.wit.dao.TicketSetDao;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.entity.TenantCategory;
import net.wit.entity.TenantSmContent;
import net.wit.entity.TicketSet;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
@Repository("tenantSmContentDaoImpl")
public class TenantSmContentDaoImpl extends BaseDaoImpl<TenantSmContent, Long> implements TenantSmContentDao{

	@Override
	public Page<TenantSmContent> findPage(Tenant tenant, Pageable pageable) {
		if (tenant == null) {
			return new Page<TenantSmContent>(Collections.<TenantSmContent> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TenantSmContent> criteriaQuery = criteriaBuilder.createQuery(TenantSmContent.class);
		Root<TenantSmContent> root = criteriaQuery.from(TenantSmContent.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

}
