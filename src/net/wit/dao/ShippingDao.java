/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Shipping;
import net.wit.entity.Tenant;

/**
 * Dao - 发货单
 * @author rsico Team
 * @version 3.0
 */
public interface ShippingDao extends BaseDao<Shipping, Long> {

	/**
	 * 根据编号查找发货单
	 * @param sn 编号(忽略大小写)
	 * @return 若不存在则返回null
	 */
	Shipping findBySn(String sn);

	/**
	 * @Title：findPage @Description：
	 * @param tenant
	 * @param pageable
	 * @return Page<Shipping>
	 */
	Page<Shipping> findPage(Tenant tenant, Pageable pageable);

}