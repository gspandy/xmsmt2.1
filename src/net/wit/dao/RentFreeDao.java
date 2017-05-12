/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: TenantSellConditionDao.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月8日
 */
package net.wit.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import net.wit.entity.RentFree;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;


/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月8日
 */
public interface RentFreeDao extends BaseDao<RentFree, Long>{

//	public List<Tenant> findList(List<Tag> tags, Integer count) {
//		String sql = "select * from xx_tenant t1 left join xx_tenant_tag t2 on t1.id = t2.tenants where 1=1";
//		String t = null;
//		for (Tag tag : tags) {
//			if (tag != null) {
//				if (t == null) {
//					t = " and t2.tags in (" + tag.getId();
//				} else {
//					t = t + "," + tag.getId();
//				}
//			}
//		}
//		if (t != null) {
//			t = t + " )";
//			sql = sql + t;
//		}
//		sql = sql + " and t1.status = 2 order by create_date desc";
//		try {
//			Query query = entityManager.createNativeQuery(sql, Tenant.class);
//			if (count != null) {
//				query.setMaxResults(count);
//			}
//			return query.getResultList();
//		} catch (NoResultException e) {
//			return null;
//		}
//	}
	
}
