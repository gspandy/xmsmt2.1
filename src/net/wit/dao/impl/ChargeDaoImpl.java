/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.ChargeDao;
import net.wit.entity.Charge;
import net.wit.entity.Member;
import net.wit.entity.Order.OrderStatus;
import net.wit.entity.Tenant;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * 
 */
@Repository("chargeDaoImpl")
public class ChargeDaoImpl extends BaseDaoImpl<Charge, Long> implements ChargeDao {

	public Page<Charge> findPage(Charge charge,Member member,Tenant tenant,Pageable pageable) {
		
		List<Order> orders = pageable.getOrders();
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Charge> createQuery = criteriaBuilder.createQuery(Charge.class);
		Root<Charge> root = createQuery.from(Charge.class);
		createQuery.select(root);
		Predicate conjunction = criteriaBuilder.conjunction();
		if (StringUtils.isNotBlank(charge.getChargeDate())) {
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("chargeDate"), charge.getChargeDate()));
		}
		if (member != null) {
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("member"),member));
		}
		if (charge.getStatus() != null) {
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("status"), charge.getStatus()));
		}
		if(pageable.getSearchProperty()!=null&&pageable.getSearchProperty().equals("name")){
			try {
				if(pageable.getSearchValue()!=null){
					conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.like(root.get("member").<String>get("name"),"%"+new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8")+"%"));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			pageable.setSearchProperty(null);
			try {
				pageable.setSearchValue(new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (tenant != null) {
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		if (charge.getType() != null) {
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("type"), charge.getType()));
		}
		createQuery.where(conjunction);
		orders.add(Order.desc("chargeDate"));
		return super.findPage(createQuery, pageable);
	}
	
	public Page<Charge> findPageByParas(Long memberId,List<Charge.Status> status,Charge.Type type, Pageable pageable) {
		
		List<Order> orders = pageable.getOrders();
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Charge> createQuery = criteriaBuilder.createQuery(Charge.class);
		Root<Charge> root = createQuery.from(Charge.class);
		createQuery.select(root);
		Predicate conjunction = criteriaBuilder.conjunction();
		if (memberId != null) {
			Member member = new Member();
			member.setId(memberId);
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("member"), member));
		}
		if (status != null) {
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.in(root.get("status")).value(status));
		}
		if (type != null) {
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("type"), type));
		}
		createQuery.where(conjunction);
		orders.add(Order.desc("chargeDate"));
		return super.findPage(createQuery, pageable);
	}
	
	public List<Charge> findChargeInfo(Long memberId,List<Charge.Status> status){
		if(memberId == null){
			return Collections.<Charge> emptyList();
		}
		Member member = new Member();
		member.setId(memberId);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Charge> createQuery = criteriaBuilder.createQuery(Charge.class);
		Root<Charge> root = createQuery.from(Charge.class);
		createQuery.select(root);
		Predicate conjunction = criteriaBuilder.conjunction();
		conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("member"), member));
		if (status != null) {
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.in(root.get("status")).value(status));
		}
		createQuery.where(conjunction);
		return super.findList(createQuery);
	}
	
	public BigDecimal sumReceived(Long memberId) {
		Member member = new Member();
		member.setId(memberId);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<BigDecimal> criteriaQuery = criteriaBuilder.createQuery(BigDecimal.class);
		Root<Charge> root = criteriaQuery.from(Charge.class);
		criteriaQuery.select(criteriaBuilder.sum(root.<BigDecimal> get("charge")));
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), Charge.Status.received));
			
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		criteriaQuery.where(restrictions);
		return entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT).getSingleResult();
	}
	
	
	public List<Charge> findChargeInfo(String txNo){
		if(StringUtils.isBlank(txNo)){
			return Collections.<Charge> emptyList();
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Charge> createQuery = criteriaBuilder.createQuery(Charge.class);
		Root<Charge> root = createQuery.from(Charge.class);
		createQuery.select(root);
		Predicate conjunction = criteriaBuilder.conjunction();
		conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("txNo"), txNo));
		createQuery.where(conjunction);
		return super.findList(createQuery);
	}
	
	public boolean updateTicketInvalid(Member shopkeeper){
		if(shopkeeper==null) return false;
		try {
			String jpql = "update Charge c set c.status = :status "
					+ "where c.member = :member and c.status = :status1 ";
			entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT)
			.setParameter("status", Charge.Status.invalid)
			.setParameter("member", shopkeeper)
			.setParameter("status1", Charge.Status.notReceive).executeUpdate();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}