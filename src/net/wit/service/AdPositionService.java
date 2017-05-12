/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import net.wit.entity.AdPosition;
import net.wit.entity.AdPositionTenant;
import net.wit.entity.Tenant;

/**
 * Service - 广告位
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface AdPositionService extends BaseService<AdPosition, Long> {

	/**
	 * 查找广告位(缓存)
	 * 
	 * @param id
	 *            ID
	 * @param tenant 商铺
	 * @param count 数量
	 * @param cacheRegion
	 *            缓存区域
	 * @return 广告位(缓存)
	 */
	AdPosition find(Long id, Tenant tenant, Integer count, String cacheRegion);

	/**
	 * 查找广告位(缓存)
	 * 
	 * @param id
	 *            ID
	 * @param tenant 商铺
	 *            缓存区域
	 * @param count 数量
	 * @return 广告位(缓存)
	 */
	AdPosition find(Long id, Tenant tenant, Integer count);

	/**
	 * 查找广告位(缓存)
	 * 
	 * @param id
	 *            ID
	 * @param tenant 商铺
	 *            缓存区域
	 * @param count 数量
	 * @return 广告位(缓存)
	 */
	AdPositionTenant findTenant(Long id);

	void saveTenant(AdPositionTenant adPositionTenant);
}