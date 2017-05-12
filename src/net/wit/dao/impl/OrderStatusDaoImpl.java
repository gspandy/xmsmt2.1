/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: OrderStatusConfigDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月18日
 */
package net.wit.dao.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.wit.dao.OrderStatusDao;
import net.wit.entity.OrderStatusConfig;
import net.wit.entity.Ticket;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月18日
 */
@Repository("orderStatusDaoImpl")
public class OrderStatusDaoImpl extends BaseDaoImpl<OrderStatusConfig, Long>implements OrderStatusDao{

	@Override
	public List<OrderStatusConfig> findList(String orderStatus,
			String paymentStatus, String shippingStatus) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OrderStatusConfig> criteriaQuery = criteriaBuilder.createQuery(OrderStatusConfig.class);
		Root<OrderStatusConfig> root = criteriaQuery.from(OrderStatusConfig.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("orderStatus"), orderStatus));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("paymentStatus"), paymentStatus));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("shippingStatus"), shippingStatus));
		criteriaQuery.where(restrictions);
		List<OrderStatusConfig> list = super.findList(criteriaQuery);
		return list;
	}

}
