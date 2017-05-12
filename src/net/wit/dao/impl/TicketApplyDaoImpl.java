/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.TicketApplyDao;
import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.entity.TicketApply;
import net.wit.entity.TicketApply.ApplyStatus;
import net.wit.entity.TicketApply.ApplyType;

import org.springframework.stereotype.Repository;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
@Repository("ticketApplyDaoImpl")
public class TicketApplyDaoImpl extends BaseDaoImpl<TicketApply, Long> implements TicketApplyDao{

	@Override
	public List<TicketApply> queryTicketApplyByOwner(Member owner ,ApplyType applyType){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TicketApply> criteriaQuery = criteriaBuilder.createQuery(TicketApply.class);
		Root<TicketApply> root = criteriaQuery.from(TicketApply.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("owner"), owner));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("applyType"), applyType));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("applyStatus"), ApplyStatus.apply));
		criteriaQuery.where(restrictions);
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createDate")));
		List<TicketApply> returnlist = super.findList(criteriaQuery);
		return returnlist;
	}
	
	
	@Override
	public List<TicketApply> queryTicketApply(ApplyType applyType,ApplyStatus status,Tenant tenant){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TicketApply> criteriaQuery = criteriaBuilder.createQuery(TicketApply.class);
		Root<TicketApply> root = criteriaQuery.from(TicketApply.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("applyType"), applyType));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("applyStatus"), status));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		criteriaQuery.where(restrictions);
		List<TicketApply> returnlist = super.findList(criteriaQuery);
		return returnlist;
	}
	
	@Override
	public List<TicketApply> queryTicketApply(ApplyType applyType,ApplyStatus status,
			Tenant tenant,Member owner,Member member){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TicketApply> criteriaQuery = criteriaBuilder.createQuery(TicketApply.class);
		Root<TicketApply> root = criteriaQuery.from(TicketApply.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("applyType"), applyType));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("applyStatus"), status));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("owner"), owner));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		criteriaQuery.where(restrictions);
		List<TicketApply> returnlist = super.findList(criteriaQuery);
		return returnlist;
	}
	
	@Override
	public List<TicketApply> queryTicketApplyByOwner(Member owner,Date beginDate, Date endDate){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TicketApply> criteriaQuery = criteriaBuilder.createQuery(TicketApply.class);
		Root<TicketApply> root = criteriaQuery.from(TicketApply.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("owner"), owner));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("applyType"), ApplyType.shopkeeperApply));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(root.get("applyStatus"), ApplyStatus.rejected));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), beginDate));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThan(root.<Date> get("createDate"), endDate));
		criteriaQuery.where(restrictions);
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createDate")));
		List<TicketApply> returnlist = super.findList(criteriaQuery);
		return returnlist;
	}
	
	@Override
	public List<TicketApply> queryTicketApplyByMemberOwner(Member owner,Member member){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TicketApply> criteriaQuery = criteriaBuilder.createQuery(TicketApply.class);
		Root<TicketApply> root = criteriaQuery.from(TicketApply.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("owner"), owner));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("applyType"), ApplyType.memberApplyToShopKeeper));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("applyStatus"), ApplyStatus.apply));
		criteriaQuery.where(restrictions);
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createDate")));
		List<TicketApply> returnlist = super.findList(criteriaQuery);
		return returnlist;
	}

	public Page<TicketApply> findPageByCriteria(Tenant tenant,
			List<ApplyStatus> applyStatus, List<ApplyType> applyType,
			String mobile, Pageable pageable) {
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TicketApply> criteriaQuery = criteriaBuilder.createQuery(TicketApply.class);
		Root<TicketApply> root = criteriaQuery.from(TicketApply.class);
		criteriaQuery.select(root);
		Predicate restrictions = createQuery(applyStatus, applyType, criteriaBuilder, root);
		if (null != tenant) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		if (null != mobile && !"".equals(mobile)) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.like(root.get("owner").<String>get("mobile"), mobile + "%"));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
		
	}

	protected Predicate createQuery(List<ApplyStatus> applyStatus,
			List<ApplyType> applyType, CriteriaBuilder criteriaBuilder,
			Root<TicketApply> root) {
		Predicate restrictions = criteriaBuilder.conjunction();
		if (null != applyStatus && !"".equals(applyStatus)) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.in(root.get("applyStatus")).value(applyStatus));
		}
		if (null != applyType && !"".equals(applyType)) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.in(root.get("applyType")).value(applyType));
		}
		return restrictions;
	}

}
