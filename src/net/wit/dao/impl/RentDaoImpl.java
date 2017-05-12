/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao.impl;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.RentDao;
import net.wit.entity.Member;
import net.wit.entity.Rent;
import net.wit.util.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;


/**
 * 
 */
@Repository("rentDaoImpl")
public class RentDaoImpl extends BaseDaoImpl<Rent, Long> implements RentDao{
	
	public Page<Rent> findPage(Long memberId, String rentDate,Rent.Status status,Pageable pageable) {
		
		List<Order> orders = pageable.getOrders();
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Rent> createQuery = criteriaBuilder.createQuery(Rent.class);
		Root<Rent> root = createQuery.from(Rent.class);
		createQuery.select(root);
		Predicate conjunction = criteriaBuilder.conjunction();
		if (memberId != null) {
			Member member = new Member();
			member.setId(memberId);
			if(pageable.getSearchProperty()!=null&&pageable.getSearchProperty().equals("nickName")){
				try {
					member.setNickName(new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pageable.setSearchProperty(null);
				pageable.setSearchValue(null);
				conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.like(root.get("member").<String>get("nickName"),"%"+member.getNickName()+"%"));
			}else if(pageable.getSearchProperty()!=null&&pageable.getSearchProperty().equals("mobile")){
				try {
					member.setMobile(new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pageable.setSearchProperty(null);
				pageable.setSearchValue(null);
				conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.like(root.get("member").<String>get("mobile"),"%"+member.getMobile()+"%"));
			}else {
				conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("member"),member));
			}
			
		}
		if(memberId==null&&pageable.getSearchProperty()!=null&&pageable.getSearchProperty().equals("nickName")){
			Member member = new Member();
			try {
				member.setNickName(new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pageable.setSearchProperty(null);
			pageable.setSearchValue(null);
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.like(root.get("member").<String>get("nickName"),"%"+member.getNickName()+"%"));
    	}
		if(memberId==null&&pageable.getSearchProperty()!=null&&pageable.getSearchProperty().equals("mobile")){
			Member member = new Member();
			try {
				member.setMobile(new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pageable.setSearchProperty(null);
			pageable.setSearchValue(null);
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.like(root.get("member").<String>get("mobile"),"%"+member.getMobile()+"%"));
		}
		if (status != null) {
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("status"), status));
		}
		if (StringUtils.isNotBlank(rentDate)) {
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("rentDate"), rentDate));
		}
		createQuery.where(conjunction);
		orders.add(Order.desc("rentDate"));
		return super.findPage(createQuery, pageable);
	}
	
	public List<Rent> findRentList(Long memberId, Date date,Rent.Status status) {
		String strDate = DateUtil.changeDateToStr(date, DateUtil.LINK_DISPLAY_DATE_MONTH);
		Member member = new Member();
		member.setId(memberId);
		
		StringBuffer hql = new StringBuffer(" from Rent r where 1=1");
		hql.append(" and r.member = :member");
		hql.append(" and status = :status");
		hql.append(" and rentDate <= :rentDate");
		hql.append(" order by rentDate");
		return entityManager.createQuery(hql.toString(), Rent.class)
				.setFlushMode(FlushModeType.COMMIT)
				.setParameter("member", member)
				.setParameter("status", status)
				.setParameter("rentDate", strDate)
				.getResultList();
	}
	public List<Rent> findRentList(Long memberId, Rent.Status status) {
		Member member = new Member();
		member.setId(memberId);
		
		StringBuffer hql = new StringBuffer(" from Rent r where 1=1");
		hql.append(" and r.member = :member");
		hql.append(" and status = :status");
		hql.append(" order by rentDate");
		return entityManager.createQuery(hql.toString(), Rent.class)
				.setFlushMode(FlushModeType.COMMIT)
				.setParameter("member", member)
				.setParameter("status", status)
				.getResultList();
	}
	
	public Rent getLastChargedRent(Long memberId){
		Member member = new Member();
		member.setId(memberId);
		try {
			StringBuffer hql = new StringBuffer(" from Rent r where 1=1 ");
			hql.append(" and r.member = :member");
			hql.append(" and status in (")
			   .append(Rent.Status.charged.ordinal()).append(",")
			   .append(Rent.Status.system.ordinal()).append(")");
			hql.append(" order by rentDate desc");
			return entityManager.createQuery(hql.toString(), Rent.class)
			.setFlushMode(FlushModeType.COMMIT)
			.setParameter("member", member)
			.setFirstResult(0).setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
		
	}
	
	public int countRentCharged(Long memberId, Date date){
		Member member = new Member();
		member.setId(memberId);
		
		String strDate = DateUtil.changeDateToStr(date, DateUtil.LINK_DISPLAY_DATE_MONTH);
		StringBuffer hql = new StringBuffer("select count(*) from Rent r where 1=1 ");
		hql.append(" and r.member = :member");
		hql.append(" and status in (")
		   .append(Rent.Status.charged.ordinal()).append(",")
		   .append(Rent.Status.system.ordinal()).append(")");
		hql.append(" and rentDate >= :rentDate");
		Long count = entityManager.createQuery(hql.toString(), Long.class).
		setFlushMode(FlushModeType.COMMIT)
		.setParameter("member", member)
		.setParameter("rentDate", strDate).getSingleResult();
		return count.intValue();
	}
	
	public List<Rent> findRentInfo(Long memberId,Rent.Status status) {
		if(memberId == null){
			return Collections.<Rent> emptyList();
		}
		Member member = new Member();
		member.setId(memberId);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Rent> createQuery = criteriaBuilder.createQuery(Rent.class);
		Root<Rent> root = createQuery.from(Rent.class);
		createQuery.select(root);
		Predicate conjunction = criteriaBuilder.conjunction();
		conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("member"), member));
		if (status != null) {
			conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("status"), status));
		}
		createQuery.where(conjunction);
		return super.findList(createQuery);
	}
	
	public List<Rent> findByTxNo(String txNo) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Rent> createQuery = criteriaBuilder.createQuery(Rent.class);
		Root<Rent> root = createQuery.from(Rent.class);
		createQuery.select(root);
		Predicate conjunction = criteriaBuilder.conjunction();
		conjunction = criteriaBuilder.and(conjunction, criteriaBuilder.equal(root.get("txNo"), txNo));
		createQuery.where(conjunction);
		return super.findList(createQuery);
	}
}
