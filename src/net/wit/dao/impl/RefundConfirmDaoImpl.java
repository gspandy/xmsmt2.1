/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: RefundConfirmDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月14日
 */
package net.wit.dao.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.RefundConfirmDao;
import net.wit.dao.RefundsDao;
import net.wit.entity.Refunds;
import net.wit.entity.Tenant;
import net.wit.entity.Ticket;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月14日
 */
@Repository("refundConfirmDaoImpl")
public class RefundConfirmDaoImpl extends BaseDaoImpl<Refunds, Long> implements RefundConfirmDao{

	public Page<Refunds> findPage(Tenant tenant, Pageable pageable) {
		if(tenant == null){
			return new Page<Refunds>();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Refunds> criteriaQuery = criteriaBuilder.createQuery(Refunds.class);
		Root<Refunds> root = criteriaQuery.from(Refunds.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("order").get("tenant"), tenant));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"),Refunds.RefurnsStatus.agree));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
		
	}
	@Override
	public List<Refunds> listAll(Tenant tenant) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Refunds> criteriaQuery = criteriaBuilder.createQuery(Refunds.class);
		Root<Refunds> root = criteriaQuery.from(Refunds.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("order").get("tenant"), tenant));
		criteriaQuery.where(restrictions);
		List<Refunds> list = super.findList(criteriaQuery);
		return list;
	}

}
