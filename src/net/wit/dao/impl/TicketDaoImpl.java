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

import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.TicketDao;
import net.wit.entity.Member;
import net.wit.entity.Ticket;
import net.wit.entity.Tenant;
import net.wit.entity.Ticket.Status;

import org.springframework.stereotype.Repository;


/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
@Repository("ticketDaoImpl")
public class TicketDaoImpl extends BaseDaoImpl<Ticket, Long> implements TicketDao{

	@Override
	public List<Ticket> getTicketListByMemberId(Long memberId,int pageSize,int pageNo) {
		Member member = new Member();
		member.setId(memberId);
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Ticket> criteriaQuery = criteriaBuilder.createQuery(Ticket.class);
			Root<Ticket> root = criteriaQuery.from(Ticket.class);
			criteriaQuery.select(root);
			Predicate restrictions = criteriaBuilder.conjunction();
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
			criteriaQuery.where(restrictions);
			List<Ticket> list = super.findList(criteriaQuery);
			return list;
		
	}
	
	public List<Ticket> getTicketByShopkeeperId(Long shopkeeperId,Ticket.Status status) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Ticket> criteriaQuery = criteriaBuilder.createQuery(Ticket.class);
		Root<Ticket> root = criteriaQuery.from(Ticket.class);
		Member shopkeeper = new Member();
		shopkeeper.setId(shopkeeperId);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("shopkeeper"), shopkeeper));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), status));
		criteriaQuery.where(restrictions);
		List<Ticket> list = super.findList(criteriaQuery);
		return list;
	
	}
	
	public List<Ticket> getTicketByTenant(Long memberId,Long tenantId,Ticket.Status status) {
		
		Member member = new Member();
		Tenant tenant = new Tenant();
		tenant.setId(tenantId);
		member.setId(memberId);
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Ticket> criteriaQuery = criteriaBuilder.createQuery(Ticket.class);
		Root<Ticket> root = criteriaQuery.from(Ticket.class);
		
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), status));
		criteriaQuery.where(restrictions);
		List<Ticket> list = super.findList(criteriaQuery);
		return list;
	
	}
	
	public boolean isExistTicket(Long memberId) {
		if (memberId == null) {
			return false;
		}
		Member member = new Member();
		member.setId(memberId);
		String jpql = "select count(*) from Ticket ticket where ticket.member = :member "
				+ " or ticket.shopkeeper = :shopkeeper";
		Long count = entityManager.createQuery(jpql, Long.class)
				.setFlushMode(FlushModeType.COMMIT)
				.setParameter("member", member)
				.setParameter("shopkeeper", member)
				.getSingleResult();
		return count > 0;
	}
	
	public Long countTicketByMemberId(Long memberId,Long tenantId,Ticket.Status status) {
		Long count = 0l;
		Member member = new Member();
		member.setId(memberId);
		Tenant tenant = new Tenant();
		tenant.setId(tenantId);
		String jpql = "select count(*) from Ticket tic where 1=1 "
				+ " and tic.member = :member "
				+ " and tic.status = :status";
				if(tenantId != null){
					jpql = jpql+ " and tic.tenant = :tenant";
					
					count = entityManager.createQuery(jpql, Long.class)
							.setFlushMode(FlushModeType.COMMIT)
							.setParameter("member", member)
							.setParameter("status", status)
							.setParameter("tenant", tenant)
							.getSingleResult();
				}else{
					count = entityManager.createQuery(jpql, Long.class)
							.setFlushMode(FlushModeType.COMMIT)
							.setParameter("member", member)
							.setParameter("status", status)
							.getSingleResult();
				}
		
		return count;
	}

	@Override
	public List<Ticket> countTicketByMemberIdAndShopKeeperId(Long memberId,Long shopKeeperId,
			Long tenantId, Status status) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Ticket> criteriaQuery = criteriaBuilder.createQuery(Ticket.class);
		Root<Ticket> root = criteriaQuery.from(Ticket.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if(tenantId!=null){
			Tenant tenant = new Tenant();
			tenant.setId(tenantId);
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		if(memberId!=null){
			Member member = new Member();
			member.setId(memberId);
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		}
		if(shopKeeperId!=null){
			Member shopkeeper = new Member();
			shopkeeper.setId(shopKeeperId);
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("shopkeeper"), shopkeeper));
		}
		if(status!=null){
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), status));
		}
		criteriaQuery.where(restrictions);
		List<Ticket> list = super.findList(criteriaQuery);
		return list;
	}
	
	@Override
	public List<Ticket> queryTenantByTickets(Member member) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Ticket> criteriaQuery = criteriaBuilder.createQuery(Ticket.class);
		Root<Ticket> root = criteriaQuery.from(Ticket.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		criteriaQuery.where(restrictions).groupBy(root.get("tenant"));
		List<Ticket> list = super.findList(criteriaQuery);
		return list;
	}
	@Override
	public boolean updateTicketInvalid(Member shopkeeper){
		if(shopkeeper==null) return false;
		try {
			String jpql = "update Ticket  ticket set  ticket.status =:status where ticket.shopkeeper :=shopkeeper and ticket.status<=:status2";
			this.entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("status", Ticket.Status.expired).setParameter("shopkeeper", shopkeeper).setParameter("status2", Ticket.Status.recevied).executeUpdate();
			return true;
		} catch (Exception e) {
			return false;
		}
		
	}
	
	public Page<Ticket> findPage(Long tenantId,Member member, String ticketStatusParam, Date firstDayOfMonth, Date lastDayOfMonth, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Ticket> criteriaQuery = criteriaBuilder.createQuery(Ticket.class);
		Root<Ticket> root = criteriaQuery.from(Ticket.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if(member!=null){
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		}
		if (tenantId != null&&!"".equals(tenantId)) {
			Tenant tenant = new Tenant();
			tenant.setId(tenantId);
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		if (ticketStatusParam != null&&ticketStatusParam.equals("expired")) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), Ticket.Status.expired));
		}
		if (ticketStatusParam != null&&ticketStatusParam.equals("used")) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), Ticket.Status.used));
		}
		if (ticketStatusParam != null&&ticketStatusParam.equals("nouse")) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), Ticket.Status.nouse));
		}
		if (ticketStatusParam != null&&ticketStatusParam.equals("recevied")) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), Ticket.Status.recevied));
		}
		if (firstDayOfMonth != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("modifyDate"), firstDayOfMonth));
		}
		if (lastDayOfMonth != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date> get("modifyDate"), lastDayOfMonth));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
	
	public List<Ticket> findList(Long tenantId,Member member, String ticketStatusParam, Date firstDayOfMonth, Date lastDayOfMonth) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Ticket> criteriaQuery = criteriaBuilder.createQuery(Ticket.class);
		Root<Ticket> root = criteriaQuery.from(Ticket.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if(member!=null){
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		}
		if (tenantId != null&&!"".equals(tenantId)) {
			Tenant tenant = new Tenant();
			tenant.setId(tenantId);
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		if (ticketStatusParam != null&&ticketStatusParam.equals("expired")) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), Ticket.Status.expired));
		}
		if (ticketStatusParam != null&&ticketStatusParam.equals("used")) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), Ticket.Status.used));
		}
		if (ticketStatusParam != null&&ticketStatusParam.equals("nouse")) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), Ticket.Status.nouse));
		}
		if (ticketStatusParam != null&&ticketStatusParam.equals("recevied")) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), Ticket.Status.recevied));
		}
		if (firstDayOfMonth != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("modifyDate"), firstDayOfMonth));
		}
		if (lastDayOfMonth != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date> get("modifyDate"), lastDayOfMonth));
		}
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery);
	}

}
