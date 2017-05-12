/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.TicketCacheDao;
import net.wit.entity.Member;
import net.wit.entity.TicketCache;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
@Repository("ticketCacheDaoImpl")
public class TicketCacheDaoImpl extends BaseDaoImpl<TicketCache, Long> implements TicketCacheDao{

	public List<TicketCache> getTicketCacheByTenantId(Long memberId,String receiveStatus){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TicketCache> criteriaQuery = criteriaBuilder.createQuery(TicketCache.class);
		Root<TicketCache> root = criteriaQuery.from(TicketCache.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("memberId"), memberId));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("receiveStatus"), receiveStatus));
		criteriaQuery.where(restrictions);
		List<TicketCache> list = super.findList(criteriaQuery);
		return list;
	}
	
	public Long getshopkeeperNoUseCount(Long memberId,String receiveStatus){
		Long count = null;
		String jpql = "select sum(tc.num) from TicketCache tc where tc.memberId =:memberId and tc.receiveStatus = :receiveStatus";
		try {
			count = entityManager.createQuery(jpql,Long.class)
					.setFlushMode(FlushModeType.COMMIT).setParameter("memberId", memberId)
					.setParameter("receiveStatus",receiveStatus).getSingleResult();
		} catch (NoResultException e) {
			
		}
		if(count==null) count = new Long(0);
		return count;
	}
	@Override
	public boolean updateTicketCache(Long memberId){
		if(memberId==null) return false;
		try{
			String jpql = "update  TicketCache set  tc.receiveStatus =:receiveStatus  where tc.memberId =:memberId and tc.receiveStatus = :receiveStatus1";
			entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("memberId",memberId).setParameter("receiveStatus", TicketCache.TICKETCACHE_RECEIVEDSTATUS).
			setParameter("receiveStatus1", TicketCache.TICKETCACHE_NORECEIVESTATUS).executeUpdate();
			return true;
		}catch(Exception e){
			return false;
		}

	}
	@Override
	public List<TicketCache> getTicketCacheByMember(Long memberId){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TicketCache> criteriaQuery = criteriaBuilder.createQuery(TicketCache.class);
		Root<TicketCache> root = criteriaQuery.from(TicketCache.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("memberId"), memberId));
		criteriaQuery.where(restrictions);
		List<TicketCache> list = super.findList(criteriaQuery);
		return list;	
	}
	
	
	@Override
	public Page<TicketCache> findTickeCacheByPage(Long tenantId,Date startDate,Date endDate,String sendModel,List<Member> members,Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TicketCache> criteriaQuery = criteriaBuilder.createQuery(TicketCache.class);
		Root<TicketCache> root = criteriaQuery.from(TicketCache.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (tenantId != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Long>get("tenantId"), tenantId));
		}
		if(startDate!=null)
		{
			restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), startDate));
		}
		if(endDate!=null)
		{
			restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), endDate));
		}
		if(StringUtils.isNotBlank(sendModel)){
			restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.equal(root.<String> get("model"), sendModel));
		}
		if(members!=null){
			if(members.size()<1){
				return new Page<TicketCache>(Collections.<TicketCache> emptyList(), 0, pageable);
			}else{
				List<Long> ids=new ArrayList<Long>();
				for (Member member : members) {
					ids.add(member.getId());
				}
				restrictions=criteriaBuilder.and(restrictions, criteriaBuilder.in(root.<List<Long>> get("memberId")).value(ids));
			}
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
	
	
	@Override
	public List<TicketCache> findTickeCacheByList(Long tenantId,Date startDate,Date endDate,String sendModel,List<Member> members) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TicketCache> criteriaQuery = criteriaBuilder.createQuery(TicketCache.class);
		Root<TicketCache> root = criteriaQuery.from(TicketCache.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (tenantId != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Long>get("tenantId"), tenantId));
		}
		if(startDate!=null)
		{
			restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.greaterThanOrEqualTo(root.<Date> get("createDate"), startDate));
		}
		if(endDate!=null)
		{
			restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.lessThanOrEqualTo(root.<Date> get("createDate"), endDate));
		}
		if(StringUtils.isNotBlank(sendModel)){
			restrictions=criteriaBuilder.and(restrictions,criteriaBuilder.equal(root.<String> get("model"), sendModel));
		}
		if(members!=null){
			if(members.size()<1){
				return null;
			}else{
				List<Long> ids=new ArrayList<Long>();
				for (Member member : members) {
					ids.add(member.getId());
				}
				restrictions=criteriaBuilder.and(restrictions, criteriaBuilder.in(root.<List<Long>> get("memberId")).value(ids));
			}
		}
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery);
	}
	
	
	
}
