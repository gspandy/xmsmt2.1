/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import net.wit.entity.DeliveryCorp;

/**
 * Service - 物流公司
 * @author rsico Team
 * @version 3.0
 */
public interface DeliveryCorpService extends BaseService<DeliveryCorp, Long> {

	/**
	 * @Title：findByName @Description：
	 * @param string
	 * @return DeliveryCorp
	 */
	DeliveryCorp findByName(String name);

}