/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.dao.OrderItemDao;
import net.wit.entity.Order;
import net.wit.entity.OrderItem;
import net.wit.entity.Refunds;

import org.springframework.stereotype.Repository;

/**
 * Dao - 订单项
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("orderItemDaoImpl")
public class OrderItemDaoImpl extends BaseDaoImpl<OrderItem, Long> implements OrderItemDao {
	@Override
	public List<OrderItem> findByOrder(Order order) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OrderItem> criteriaQuery = criteriaBuilder
				.createQuery(OrderItem.class);
		Root<OrderItem> root = criteriaQuery.from(OrderItem.class);
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