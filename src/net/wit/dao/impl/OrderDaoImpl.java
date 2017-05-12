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
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import net.wit.Filter;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.OrderDao;
import net.wit.entity.Member;
import net.wit.entity.MemberRank;
import net.wit.entity.Order;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Order.PaymentStatus;
import net.wit.entity.Order.ShippingStatus;
import net.wit.entity.OrderItem;
import net.wit.entity.Product;
import net.wit.entity.Refunds;
import net.wit.entity.Tenant;
import net.wit.enums.OrderSearchStatus;
import net.wit.util.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * Dao - 订单
 * @author rsico Team
 * @version 3.0
 */
@Repository("orderDaoImpl")
public class OrderDaoImpl extends BaseDaoImpl<Order, Long>implements OrderDao {

	public Order findBySn(String sn) {
		if (sn == null) {
			return null;
		}
		String jpql = "select orders from Order orders where orders.sn = :sn";
		try {
			return entityManager.createQuery(jpql, Order.class).setFlushMode(FlushModeType.COMMIT).setParameter("sn", sn).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Order> findList(Member member, Integer count, List<Filter> filters, List<net.wit.Order> orders) {
		if (member == null) {
			return Collections.<Order> emptyList();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get("member"), member));
		return super.findList(criteriaQuery, null, count, filters, orders);
	}

	public Page<Order> findPage(Member member, Pageable pageable) {
		if (member == null) {
			return new Page<Order>(Collections.<Order> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get("member"), member));
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Order> findPage(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable) {
		if (member == null) {
			return new Page<Order>(Collections.<Order> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();

		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		if (orderStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("orderStatus"), orderStatus));
		}
		if (paymentStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("paymentStatus"), paymentStatus));
		}
		if (shippingStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("shippingStatus"), shippingStatus));
		}
		if (hasExpired != null) {
			if (hasExpired) {
				restrictions = criteriaBuilder.and(restrictions, root.get("expire").isNotNull(), criteriaBuilder.lessThan(root.<Date> get("expire"), new Date()));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("expire").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("expire"), new Date())));
			}
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Order> findPage(OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = createQuery(orderStatus, paymentStatus, shippingStatus, hasExpired, criteriaBuilder, root);
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	/**
	 * @Title：createQuery @Description：
	 * @param orderStatus
	 * @param paymentStatus
	 * @param shippingStatus
	 * @param hasExpired
	 * @param criteriaBuilder
	 * @param root
	 * @return Predicate
	 */
	protected Predicate createQuery(OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, CriteriaBuilder criteriaBuilder, Root<Order> root) {
		Predicate restrictions = criteriaBuilder.conjunction();
		if (orderStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("orderStatus"), orderStatus));
		}
		if (paymentStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("paymentStatus"), paymentStatus));
		}
		if (shippingStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("shippingStatus"), shippingStatus));
		}
		if (hasExpired != null) {
			if (hasExpired) {
				restrictions = criteriaBuilder.and(restrictions, root.get("expire").isNotNull(), criteriaBuilder.lessThan(root.<Date> get("expire"), new Date()));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("expire").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("expire"), new Date())));
			}
		}
		return restrictions;
	}

	/**
	 * @Title：findMemberAndOwer @Description：
	 * @param member
	 * @param owner
	 * @param orderStatus
	 * @param paymentStatus
	 * @param shippingStatus
	 * @param pageable
	 * @return Page<Order>
	 */
	public Page<Order> findMemberAndOwer(Member member, Member owner, List<OrderStatus> orderStatuses, List<PaymentStatus> paymentStatuses, List<ShippingStatus> shippingStatuses, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = createQuery(orderStatuses, paymentStatuses, shippingStatuses, criteriaBuilder, root);
		if (member != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		}
		if (owner != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("owner"), owner));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);

	}

	/**
	 * @Title：createQuery @Description：
	 * @param orderStatus
	 * @param paymentStatus
	 * @param shippingStatus
	 * @param hasExpired
	 * @param criteriaBuilder
	 * @param root
	 * @return Predicate
	 */
	protected Predicate createQuery(List<OrderStatus> orderStatuses, List<PaymentStatus> paymentStatuses, List<ShippingStatus> shippingStatuses, CriteriaBuilder criteriaBuilder, Root<Order> root) {
		Predicate restrictions = criteriaBuilder.conjunction();
		if (orderStatuses != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.in(root.get("orderStatus")).value(orderStatuses));
		}
		if (paymentStatuses != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.in(root.get("paymentStatus")).value(paymentStatuses));
		}
		if (shippingStatuses != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.in(root.get("shippingStatus")).value(shippingStatuses));
		}
		return restrictions;
	}

	public Page<Order> findPageWithoutStatus(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Pageable pageable) {
		if (member == null) {
			return new Page<Order>(Collections.<Order> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();

		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		if (orderStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(root.get("orderStatus"), orderStatus));
		}
		if (paymentStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(root.get("paymentStatus"), paymentStatus));
		}
		if (shippingStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(root.get("shippingStatus"), shippingStatus));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Order> findPageTenant(Member member, Date beginDate, Date endDate, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), endDate));
		}
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member").get("member"), member));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Long count(OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = createQuery(orderStatus, paymentStatus, shippingStatus, hasExpired, criteriaBuilder, root);
		criteriaQuery.where(restrictions);
		return super.count(criteriaQuery, null);
	}

	public Long waitingPaymentCount(Tenant tenant, Member member) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(root.get("orderStatus"), OrderStatus.completed), criteriaBuilder.notEqual(root.get("orderStatus"), OrderStatus.cancelled));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.unpaid), criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.partialPayment)));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("expire").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("expire"), new Date())));
		if (member != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		}
		if (tenant != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		criteriaQuery.where(restrictions);
		return super.count(criteriaQuery, null);
	}

	public Long waitingShippingCount(Tenant tenant, Member member) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(root.get("orderStatus"), OrderStatus.completed), criteriaBuilder.notEqual(root.get("orderStatus"), OrderStatus.cancelled),
				criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.paid), criteriaBuilder.equal(root.get("shippingStatus"), ShippingStatus.unshipped));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("expire").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("expire"), new Date())));
		if (member != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		}
		if (tenant != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		criteriaQuery.where(restrictions);
		return super.count(criteriaQuery, null);
	}

	public BigDecimal getSalesAmount(Date beginDate, Date endDate) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<BigDecimal> criteriaQuery = criteriaBuilder.createQuery(BigDecimal.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(criteriaBuilder.sum(root.<BigDecimal> get("amountPaid")));
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("orderStatus"), OrderStatus.completed));
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), endDate));
		}
		criteriaQuery.where(restrictions);
		return entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT).getSingleResult();
	}

	public Integer getSalesVolume(Date beginDate, Date endDate) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(criteriaBuilder.sum(root.join("orderItems").<Integer> get("shippedQuantity")));
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("orderStatus"), OrderStatus.completed));
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), endDate));
		}
		criteriaQuery.where(restrictions);
		return entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT).getSingleResult();
	}

	public void releaseStock() {
		String jpql = "select orders from Order orders where orders.isAllocatedStock = :isAllocatedStock and orders.expire is not null and orders.expire <= :now";
		List<Order> orders = entityManager.createQuery(jpql, Order.class).setParameter("isAllocatedStock", true).setParameter("now", new Date()).getResultList();
		if (orders == null || orders.isEmpty()) {
			return;
		}
		for (Order order : orders) {
			List<OrderItem> orderItems = order.getOrderItems();
			if (orderItems == null || orderItems.isEmpty()) {
				continue;
			}
			for (OrderItem orderItem : orderItems) {
				if (orderItem == null) {
					continue;
				}
				Product product = orderItem.getProduct();
				if (product == null) {
					continue;
				}
				entityManager.lock(product, LockModeType.PESSIMISTIC_WRITE);
				product.setAllocatedStock(product.getAllocatedStock() - (orderItem.calculateQuantity().subtract(orderItem.calculateShippedQuantity()).intValue()));
			}
			order.setIsAllocatedStock(false);
		}
	}

	/**
	 * 获取vip统计订单明细
	 */
	public Page<Order> findPage(Member member, Date beginDate, Date endDate, Pageable pageable) {
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
			Root<Order> root = criteriaQuery.from(Order.class);
			criteriaQuery.select(root);
			Predicate restrictions = criteriaBuilder.conjunction();

			if (beginDate != null) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), beginDate));
			}
			if (endDate != null) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), DateUtil.addDay(endDate, 1)));
			}
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(root.get("paymentStatus"), Order.PaymentStatus.unpaid));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("owner"), member));
			criteriaQuery.where(restrictions);
			return super.findPage(criteriaQuery, pageable);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Page<Order> findPageMember(Member member, Date beginDate, Date endDate, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Date> get("member"), member));

		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), endDate));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Order> findPageAll(Member member, Date beginDate, Date endDate, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();

		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), endDate));
		}
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.equal(root.get("member").get("member"), member), criteriaBuilder.equal(root.get("member"), member)));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Long countAll(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();

		Subquery<Order> subquery = criteriaQuery.subquery(Order.class);
		Root<Order> subqueryroot = subquery.from(Order.class);
		subquery.select(subqueryroot);
		subquery.where(criteriaBuilder.equal(subqueryroot, root), criteriaBuilder.equal(subqueryroot.join("trades").get("tenant").get("salesman"), member));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.exists(subquery), criteriaBuilder.equal(root.get("member"), member)));

		if (orderStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("orderStatus"), orderStatus));
		}
		if (paymentStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("paymentStatus"), paymentStatus));
		}
		if (shippingStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("shippingStatus"), shippingStatus));
		}
		if (hasExpired != null) {
			if (hasExpired) {
				restrictions = criteriaBuilder.and(restrictions, root.get("expire").isNotNull(), criteriaBuilder.lessThan(root.<Date> get("expire"), new Date()));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("expire").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("expire"), new Date())));
			}
		}
		criteriaQuery.where(restrictions);
		return super.count(criteriaQuery, null);
	}

	public long countmy(Member member, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Date> get("member"), member));

		if (orderStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("orderStatus"), orderStatus));
		}
		if (paymentStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("paymentStatus"), paymentStatus));
		}
		if (shippingStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("shippingStatus"), shippingStatus));
		}
		if (hasExpired != null) {
			if (hasExpired) {
				restrictions = criteriaBuilder.and(restrictions, root.get("expire").isNotNull(), criteriaBuilder.lessThan(root.<Date> get("expire"), new Date()));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("expire").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("expire"), new Date())));
			}
		}
		criteriaQuery.where(restrictions);
		return super.count(criteriaQuery, null);
	}

	public List<Order> findList(Tenant tenant, Date startDate, Date endDate, List<OrderStatus> orderStatuses, List<ShippingStatus> shippingStatuses) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.join("trades").get("tenant"), tenant));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.between(root.<Date> get("createDate"), startDate, endDate));
		if (orderStatuses != null && !orderStatuses.isEmpty()) {
			restrictions = criteriaBuilder.and(restrictions, root.get("orderStatus").in(orderStatuses));
		}
		if (shippingStatuses != null && !shippingStatuses.isEmpty()) {
			restrictions = criteriaBuilder.and(restrictions, root.get("shippingStatus").in(shippingStatuses));
		}
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery, null, null, null, null);
	}

	/**
	 * 查找会员代付款订单
	 */
	public Page<Order> findWaitPay(Member member, Boolean hasExpired, Pageable pageable) {
		if (member == null) {
			return new Page<Order>(Collections.<Order> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(
				criteriaBuilder.and(criteriaBuilder.and(restrictions, root.get("orderStatus").in(new OrderStatus[] { OrderStatus.unconfirmed, OrderStatus.confirmed })), criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.unpaid)),
				criteriaBuilder.equal(root.get("member"), member));
		if (hasExpired != null) {
			if (hasExpired) {
				restrictions = criteriaBuilder.and(restrictions, root.get("expire").isNotNull(), criteriaBuilder.lessThan(root.<Date> get("expire"), new Date()));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("expire").isNull(), criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("expire"), new Date())));
			}
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	/**
	 * 查找会员待发货订单
	 */
	public Page<Order> findWaitShipping(Member member, Boolean hasExpired, Pageable pageable) {
		if (member == null) {
			return new Page<Order>(Collections.<Order> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(criteriaBuilder.and(criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("orderStatus"), OrderStatus.confirmed)), criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.paid)),
				criteriaBuilder.equal(root.get("member"), member));
		criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("shippingStatus"), ShippingStatus.unshipped));
		if (hasExpired != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isExpired"), hasExpired));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	/*
	 * @Title: findPageByTenant
	 * @Description:
	 * @param tenant
	 * @param orderStatus
	 * @param paymentStatus
	 * @param shippingStatus
	 * @param hasExpired
	 * @param pageable
	 * @return
	 */

	public Page<Order> findPageByTenant(Tenant tenant, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Boolean hasExpired, Pageable pageable) {
		// if (tenant == null) {
		// return new Page<Order>(new ArrayList<Order>(), 0, pageable);
		// }
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = createQuery(orderStatus, paymentStatus, shippingStatus, hasExpired, criteriaBuilder, root);
		if (tenant != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	/*
	 * @Title: findPageByTenant
	 * @Description:
	 * @param tenant
	 * @param orderStatus
	 * @param paymentStatus
	 * @param shippingStatus
	 * @param hasExpired
	 * @param pageable
	 * @return
	 */
	public Page<Order> findPageByTenant(Tenant tenant, List<OrderStatus> orderStatuses, List<PaymentStatus> paymentStatuses, List<ShippingStatus> shippingStatuses, Boolean hasExpired, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = createQuery(orderStatuses, paymentStatuses, shippingStatuses, criteriaBuilder, root);
		if (tenant != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Order> findMemberAndOwer(Member member, Member owner, Boolean hasExpired, OrderStatus orderStatus, PaymentStatus paymentStatus, ShippingStatus shippingStatus, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = createQuery(orderStatus, paymentStatus, shippingStatus, hasExpired, criteriaBuilder, root);
		if (member != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		}
		if (owner != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("owner"), owner));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	/**
	 * 根据订单状态、支付状态、物流状态组合获取商家退款（货）单
	 */
	public Page<Order> findReturnPage(Tenant tenant, List<PaymentStatus> paymentStatuses, List<ShippingStatus> shippingStatuses, Pageable pageable) {
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
			Root<Order> root = criteriaQuery.from(Order.class);
			criteriaQuery.select(root);
			Predicate restrictions = criteriaBuilder.conjunction();
			restrictions = criteriaBuilder.or(root.get("paymentStatus").in(paymentStatuses), root.get("shippingStatus").in(shippingStatuses));
			if (tenant != null) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
			}

			// restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("refunds").get("status"), Refunds.RefurnsStatus.apply));
			criteriaQuery.where(restrictions);
			return super.findPage(criteriaQuery, pageable);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Page<Order> findPage(List<Tenant> tenant, Pageable pageable) {
		if (tenant.size() == 0) {
			return new Page<Order>();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.in(root.get("tenant")).value(tenant));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Order> findForExport(Tenant tenant, String name, Date beginDate, Date endDate, MemberRank owerRank, OrderSearchStatus status, String sn, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = createQuery(tenant, name, beginDate, endDate, owerRank, status, sn, criteriaBuilder, criteriaQuery, root,null);
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);

	}

	/**
	 * @Title：createQuery @Description：
	 * @param tenant
	 * @param name
	 * @param beginDate
	 * @param endDate
	 * @param owerRank
	 * @param status
	 * @param sn
	 * @param criteriaBuilder
	 * @param criteriaQuery
	 * @param root
	 * @param restrictions
	 * @return Predicate
	 */
	protected Predicate createQuery(Tenant tenant, String name, Date beginDate, Date endDate, MemberRank owerRank, OrderSearchStatus status, String sn, CriteriaBuilder criteriaBuilder, CriteriaQuery<Order> criteriaQuery, Root<Order> root,String memberName) {
		Predicate restrictions = criteriaBuilder.conjunction();
		if (tenant != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		if (StringUtils.isNotBlank(name)) {
			Subquery<OrderItem> namebquery = criteriaQuery.subquery(OrderItem.class);
			Root<OrderItem> nameRoot = namebquery.from(OrderItem.class);
			namebquery.select(nameRoot);
			namebquery.where(criteriaBuilder.and(criteriaBuilder.equal(root, nameRoot.get("order")), criteriaBuilder.like(nameRoot.<String> get("fullName"), "%" + name + "%")));
			restrictions = criteriaBuilder.and(criteriaBuilder.exists(namebquery), restrictions);
		}
		if (StringUtils.isNotBlank(sn)) {
			restrictions = criteriaBuilder.and(criteriaBuilder.like(root.<String> get("sn"), "%" + sn + "%"), restrictions);
		}
		if (memberName != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.like(root.get("owner").<String>get("name"), memberName + "%"));
		}
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), endDate));
		}
		if (owerRank != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("owner").get("memberRank"), owerRank));
		}
		// 等待付款
		if (OrderSearchStatus.waitPay.equals(status)) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.and(criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.unpaid), criteriaBuilder.notEqual(root.get("orderStatus"), OrderStatus.cancelled)));
			// 等待发货
		} else if (OrderSearchStatus.waitShipping.equals(status)) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.and(criteriaBuilder.and(criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.paid), criteriaBuilder.notEqual(root.get("orderStatus"), OrderStatus.completed)),
					criteriaBuilder.equal(root.get("shippingStatus"), ShippingStatus.unshipped)));
			// 已发货
		} else if (OrderSearchStatus.shippinged.equals(status)) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.and(criteriaBuilder.and(criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.paid), criteriaBuilder.notEqual(root.get("orderStatus"), OrderStatus.completed)),
					criteriaBuilder.equal(root.get("shippingStatus"), ShippingStatus.shipped)));
			// 退货申请中
		} else if (OrderSearchStatus.returning.equals(status)) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.and(criteriaBuilder.and(criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.paid), criteriaBuilder.notEqual(root.get("orderStatus"), OrderStatus.completed)),
					criteriaBuilder.equal(root.get("shippingStatus"), ShippingStatus.apply)));
			// 退款申请中
		} else if (OrderSearchStatus.refunding.equals(status)) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.and(criteriaBuilder.and(criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.refundapply), criteriaBuilder.notEqual(root.get("orderStatus"), OrderStatus.completed)),
					criteriaBuilder.equal(root.get("shippingStatus"), ShippingStatus.unshipped)));
			// 已退款
		} else if (OrderSearchStatus.refunded.equals(status)) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.and(criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.refunded), criteriaBuilder.equal(root.get("orderStatus"), OrderStatus.completed)));
			// 已签收
		} else if (OrderSearchStatus.signed.equals(status)) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.and(criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.paid), criteriaBuilder.equal(root.get("shippingStatus"), ShippingStatus.accept)));
			// 已关闭
		} else if (OrderSearchStatus.cancel.equals(status)) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("orderStatus"), OrderStatus.cancelled));
			// 交易成功
		} else if (OrderSearchStatus.complete.equals(status)) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("orderStatus"), OrderStatus.completed));
		}
		return restrictions;
	}

	public List<Order> findForExport(Tenant tenant, String name, Date beginDate, Date endDate, MemberRank owerRank, OrderSearchStatus status, String sn, Integer count,String memberNam) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = createQuery(tenant, name, beginDate, endDate, owerRank, status, sn, criteriaBuilder, criteriaQuery, root,memberNam);
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery, null, count, null, null);
	}
	
	@Override
	public List<Order> findOrderByOwner(Member owner,Date beginDate,Date endDate,PaymentStatus status){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), beginDate));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThan(root.<Date> get("createDate"), endDate));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("paymentStatus"),status));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("owner"), owner));
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery);
	}

	@Override
	public Page<Order> findPageByTenant(Tenant tenant,
			List<OrderStatus> orderStatuses,
			List<PaymentStatus> paymentStatuses,
			List<ShippingStatus> shippingStatuses, String productName,
			String username, String sn, Date startTime, Date endTime,
			Boolean hasExpired, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = createQuery(orderStatuses, paymentStatuses, shippingStatuses, criteriaBuilder, root);
		if (tenant != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		if (username != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.like(root.get("owner").<String>get("name"), username + "%"));
		}
		if (sn != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("sn"), sn));
		}
		if(startTime!=null)
		{
			restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), startTime));
		}
		if(endTime!=null)
		{
			restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), endTime));
		}
		if (StringUtils.isNotBlank(productName)) {
			Subquery<OrderItem> namebquery = criteriaQuery.subquery(OrderItem.class);
			Root<OrderItem> nameRoot = namebquery.from(OrderItem.class);
			namebquery.select(nameRoot);
			namebquery.where(criteriaBuilder.and(criteriaBuilder.equal(root, nameRoot.get("order")), criteriaBuilder.like(nameRoot.<String> get("fullName"), "%" + productName + "%")));
			restrictions = criteriaBuilder.and(criteriaBuilder.exists(namebquery), restrictions);
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
	
	
	
	@Override
	public List<Order> findOrdersByTenant(Tenant tenant,Member member,List<OrderStatus> orderStatuses,List<PaymentStatus> paymentStatuses) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> root = criteriaQuery.from(Order.class);
		criteriaQuery.select(root);
		Predicate restrictions = createQuery(orderStatuses, paymentStatuses, null,criteriaBuilder, root);
		if (tenant != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		if (tenant != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		}
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery);
	}

}