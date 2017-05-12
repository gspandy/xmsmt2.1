/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Brand;
import net.wit.entity.Tenant;

/**
 * Dao - 品牌
 * @author rsico Team
 * @version 3.0
 */
public interface BrandDao extends BaseDao<Brand, Long> {

	/**
	 * @Title：findPage @Description：
	 * @param tenant
	 * @param pageable
	 * @return Page<Brand>
	 */
	Page<Brand> findPage(Tenant tenant, Pageable pageable);

	/**
	 * @Title：findList @Description：
	 * @param tenant
	 * @return List<Brand>
	 */
	List<Brand> findList(Tenant tenant);

}