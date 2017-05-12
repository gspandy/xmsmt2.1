/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: VersionUpdateDaoImpl.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月11日
 */
package net.wit.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.VersionUpdateDao;
import net.wit.dao.VipLevelDao;
import net.wit.entity.OrderSettlement;
import net.wit.entity.Tenant;
import net.wit.entity.TenantCategory;
import net.wit.entity.TenantShopkeeper.IsShopkeeper;
import net.wit.entity.Ticket;
import net.wit.entity.TicketCache;
import net.wit.entity.VersionUpdate;
import net.wit.entity.VipLevel;

import org.springframework.stereotype.Repository;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月11日
 */
@Repository("vipLevelDaoImpl")
public class VipLevelDaoImpl extends BaseDaoImpl<VipLevel, Long> implements VipLevelDao{

	@Override
	public List<VipLevel> queryLevel(Tenant tenant, String levelName,
			Boolean isDefault,Integer level) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<VipLevel> criteriaQuery = criteriaBuilder.createQuery(VipLevel.class);
		Root<VipLevel> root = criteriaQuery.from(VipLevel.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if(tenant!=null){
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		}
		if(levelName!=null){
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("levelName"), levelName));
		}
		if(isDefault!=null){
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isDefault"), isDefault));
		}
		if(level!=null){
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("level"), level));
		}
		criteriaQuery.where(restrictions);
		criteriaQuery.orderBy(criteriaBuilder.asc(root.get("level")));
		List<VipLevel> list = super.findList(criteriaQuery);
		return list;
	}
	@Override
	public VipLevel queryLevel(Tenant tenant, Integer level) {
		if(tenant == null || level == null){
			return null;
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<VipLevel> criteriaQuery = criteriaBuilder.createQuery(VipLevel.class);
		Root<VipLevel> root = criteriaQuery.from(VipLevel.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("level"), level));
		criteriaQuery.where(restrictions);
		List<VipLevel> list = super.findList(criteriaQuery);
		if(list == null || list.size() == 0){
			return null;
		}
		return list.get(0);
	}
	@Override
	public VipLevel getLastLevel(Tenant tenant) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<VipLevel> criteriaQuery = criteriaBuilder.createQuery(VipLevel.class);
		Root<VipLevel> root = criteriaQuery.from(VipLevel.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("tenant"), tenant));
		criteriaQuery.where(restrictions);
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("level")));
		try {
			return entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT)
			.setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	@Override
	public void batchUpateVipLevel(VipLevel level)throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append(" update t_tenant_shopkeeper set ");
		sb.append(" vip_level = ").append(level.getId());
		sb.append(" where is_shopkeeper = '").append(IsShopkeeper.yes.ordinal()).append("'");
		sb.append(" and vip_level in (");
		sb.append(" SELECT id FROM t_vip_level WHERE LEVEL < ").append(level.getLevel()).append(")");
		sb.append(" and member_id IN (");
		sb.append(" SELECT t.ID FROM (");
		sb.append(" SELECT recommend_member_id as id FROM t_tenant_shopkeeper ");
		sb.append(" WHERE recommend_member_id IS NOT NULL ");
		sb.append(" GROUP BY recommend_member_id HAVING COUNT(recommend_member_id) >= ").append(level.getInviteCondition());
		sb.append(" ) t");
		sb.append(")");
		Query query = entityManager.createNativeQuery(sb.toString());
		query.executeUpdate();
	}

}
