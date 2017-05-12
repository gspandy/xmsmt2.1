/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.AdminDao;
import net.wit.dao.ShareSetDao;
import net.wit.entity.Admin;
import net.wit.entity.Review;
import net.wit.entity.Role;
import net.wit.entity.ShareSet;
import net.wit.entity.Tenant;
import net.wit.entity.TenantBonusSet;
import net.wit.entity.Review.Flag;
import net.wit.entity.Review.Type;

import org.springframework.stereotype.Repository;

/**
 * Dao - 管理员
 * @author rsico Team
 * @version 3.0
 */
@Repository("shareSetDaoImpl")
public class ShareSetDaoImpl extends BaseDaoImpl<ShareSet, Long>implements ShareSetDao {

	@Override
	public List<ShareSet> queryShareSetByTenant(Tenant tenant , ShareSet.Type type){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ShareSet> criteriaQuery = criteriaBuilder.createQuery(ShareSet.class);
		Root<ShareSet> root = criteriaQuery.from(ShareSet.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		if (type != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("type"), type));
		}

		criteriaQuery.where(restrictions);
		List<ShareSet> list = super.findList(criteriaQuery);
		return list;
	}

}