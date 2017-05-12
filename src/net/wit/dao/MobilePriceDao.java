/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.entity.Area;
import net.wit.entity.MobilePrice;

/**
 * Dao - 手机快充价格
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface MobilePriceDao extends BaseDao<MobilePrice, Long> {

	/**
	 * 查找手机价格
	 * 
	 * @param mobile
	 *            手机号
	 * @return 价格列表
	 */
	
    List<MobilePrice> findByMobile(Area area);

}