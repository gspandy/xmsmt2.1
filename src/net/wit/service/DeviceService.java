/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.util.List;

import net.wit.entity.Device;

/**
 * Service - 设备
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface DeviceService extends BaseService<Device, Long> {

	/**
	 * 查找设备
	 * @param storeId	门店ID
	 * @return
	 */
	List<Device> findListByStoreId(String storeId);

}