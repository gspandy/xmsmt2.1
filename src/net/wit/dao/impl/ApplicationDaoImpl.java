/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import net.wit.dao.ApplicationDao;
import net.wit.entity.Application;
import net.wit.entity.Member;

import org.springframework.stereotype.Repository;

/**
 * Dao - 应用
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("applicationDaoImpl")
public class ApplicationDaoImpl extends BaseDaoImpl<Application, Long> implements ApplicationDao {

	public Application findByCode(Member member,String code) {
		if (code == null) {
			return null;
		}
		String jpql = "select applications from Application applications where applications.member=:member and applications.code = :code";
		try {
			return entityManager.createQuery(jpql, Application.class).setFlushMode(FlushModeType.COMMIT).setParameter("code", code).setParameter("member", member).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public List<Application> findByMember(Member member) {
		String jpql = "select applications from Application applications where applications.member=:member";
		try {
			Query query = entityManager.createQuery(jpql, Application.class).setFlushMode(FlushModeType.COMMIT).setParameter("member", member);
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
}