/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service;

import java.util.List;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Brand;
import net.wit.entity.Tenant;

/**
 * Service - 品牌
 * @author rsico Team
 * @version 3.0
 */
public interface BrandService extends BaseService<Brand, Long> {

	/**
	 * 查找品牌(缓存)
	 * @param count 数量
	 * @param filters 筛选
	 * @param orders 排序
	 * @param cacheRegion 缓存区域
	 * @return 品牌(缓存)
	 */
	List<Brand> findList(Integer count, List<Filter> filters, List<Order> orders, String cacheRegion);

	/**
	 * @Title：findPage @Description：
	 * @param tenant
	 * @param pageable
	 * @return Object
	 */
	Page<Brand> findPage(Tenant tenant, Pageable pageable);

	/**
	 * @Title：findList
	 * @Description：
	 * @param tenant
	 * @return  Object
	 */
	List<Brand> findList(Tenant tenant);

}