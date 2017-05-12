/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.AdminDao;
import net.wit.entity.Admin;
import net.wit.entity.Role;
import net.wit.entity.Tenant;

import org.springframework.stereotype.Repository;

/**
 * Dao - 管理员
 * @author rsico Team
 * @version 3.0
 */
@Repository("adminDaoImpl")
public class AdminDaoImpl extends BaseDaoImpl<Admin, Long>implements AdminDao {

	public boolean usernameExists(String username) {
		if (username == null) {
			return false;
		}
		String jpql = "select count(*) from Admin admin where lower(admin.username) = lower(:username)";
		Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT).setParameter("username", username).getSingleResult();
		return count > 0;
	}

	public Admin findByUsername(String username) {
		if (username == null) {
			return null;
		}
		try {
			String jpql = "select admin from Admin admin where lower(admin.username) = lower(:username)";
			return entityManager.createQuery(jpql, Admin.class).setFlushMode(FlushModeType.COMMIT).setParameter("username", username).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Page<Admin> findPage(Tenant tenant, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Admin> createQuery = criteriaBuilder.createQuery(Admin.class);
		Root<Admin> root = createQuery.from(Admin.class);
		Predicate conjunctions = criteriaBuilder.conjunction();
		if (tenant != null) {
			conjunctions = criteriaBuilder.and(conjunctions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		createQuery.where(conjunctions);
		createQuery.select(root);
		return super.findPage(createQuery, pageable);
	}

}