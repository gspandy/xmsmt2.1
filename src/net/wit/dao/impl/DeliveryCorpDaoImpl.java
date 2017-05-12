/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import net.wit.dao.DeliveryCorpDao;
import net.wit.entity.DeliveryCorp;
import net.wit.entity.ShippingMethod;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

/**
 * Dao - 物流公司
 * @author rsico Team
 * @version 3.0
 */
@Repository("deliveryCorpDaoImpl")
public class DeliveryCorpDaoImpl extends BaseDaoImpl<DeliveryCorp, Long>implements DeliveryCorpDao {

	public DeliveryCorp findByName(String name) {
		if (name == null) {
			return null;
		}
		String jpql = "select deliveryCorp from DeliveryCorp deliveryCorp where deliveryCorp.name = :name";
		try {
			return entityManager.createQuery(jpql, DeliveryCorp.class).setFlushMode(FlushModeType.COMMIT).setParameter("name", name).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}