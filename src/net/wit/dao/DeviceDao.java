/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.entity.Device;

/**
 * Dao - 设备
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface DeviceDao extends BaseDao<Device, Long> {

	/**
	 * 查找设备
	 * @param storeId	门店ID
	 * @return
	 */
	List<Device> findListByStoreId(String storeId);

}