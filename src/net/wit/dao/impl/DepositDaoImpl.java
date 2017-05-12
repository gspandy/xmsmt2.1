/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.DepositDao;
import net.wit.entity.Deposit;
import net.wit.entity.Member;
import net.wit.util.DateUtil;

import org.springframework.stereotype.Repository;

/**
 * Dao - 预存款
 * @author rsico Team
 * @version 3.0
 */
@Repository("depositDaoImpl")
public class DepositDaoImpl extends BaseDaoImpl<Deposit, Long> implements DepositDao {

	public Page<Deposit> findPage(Member member, Pageable pageable) {
		if (member == null) {
			return new Page<Deposit>(Collections.<Deposit> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Deposit> criteriaQuery = criteriaBuilder.createQuery(Deposit.class);
		Root<Deposit> root = criteriaQuery.from(Deposit.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get("member"), member));
		return super.findPage(criteriaQuery, pageable);
	}

	public List<Deposit> findList(Member member, Date beginTime, Date endTime) {
		StringBuffer hql = new StringBuffer("from Deposit  d where 1=1");
		if (member != null) {
			hql.append(" and d.member.id=" + member.getId());
		}
		if (beginTime != null) {
			hql.append(" and d.createDate >='" + DateUtil.changeDateToStr(beginTime,DateUtil.LINK_DISPLAY_DATE_FULL)+"'");
		}
		if (endTime != null) {
			hql.append(" and d.createDate<='" +  DateUtil.changeDateToStr(endTime,DateUtil.LINK_DISPLAY_DATE_FULL)+"'");
		}
		hql.append(" order by d.createDate desc");
		return entityManager.createQuery(hql.toString(), Deposit.class).setFlushMode(FlushModeType.COMMIT).getResultList();
	}

}