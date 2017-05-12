/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import net.wit.dao.DeviceDao;
import net.wit.entity.Device;

import org.springframework.stereotype.Repository;

/**
 * Dao - 设备
 * 
 * @author rsico Team
 * @version 3.0
 */
@Repository("deviceDaoImpl")
public class DeviceDaoImpl extends BaseDaoImpl<Device, Long> implements DeviceDao {

	@SuppressWarnings("unchecked")
	public List<Device> findListByStoreId(String storeId) {
		String sql = "select * from xx_device t1 where t1.delivery_center='" + storeId + "'";
		try {
			Query query = entityManager.createNativeQuery(sql, Device.class);
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

}