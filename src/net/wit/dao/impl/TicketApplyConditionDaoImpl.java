/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.TicketApplyConditionDao;
import net.wit.dao.TicketApplyDao;
import net.wit.dao.TicketDao;
import net.wit.entity.Member;
import net.wit.entity.OwnerIncome;
import net.wit.entity.Tenant;
import net.wit.entity.Ticket;
import net.wit.entity.TicketApplyCondition;
import net.wit.entity.TicketCache;
import net.wit.entity.Ticket.Status;
import net.wit.entity.TicketApply.ApplyStatus;
import net.wit.entity.TicketApply;

import org.springframework.stereotype.Repository;


/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
@Repository("ticketApplyConditionDaoImpl")
public class TicketApplyConditionDaoImpl extends BaseDaoImpl<TicketApplyCondition, Long> implements TicketApplyConditionDao{

	@Override
	public TicketApplyCondition getConditionByTenant(Tenant tenant){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TicketApplyCondition> criteriaQuery = criteriaBuilder.createQuery(TicketApplyCondition.class);
		Root<TicketApplyCondition> root = criteriaQuery.from(TicketApplyCondition.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		criteriaQuery.where(restrictions);
		List<TicketApplyCondition> list = super.findList(criteriaQuery);
		if(list.size()>0) return list.get(0);
		else return new TicketApplyCondition();
	}
	
	


}
