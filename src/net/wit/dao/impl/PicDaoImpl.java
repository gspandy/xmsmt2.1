/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.wit.dao.PicDao;
import net.wit.dao.TicketSetDao;
import net.wit.entity.Member;
import net.wit.entity.Pic;
import net.wit.entity.Tenant;
import net.wit.entity.TenantCategory;
import net.wit.entity.TenantShopkeeper;
import net.wit.entity.TicketSet;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
@Repository("picDaoImpl")
public class PicDaoImpl extends BaseDaoImpl<Pic, Long> implements PicDao{

	
	private static final String DEFAULT_PIC = "default";
	
	public List<Pic> getDefaultHeadImage(String picName){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Pic> criteriaQuery = criteriaBuilder.createQuery(Pic.class);
		Root<Pic> root = criteriaQuery.from(Pic.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("picType"), DEFAULT_PIC));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("picName"), picName));
		criteriaQuery.where(restrictions);
		List<Pic> list = super.findList(criteriaQuery);
		return list;
	}
	
	
	public List<Pic> getPicByTenantAndType(Tenant tenant,String picType){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Pic> criteriaQuery = criteriaBuilder.createQuery(Pic.class);
		Root<Pic> root = criteriaQuery.from(Pic.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenantId"), tenant.getId()));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("picType"), picType));
		criteriaQuery.where(restrictions);
		List<Pic> list = super.findList(criteriaQuery);
		return list;
	}

}
