/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.entity.ProductChannel;
import net.wit.entity.TenantCategory;

/**
 * Dao - 企业分类
 * @author rsico Team
 * @version 3.0
 */
public interface TenantCategoryDao extends BaseDao<TenantCategory, Long> {

	/**
	 * 查找顶级企业分类
	 * @param count 数量
	 * @return 顶级企业分类
	 */
	List<TenantCategory> findRoots(Integer count);

	/**
	 * 查找上级企业分类
	 * @param productChannel
	 * @param productCategory 企业分类
	 * @param count 数量
	 * @return 上级企业分类
	 */
	List<TenantCategory> findParents(ProductChannel productChannel, TenantCategory tenantCategory, Integer count);

	/**
	 * 查找下级企业分类
	 * @param productCategory 企业分类
	 * @param count 数量
	 * @return 下级企业分类
	 */
	List<TenantCategory> findChildren(TenantCategory tenantCategory, Integer count);

	List<TenantCategory> findParents(TenantCategory tenantCategory, Integer count);

}