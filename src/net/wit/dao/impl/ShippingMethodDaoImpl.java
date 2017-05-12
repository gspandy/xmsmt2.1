/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import net.wit.dao.ShippingMethodDao;
import net.wit.entity.ShippingMethod;

/**
 * Dao - 配送方式
 * @author rsico Team
 * @version 3.0
 */
@Repository("shippingMethodDaoImpl")
public class ShippingMethodDaoImpl extends BaseDaoImpl<ShippingMethod, Long>implements ShippingMethodDao {

	public ShippingMethod findByName(String name) {
		if (name == null) {
			return null;
		}
		String jpql = "select shippingMethod from ShippingMethod shippingMethod where shippingMethod.name = :name";
		try {
			return entityManager.createQuery(jpql, ShippingMethod.class).setFlushMode(FlushModeType.COMMIT).setParameter("name", name).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}