/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.RoleDao;
import net.wit.entity.Role;
import net.wit.entity.Seo;
import net.wit.entity.Stock;
import net.wit.entity.Tenant;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

/**
 * Dao - 角色
 * @author rsico Team
 * @version 3.0
 */
@Repository("roleDaoImpl")
public class RoleDaoImpl extends BaseDaoImpl<Role, Long>implements RoleDao {

	public Page<Role> findPage(Tenant tenant, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Role> createQuery = criteriaBuilder.createQuery(Role.class);
		Root<Role> root = createQuery.from(Role.class);
		Predicate conjunctions = criteriaBuilder.conjunction();
		if (tenant != null) {
			conjunctions = criteriaBuilder.and(conjunctions, criteriaBuilder.equal(root.get("tenant"), tenant));
		} else {
			// conjunctions = criteriaBuilder.or(root.get("tenant").isNull(), criteriaBuilder.and(criteriaBuilder.equal(root.get("isSuper"), true),
			// criteriaBuilder.isNotNull(root.get("tenant"))));
		}
		createQuery.where(conjunctions);
		createQuery.select(root);
		return super.findPage(createQuery, pageable);
	}

	/*
	 * @Title: findList
	 * @Description:
	 * @param tenant
	 * @return
	 */

	@Override
	public List<Role> findList(Tenant tenant) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Role> createQuery = criteriaBuilder.createQuery(Role.class);
		Root<Role> root = createQuery.from(Role.class);
		Predicate conjunctions = criteriaBuilder.conjunction();
		if (tenant != null) {
			conjunctions = criteriaBuilder.and(conjunctions, criteriaBuilder.equal(root.get("tenant"), tenant));
		} else {
			// conjunctions = criteriaBuilder.or(root.get("tenant").isNull(), criteriaBuilder.and(criteriaBuilder.equal(root.get("isSuper"), true),
			// criteriaBuilder.isNotNull(root.get("tenant"))));
		}
		createQuery.where(conjunctions);
		createQuery.select(root);
		return super.findList(createQuery, null, null, null, null);
	}

	@Override
	public Role findCustomService(Tenant tenant, String name) {
		if (tenant == null||name==null) {
			return null;
		}
		String jpql = "select role from Role role where role.tenant = :tenant and role.name = :name";
		try {
			TypedQuery<Role> typedQuery = entityManager.createQuery(jpql, Role.class);
			typedQuery.setFlushMode(FlushModeType.COMMIT);
			typedQuery.setParameter("tenant", tenant);
			typedQuery.setParameter("name", name);
			return typedQuery.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}