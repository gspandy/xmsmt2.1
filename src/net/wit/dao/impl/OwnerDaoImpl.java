/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TicketSetDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月7日
 */
package net.wit.dao.impl;

import net.wit.Pageable;
import net.wit.entity.*;
import org.springframework.stereotype.Repository;

import net.wit.dao.OwnerDao;
import net.wit.dao.TicketSetDao;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
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
@Repository("ownerDaoImpl")
public class OwnerDaoImpl extends BaseDaoImpl<Owner, Long> implements OwnerDao{

    public List<Owner> getOwnerByMemberId(Long memberId) {
        if (memberId == null) {
            return new ArrayList<Owner>();
        }
        try {
            String jpql = "select owner from Owner owner where lower(owner.memberId) = lower(:memberId)";
            return entityManager.createQuery(jpql, Owner.class).setFlushMode(FlushModeType.COMMIT).setParameter("memberId", memberId).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
