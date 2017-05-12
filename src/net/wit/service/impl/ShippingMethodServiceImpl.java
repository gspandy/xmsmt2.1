/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.wit.dao.ShippingMethodDao;
import net.wit.entity.ShippingMethod;
import net.wit.service.ShippingMethodService;

/**
 * Service - 配送方式
 * @author rsico Team
 * @version 3.0
 */
@Service("shippingMethodServiceImpl")
public class ShippingMethodServiceImpl extends BaseServiceImpl<ShippingMethod, Long>implements ShippingMethodService {

	@Resource(name = "shippingMethodDaoImpl")
	private ShippingMethodDao shippingMethodDao;

	@Resource(name = "shippingMethodDaoImpl")
	public void setBaseDao(ShippingMethodDao shippingMethodDao) {
		super.setBaseDao(shippingMethodDao);
	}

	public ShippingMethod findByName(String name) {
		return shippingMethodDao.findByName(name);
	}

}