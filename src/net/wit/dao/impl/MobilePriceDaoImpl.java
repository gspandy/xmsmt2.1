/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;

import net.wit.dao.MobilePriceDao;
import net.wit.entity.Area;
import net.wit.entity.MobilePrice;

import org.springframework.stereotype.Repository;

/**
 * Dao - 手机快充价格
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("mobilePriceDaoImpl")
public class MobilePriceDaoImpl extends BaseDaoImpl<MobilePrice, Long> implements MobilePriceDao {

	public List<MobilePrice> findByMobile(Area area) {
		String jpql = "select mobilePrice from MobilePrice mobilePrice where mobilePrice.area=:area order by mobilePrice.denomination asc";
		TypedQuery<MobilePrice> query = entityManager.createQuery(jpql, MobilePrice.class).setFlushMode(FlushModeType.COMMIT).setParameter("area", area);
		return query.getResultList();
	}
}