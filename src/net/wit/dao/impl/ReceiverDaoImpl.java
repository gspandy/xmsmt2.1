/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.ReceiverDao;
import net.wit.entity.Member;
import net.wit.entity.Receiver;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Dao - 收货地址
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("receiverDaoImpl")
public class ReceiverDaoImpl extends BaseDaoImpl<Receiver, Long> implements ReceiverDao {

	public Receiver findDefault(Member member) {
		if (member == null) {
			return null;
		}
		try {
			String jpql = "select receiver from Receiver receiver where receiver.member = :member and receiver.isDefault = true";
			return entityManager.createQuery(jpql, Receiver.class).setFlushMode(FlushModeType.COMMIT).setParameter("member", member).getSingleResult();
		} catch (NoResultException e) {
			try {
				String jpql = "select receiver from Receiver receiver where receiver.member = :member order by receiver.modifyDate desc";
				return entityManager.createQuery(jpql, Receiver.class).setFlushMode(FlushModeType.COMMIT).setParameter("member", member).setMaxResults(1).getSingleResult();
			} catch (NoResultException e1) {
				return null;
			}
		}
	}

	public Page<Receiver> findPage(Member member, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Receiver> criteriaQuery = criteriaBuilder.createQuery(Receiver.class);
		Root<Receiver> root = criteriaQuery.from(Receiver.class);
		criteriaQuery.select(root);
		if (member != null) {
			criteriaQuery.where(criteriaBuilder.equal(root.get("member"), member));
		}
		return super.findPage(criteriaQuery, pageable);
	}

	/**
	 * 保存并处理默认
	 * 
	 * @param receiver
	 *            收货地址
	 */
	@Override
	public void persist(Receiver receiver) {
		Assert.notNull(receiver);
		Assert.notNull(receiver.getMember());
		if (receiver.getIsDefault()) {
			String jpql = "update Receiver receiver set receiver.isDefault = false where receiver.member = :member and receiver.isDefault = true";
			entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("member", receiver.getMember()).executeUpdate();
		}
		super.persist(receiver);
	}

	/**
	 * 更新并处理默认
	 * 
	 * @param receiver
	 *            收货地址
	 * @return 收货地址
	 */
	@Override
	public Receiver merge(Receiver receiver) {
		Assert.notNull(receiver);
		Assert.notNull(receiver.getMember());
		if (receiver.getIsDefault()) {
			String jpql = "update Receiver receiver set receiver.isDefault = false where receiver.member = :member and receiver.isDefault = true and receiver != :receiver";
			entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).setParameter("member", receiver.getMember()).setParameter("receiver", receiver).executeUpdate();
		}
		return super.merge(receiver);
	}

	public List<Receiver> findList(Member member){
		String jpql = "select receiver from Receiver receiver where receiver.member = :member";
		Query query = entityManager.createQuery(jpql, Receiver.class).setFlushMode(FlushModeType.COMMIT).setParameter("member", member);
		return query.getResultList();
	}
	
}