/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import net.wit.entity.AdPosition;
import net.wit.entity.AdPositionTenant;
import net.wit.entity.Tenant;

/**
 * Dao - 广告位
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface AdPositionDao extends BaseDao<AdPosition, Long> {

	public	AdPosition find(Long id, Tenant tenant, Integer count);

	public AdPositionTenant findTenant(Long id);

	public void saveTenant(AdPositionTenant adPositionTenant);

}