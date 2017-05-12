/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import net.wit.entity.DeliveryCorp;

/**
 * Dao - 物流公司
 * @author rsico Team
 * @version 3.0
 */
public interface DeliveryCorpDao extends BaseDao<DeliveryCorp, Long> {

	/**
	 * @Title：findByName @Description：
	 * @param name
	 * @return DeliveryCorp
	 */
	DeliveryCorp findByName(String name);

}