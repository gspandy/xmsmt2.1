/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Order;
import net.wit.entity.Refunds;
import net.wit.entity.Tenant;

/**
 * Dao - 退款单
 * 
 * @author rsico Team
 * @version 3.0
 */
public interface RefundsDao extends BaseDao<Refunds, Long> {

	/**
	 * @Title：findPage
	 * @Description：
	 * @param tenant
	 * @param pageable
	 * @return Page<Refunds>
	 */
	Page<Refunds> findPage(Tenant tenant, Pageable pageable);

	List<Refunds> findByOrder(Order order);

}