/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import net.wit.dao.AreaDao;
import net.wit.entity.Area;
import net.wit.entity.Area.Status;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * Dao - 地区
 * @author rsico Team
 * @version 3.0
 */
@Repository("areaDaoImpl")
public class AreaDaoImpl extends BaseDaoImpl<Area, Long> implements AreaDao {

	public List<Area> findRoots(Integer count) {
		String jpql = "select area from Area area where area.parent is null order by area.order asc";
		TypedQuery<Area> query = entityManager.createQuery(jpql, Area.class).setFlushMode(FlushModeType.COMMIT);
		if (count != null) {
			query.setMaxResults(count);
		}
		return query.getResultList();
	}

	public Area findByCode(String code) {
		String jpql = "select area from Area area where area.code=:code";
		try {
			return entityManager.createQuery(jpql, Area.class).setFlushMode(FlushModeType.COMMIT).setParameter("code", code).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}catch (Exception e) {
			return null;
		}
	}

	public Area findByZipCode(String zipCode) {
		String jpql = "select area from Area area where area.zipCode=:zipCode";
		try {
			return entityManager.createQuery(jpql, Area.class).setFlushMode(FlushModeType.COMMIT).setParameter("zipCode", zipCode).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}catch (Exception e) {
			return null;
		}
	}

	public Area findByTelCode(String code) {
		String jpql = "select area from Area area where area.telCode=:code";
		try {
			return entityManager.createQuery(jpql, Area.class).setFlushMode(FlushModeType.COMMIT).setParameter("code", code).getResultList().get(0);
		} catch (Exception e) {
			return null;
		}
	}

	public Area findByAreaName(String areaName) {
		if (StringUtils.isEmpty(areaName)) {
			return null;
		}
		String jpql = "select area from Area area where area.fullName like:areaName";
		try {
			return entityManager.createQuery(jpql, Area.class).setFlushMode(FlushModeType.COMMIT).setParameter("areaName", areaName).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Area> findOpenList() {
		String jpql = "select area from Area area where area.status =:status";
		try {
			return entityManager.createQuery(jpql, Area.class).setFlushMode(FlushModeType.COMMIT).setParameter("status", Status.enabled).getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<Area> findLevel(Long id) {
		String jpql = "select area from Area area where area.id=:id";
		try {
			return entityManager.createQuery(jpql, Area.class).setFlushMode(FlushModeType.COMMIT).setParameter("id",id).getResultList();
		} catch (NoResultException e) {
			return null;
		}catch (Exception e) {
			return null;
		}
	}

}