package net.wit.dao.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.TradeDao;
import net.wit.entity.Coupon;
import net.wit.entity.Member;
import net.wit.entity.Order;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Order.ShippingStatus;
import net.wit.entity.Trade;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: www.insuper.com
 * </p>
 * <p>
 * Company: www.insuper.com
 * </p>
 * @author chenqifu
 * @version 1.0 Apr 3, 2013
 */

@Repository("tradeDaoImpl")
public class TradeDaoImpl extends BaseDaoImpl<Trade, Long>implements TradeDao {

	public Trade findBySn(String sn) {
		if (sn == null) {
			return null;
		}
		String jpql = "select trade from Trade trade where trade.sn = :sn";
		try {
			return entityManager.createQuery(jpql, Trade.class).setFlushMode(FlushModeType.COMMIT).setParameter("sn", sn).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Page<Trade> findPage(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable) {
		if (member == null) {
			return new Page<Trade>(Collections.<Trade> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> criteriaQuery = criteriaBuilder.createQuery(Trade.class);
		Root<Trade> root = criteriaQuery.from(Trade.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), member.getTenant()));
		if (orderStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("order").get("orderStatus"), orderStatus));
		}
		if (paymentStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("order").get("paymentStatus"), paymentStatus));
		}
		if (shippingStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("shippingStatus"), shippingStatus));
		}
		if (hasExpired != null) {
			if (hasExpired) {
				restrictions = criteriaBuilder.and(restrictions, root.get("order").get("expire").isNotNull(), criteriaBuilder.lessThan(root.get("order").<Date> get("expire"), new Date()));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("order").get("expire").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.get("order").<Date> get("expire"), new Date())));
			}
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Trade> findPage(Member member, Date beginDate, Date endDate, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> criteriaQuery = criteriaBuilder.createQuery(Trade.class);
		Root<Trade> root = criteriaQuery.from(Trade.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), endDate));
		}
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant").get("salesman"), member));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Trade> findPage(Member member, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> criteriaQuery = criteriaBuilder.createQuery(Trade.class);
		Root<Trade> root = criteriaQuery.from(Trade.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("order").get("member"), member));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public long countTenant(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> criteriaQuery = criteriaBuilder.createQuery(Trade.class);
		Root<Trade> root = criteriaQuery.from(Trade.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (orderStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("order").get("orderStatus"), orderStatus));
		}
		if (paymentStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("order").get("paymentStatus"), paymentStatus));
		}
		if (shippingStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("order").get("shippingStatus"), shippingStatus));
		}
		if (hasExpired != null) {
			if (hasExpired) {
				restrictions = criteriaBuilder.and(restrictions, root.get("order").get("expire").isNotNull(), criteriaBuilder.lessThan(root.<Date> get("expire"), new Date()));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("order").get("expire").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("expire"), new Date())));
			}
		}
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant").get("salesman"), member));
		criteriaQuery.where(restrictions);
		return super.count(criteriaQuery, null);
	}
	
	@Override
	public List<Trade> findByOrder(Order order) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> criteriaQuery = criteriaBuilder
				.createQuery(Trade.class);
		Root<Trade> root = criteriaQuery.from(Trade.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (order != null) {
			restrictions = criteriaBuilder.and(restrictions,
					criteriaBuilder.equal(root.get("order"), order));
		}
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery);
	}

}
