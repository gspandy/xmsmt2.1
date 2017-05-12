/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao.impl;

import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Ad;
import org.springframework.stereotype.Repository;

import net.wit.dao.OwnerIncomeDao;
import net.wit.dao.TicketSetDao;
import net.wit.entity.OwnerIncome;
import net.wit.entity.TenantCategory;
import net.wit.entity.TicketSet;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月7日
 */
@Repository("ownerIncomeImpl")
public class OwnerIncomeDaoImpl extends BaseDaoImpl<OwnerIncome, Long> implements OwnerIncomeDao{
    public Page<OwnerIncome> getOwnerIncomeByMemberId(Long memberId, Pageable pageable) {
        if (memberId == null) {
            return new Page<OwnerIncome>();
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OwnerIncome> criteriaQuery = criteriaBuilder.createQuery(OwnerIncome.class);
        Root<OwnerIncome> root = criteriaQuery.from(OwnerIncome.class);
        criteriaQuery.select(root);
        Predicate restrictions = criteriaBuilder.conjunction();
        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("memberId"), memberId));
        criteriaQuery.where(restrictions);

        return super.findPage(criteriaQuery, pageable);
    }
}
