/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.DeliveryCenterDao;
import net.wit.dao.PromotionDao;
import net.wit.entity.Area;
import net.wit.entity.Community;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Location;
import net.wit.entity.Product;
import net.wit.entity.ProductCategory;
import net.wit.entity.Promotion;
import net.wit.entity.Tenant;
import net.wit.entity.Promotion.Classify;
import net.wit.entity.Promotion.Status;
import net.wit.entity.Promotion.Type;

import org.springframework.stereotype.Repository;

/**
 * Dao - 促销
 * @author rsico Team
 * @version 3.0
 */
@Repository("promotionDaoImpl")
public class PromotionDaoImpl extends BaseDaoImpl<Promotion, Long> implements PromotionDao {
	@Resource(name = "deliveryCenterDaoImpl")
	private DeliveryCenterDao deliveryCenterDao;

	public List<Promotion> findList(Type type, Boolean hasBegun, Boolean hasEnded, Area area, Integer count, List<Filter> filters, List<Order> orders) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Promotion> criteriaQuery = criteriaBuilder.createQuery(Promotion.class);
		Root<Promotion> root = criteriaQuery.from(Promotion.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (hasBegun != null) {
			if (hasBegun) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("beginDate").isNull(), criteriaBuilder.lessThanOrEqualTo(root.<Date> get("beginDate"), new Date())));
			} else {
				restrictions = criteriaBuilder.and(restrictions, root.get("beginDate").isNotNull(), criteriaBuilder.greaterThan(root.<Date> get("beginDate"), new Date()));
			}
		}
		if (hasEnded != null) {
			if (hasEnded) {
				restrictions = criteriaBuilder.and(restrictions, root.get("endDate").isNotNull(), criteriaBuilder.lessThan(root.<Date> get("endDate"), new Date()));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("endDate").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("endDate"), new Date())));
			}
		}
		if (type != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("type"), type));
		}
		if (area != null) {
			List<Tenant> tenants = new ArrayList<Tenant>();
			List<DeliveryCenter> dlvs = new ArrayList<DeliveryCenter>();
			dlvs = deliveryCenterDao.findList(area, null);

			for (Iterator<DeliveryCenter> it = dlvs.iterator(); it.hasNext();) {
				DeliveryCenter dc = it.next();
				tenants.add(dc.getTenant());
			}
			if (tenants.size() == 0) {
				return new ArrayList<Promotion>();
			}
			Subquery<Promotion> subquery = criteriaQuery.subquery(Promotion.class);
			Root<Promotion> productRoot = subquery.from(Promotion.class);
			subquery.select(productRoot);
			subquery.where(criteriaBuilder.equal(productRoot, root), productRoot.join("promotionProducts").get("product").get("tenant").in(tenants));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery, null, count, filters, orders);
	}

	public Page<Promotion> findPage(Type type, Area area, Boolean hasBegun, Boolean hasEnded, List<Status> status, Classify classify, Boolean periferal, Community community, Location location, BigDecimal distance, ProductCategory productCategory,
			Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Promotion> criteriaQuery = criteriaBuilder.createQuery(Promotion.class);
		Root<Promotion> root = criteriaQuery.from(Promotion.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (type != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("type"), type));
		}
		if (area != null) {
			List<Tenant> tenants = new ArrayList<Tenant>();
			List<DeliveryCenter> dlvs = new ArrayList<DeliveryCenter>();
			if ((periferal != null) && periferal) {
				if ((location == null || !location.isExists()) && community != null) {
					dlvs = deliveryCenterDao.findList(area, community.getLocation(), new BigDecimal(6));
				} else if (location.isExists()) {
					if (distance == null) {
						distance = new BigDecimal(6);
					}
					dlvs = deliveryCenterDao.findList(area, location, distance);
				}
			} else {
				dlvs = deliveryCenterDao.findList(area, community);
			}
			for (Iterator<DeliveryCenter> it = dlvs.iterator(); it.hasNext();) {
				DeliveryCenter dc = it.next();
				tenants.add(dc.getTenant());
			}
			if (tenants.size() == 0) {
				return new Page<Promotion>(Collections.<Promotion> emptyList(), 0, pageable);
			}
			Subquery<Promotion> subquery = criteriaQuery.subquery(Promotion.class);
			Root<Promotion> productRoot = subquery.from(Promotion.class);
			subquery.select(productRoot);
			subquery.where(criteriaBuilder.equal(productRoot, root), productRoot.join("promotionProducts").get("product").get("tenant").in(tenants));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		if (hasBegun != null) {
			if (hasBegun) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("beginDate").isNull(), criteriaBuilder.lessThanOrEqualTo(root.<Date> get("beginDate"), new Date())));
			} else {
				restrictions = criteriaBuilder.and(restrictions, root.get("beginDate").isNotNull(), criteriaBuilder.greaterThan(root.<Date> get("beginDate"), new Date()));
			}
		}
		if (hasEnded != null) {
			if (hasEnded) {
				restrictions = criteriaBuilder.and(restrictions, root.get("endDate").isNotNull(), criteriaBuilder.lessThan(root.<Date> get("endDate"), new Date()));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("endDate").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("endDate"), new Date())));
			}
		}

		if (status != null) {
			restrictions = criteriaBuilder.and(restrictions, root.get("status").in(status));
		}
		if (classify != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("classify"), classify));
		}

		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);

	}
}