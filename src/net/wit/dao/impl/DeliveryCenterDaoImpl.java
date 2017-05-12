/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.DeliveryCenterDao;
import net.wit.entity.Area;
import net.wit.entity.Community;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Location;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.support.EntitySupport;

/**
 * Dao - 发货点
 * @author rsico Team
 * @version 3.0
 */
@Repository("deliveryCenterDaoImpl")
public class DeliveryCenterDaoImpl extends BaseDaoImpl<DeliveryCenter, Long>implements DeliveryCenterDao {

	public DeliveryCenter findDefault() {
		try {
			String jpql = "select deliveryCenter from DeliveryCenter deliveryCenter where deliveryCenter.isDefault = true and tenant=:tenant";
			Tenant tenant = EntitySupport.createInitTenant();
			tenant.setId(1L);
			return entityManager.createQuery(jpql, DeliveryCenter.class).setParameter("tenant", tenant).setFlushMode(FlushModeType.COMMIT).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public DeliveryCenter findDefault(Tenant tenant) {
		try {
			String jpql = "select deliveryCenter from DeliveryCenter deliveryCenter where deliveryCenter.isDefault = true and tenant=:tenant";
			return entityManager.createQuery(jpql, DeliveryCenter.class).setParameter("tenant", tenant).setFlushMode(FlushModeType.COMMIT).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * 处理默认并保存
	 * @param deliveryCenter 发货点
	 */
	@Override
	public void persist(DeliveryCenter deliveryCenter) {
		Assert.notNull(deliveryCenter);
		if (deliveryCenter.getIsDefault()) {
			String jpql = "update DeliveryCenter deliveryCenter set deliveryCenter.isDefault = false where deliveryCenter.isDefault = true and deliveryCenter.tenant = :tenant";
			entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("tenant", deliveryCenter.getTenant()).executeUpdate();
		}
		super.persist(deliveryCenter);
	}

	/**
	 * 处理默认并更新
	 * @param deliveryCenter 发货点
	 * @return 发货点
	 */
	@Override
	public DeliveryCenter merge(DeliveryCenter deliveryCenter) {
		Assert.notNull(deliveryCenter);
		if (deliveryCenter.getIsDefault()) {
			String jpql = "update DeliveryCenter deliveryCenter set deliveryCenter.isDefault = false where deliveryCenter.isDefault = true and deliveryCenter.tenant = :tenant and deliveryCenter != :deliveryCenter";
			entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("tenant", deliveryCenter.getTenant()).setParameter("deliveryCenter", deliveryCenter).executeUpdate();
		}
		return super.merge(deliveryCenter);
	}

	public List<DeliveryCenter> findMyAll(Member member) {
		try {
			String jpql = "select deliveryCenter from DeliveryCenter deliveryCenter where tenant=:tenant";
			return entityManager.createQuery(jpql, DeliveryCenter.class).setFlushMode(FlushModeType.COMMIT).setParameter("tenant", member.getTenant()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<DeliveryCenter> findList(Area area, Community community) {
		if ((area == null) && (community == null)) {
			return new ArrayList<DeliveryCenter>();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeliveryCenter> criteriaQuery = criteriaBuilder.createQuery(DeliveryCenter.class);
		Root<DeliveryCenter> root = criteriaQuery.from(DeliveryCenter.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (area != null) {
			restrictions = criteriaBuilder.and(restrictions,
					criteriaBuilder.or(criteriaBuilder.equal(root.get("area"), area), criteriaBuilder.like(root.get("area").<String> get("treePath"), "%" + Area.TREE_PATH_SEPARATOR + area.getId() + Area.TREE_PATH_SEPARATOR + "%")));
		}
		if (community != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("community"), community));
		}
		criteriaQuery.where(restrictions);
		return entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT).getResultList();
	}

	public List<DeliveryCenter> findList(Area area, Location location, BigDecimal distatce) {
		if (location == null || !location.isExists()) {
			return new ArrayList<DeliveryCenter>();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeliveryCenter> criteriaQuery = criteriaBuilder.createQuery(DeliveryCenter.class);
		Root<DeliveryCenter> root = criteriaQuery.from(DeliveryCenter.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (area != null) {
			restrictions = criteriaBuilder.and(restrictions,
					criteriaBuilder.or(criteriaBuilder.equal(root.get("area"), area), criteriaBuilder.like(root.get("area").<String> get("treePath"), "%" + Area.TREE_PATH_SEPARATOR + area.getId() + Area.TREE_PATH_SEPARATOR + "%")));
		}
		criteriaQuery.where(restrictions);
		List<DeliveryCenter> dvcs = entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT).getResultList();
		List<DeliveryCenter> data = new ArrayList<DeliveryCenter>();
		for (Iterator<DeliveryCenter> it = dvcs.iterator(); it.hasNext();) {
			DeliveryCenter dc = (DeliveryCenter) it.next();
			if (dc.getDistatce(location).compareTo(distatce) < 0) {
				data.add(dc);
			}
		}

		class SortByX implements Comparator<Object> {
			public Location location;

			public int compare(Object obj1, Object obj2) {
				DeliveryCenter point1 = (DeliveryCenter) obj1;
				DeliveryCenter point2 = (DeliveryCenter) obj2;
				return point1.getDistatce(location).compareTo(point2.getDistatce(location));
			}

		}
		SortByX sortByX = new SortByX();
		sortByX.location = location;
		Collections.sort(data, sortByX);
		return data;
	}

	public Object findPage(Member member, Pageable pageable) {
		if (member == null) {
			return new Page<DeliveryCenter>(Collections.<DeliveryCenter> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeliveryCenter> criteriaQuery = criteriaBuilder.createQuery(DeliveryCenter.class);
		Root<DeliveryCenter> root = criteriaQuery.from(DeliveryCenter.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get("tenant"), member.getTenant()));
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<DeliveryCenter> findPage(Tenant tenant, Pageable pageable) {
		if (tenant == null) {
			return new Page<DeliveryCenter>(Collections.<DeliveryCenter> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeliveryCenter> criteriaQuery = criteriaBuilder.createQuery(DeliveryCenter.class);
		Root<DeliveryCenter> root = criteriaQuery.from(DeliveryCenter.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get("tenant"), tenant));
		return super.findPage(criteriaQuery, pageable);
	}

}