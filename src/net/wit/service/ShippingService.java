/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.util.List;
import java.util.Map;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Returns;
import net.wit.entity.Shipping;
import net.wit.entity.Tenant;

/**
 * Service - 发货单
 * @author rsico Team
 * @version 3.0
 */
public interface ShippingService extends BaseService<Shipping, Long> {

	/**
	 * 根据编号查找发货单
	 * @param sn 编号(忽略大小写)
	 * @return 若不存在则返回null
	 */
	Shipping findBySn(String sn);

	/**
	 * 查询物流动态
	 * @param shipping 发货单
	 * @return 物流动态
	 */
	Map<String, Object> query(Shipping shipping);
	
	/**
	 *查询退货单物流信息
	 * @param returns
	 * @return
	 */
	public Map<String, Object> queryReturns(Returns returns);

	/**
	 * @Title：findPage @Description：
	 * @param tenant
	 * @param pageable
	 * @return Object
	 */
	Page<Shipping> findPage(Tenant tenant, Pageable pageable);

	/**
	 * @Title：importData @Description：
	 * @param titles
	 * @param shippings
	 * @param tenant
	 * @return String
	 */
	String importData(List<String> titles, List<Object[]> shippings, Tenant tenant);
	
	/**
	 * @Title：importData @Description：
	 * @param titles
	 * @param shippings
	 * @param tenant
	 * @return String
	 */
	String importShipped(List<String> titles, List<Object[]> shippings, Tenant tenant);

}