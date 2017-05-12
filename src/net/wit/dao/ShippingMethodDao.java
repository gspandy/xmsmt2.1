/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import net.wit.entity.ShippingMethod;

/**
 * Dao - 配送方式
 * @author rsico Team
 * @version 3.0
 */
public interface ShippingMethodDao extends BaseDao<ShippingMethod, Long> {

	/**
	 * @Title：findByName @Description：
	 * @param name
	 * @return ShippingMethod
	 */
	ShippingMethod findByName(String name);

}