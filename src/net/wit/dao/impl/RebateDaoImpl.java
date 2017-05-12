/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.Collections;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.RebateDao;
import net.wit.entity.Member;
import net.wit.entity.Rebate;

import org.springframework.stereotype.Repository;

/**
 * Dao - 收款单
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("rebateDaoImpl")
public class RebateDaoImpl extends BaseDaoImpl<Rebate, Long> implements RebateDao {

	public Page<Rebate> findPage(Member member, Pageable pageable) {
		if (member == null) {
			return new Page<Rebate>(Collections.<Rebate> emptyList(), 0, pageable);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Rebate> criteriaQuery = criteriaBuilder.createQuery(Rebate.class);
		Root<Rebate> root = criteriaQuery.from(Rebate.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), member));
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
	
	public void calcRebate() {
		String  jpql = "";
		        jpql +="insert into xx_rebate(create_date,modify_date,rebate_date,";
				jpql +="lv1member_count,lv1amount,lv1rebate,";
				jpql +="lv2member_count,lv2amount,lv2rebate,";
				jpql +="status,member) ";
				jpql +="select curdate() as create_date,curdate() as modify_date,date_sub(curdate(),interval 1 day) as rebate_date,";
				jpql +="sum(lv1_member_count),sum(lv1_amount),sum(lv1_rebate),";
				jpql +="sum(lv2_member_count),sum(lv2_amount),sum(lv2_rebate),";
				jpql +="0 as status,";
				jpql +="member from (";
				jpql +="select ";
				jpql +="count(distinct a.id) lv1_member_count,sum(a.amount) lv1_amount,sum(a.recv-a.cost)*0.3 lv1_rebate,";
				jpql +="0 as lv2_member_count,0 as lv2_amount,0 as lv2_rebate,";
				jpql +="b.member as member ";
				jpql +="from xx_credit a,xx_member b where a.member=b.id ";
				jpql +="and status=2 and b.member is not null and ";
				jpql +="date(credit_date) = date_sub(curdate(),interval 1 day) ";
				jpql +="group by b.member ";
				jpql +="union all ";
				jpql +="select ";
				jpql +="0 as lv1_member_count,0 as lv1_amount,0 as lv1_rebate,";
				jpql +="count(distinct a.id) lv2_member_count,sum(a.amount) lv2_amount,sum(a.recv-a.cost)*0.03 lv2_rebate,";
				jpql +="c.member as member ";
				jpql +="from xx_credit a,xx_member b,xx_member c where a.member=b.id and b.member=c.id ";
				jpql +="and status=2 and c.member is not null and ";
				jpql +="date(credit_date) = date_sub(curdate(),interval 1 day) ";
				jpql +="group by c.member ";
				jpql +=") j group by member";
		Query query = entityManager.createNativeQuery(jpql);
		query.executeUpdate();
	}
}