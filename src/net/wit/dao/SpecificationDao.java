/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.ProductCategory;
import net.wit.entity.Specification;
import net.wit.entity.Tenant;

/**
 * Dao - 规格
 * @author rsico Team
 * @version 3.0
 */
public interface SpecificationDao extends BaseDao<Specification, Long> {

	/**
	 * @Title：findPage @Description：
	 * @param productCategory
	 * @param tenant
	 * @param pageable
	 * @return Page<Specification>
	 */
	Page<Specification> findPage(ProductCategory productCategory, Tenant tenant, Pageable pageable);

	/**
	 * @Title：findList @Description：
	 * @param curproductCategory
	 * @return List<Specification>
	 */
	List<Specification> findList(ProductCategory curproductCategory, Tenant tenant);

}