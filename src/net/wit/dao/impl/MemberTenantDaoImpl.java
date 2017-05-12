/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao.impl;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.MemberDao;
import net.wit.entity.*;
import net.wit.mobile.bo.Param;
import net.wit.mobile.cache.CacheUtil;
import net.wit.mobile.util.ShortUUID;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import net.wit.dao.MemberTenantDao;
import net.wit.dao.TicketSetDao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
@Repository("memberTanentImpl")
public class MemberTenantDaoImpl extends BaseDaoImpl<MemberTenant, Long> implements MemberTenantDao{
    public boolean addMemberTanent(MemberTenant memberTanent) {
        boolean flag = true;
        try {
            entityManager.persist(memberTanent);
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }
    
    public boolean getExistByTenantAndMember(Long tenantId ,Long memberId){ 
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<MemberTenant> criteriaQuery = criteriaBuilder.createQuery(MemberTenant.class);
		Root<MemberTenant> root = criteriaQuery.from(MemberTenant.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenantId"), tenantId));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("memberId"), memberId));
		criteriaQuery.where(restrictions);
		List<MemberTenant> list = super.findList(criteriaQuery);
		if(list.size()>0) return true;
		else return false;
    }

    public void addMember(Member member) {
        memberDao.persist(member);
    }

    @Autowired
    private MemberDao memberDao;

	@Override
	public List<MemberTenant> getMemberTenantByTenant(Long tenantId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<MemberTenant> criteriaQuery = criteriaBuilder.createQuery(MemberTenant.class);
		Root<MemberTenant> root = criteriaQuery.from(MemberTenant.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenantId"), tenantId));
		criteriaQuery.where(restrictions);
		List<MemberTenant> list = super.findList(criteriaQuery);
		return list;
	}
}
