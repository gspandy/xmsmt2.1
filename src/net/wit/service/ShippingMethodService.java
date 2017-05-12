/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import net.wit.entity.ShippingMethod;

/**
 * Service - 配送方式
 * @author rsico Team
 * @version 3.0
 */
public interface ShippingMethodService extends BaseService<ShippingMethod, Long> {

	/**
	 * @Title：findByName @Description：
	 * @param string
	 * @return ShippingMethod
	 */
	ShippingMethod findByName(String string);

}