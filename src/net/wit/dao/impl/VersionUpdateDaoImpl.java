/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: VersionUpdateDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月11日
 */
package net.wit.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.VersionUpdateDao;
import net.wit.entity.Tenant;
import net.wit.entity.TenantCategory;
import net.wit.entity.Ticket;
import net.wit.entity.VersionUpdate;

import org.springframework.stereotype.Repository;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月11日
 */
@Repository("versionUpdateDaoImpl")
public class VersionUpdateDaoImpl extends BaseDaoImpl<VersionUpdate, Long> implements VersionUpdateDao{
	@Override
	public Page<VersionUpdate> timeSearch(Date  startTime, Date endTime,
			Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<VersionUpdate> criteriaQuery = criteriaBuilder.createQuery(VersionUpdate.class);
		Root<VersionUpdate> root = criteriaQuery.from(VersionUpdate.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if(startTime!=null)
		{
			restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("versionDate"), startTime));
		}
		if(endTime!=null)
		{
			restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.lessThanOrEqualTo(root.<Date> get("versionDate"), endTime));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	
	@Override
	public List<VersionUpdate> versionUpdate(String versionType) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<VersionUpdate> criteriaQuery = criteriaBuilder.createQuery(VersionUpdate.class);
		Root<VersionUpdate> root = criteriaQuery.from(VersionUpdate.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("versionType"), versionType));
		criteriaQuery.where(restrictions);
		List<VersionUpdate> list = super.findList(criteriaQuery);
		return list;
	}

}
