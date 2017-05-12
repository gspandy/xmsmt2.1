/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao;

import java.util.List;

import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;

/**
 * Dao - 商家商品分类
 * @author rsico Team
 * @version 3.0
 */
public interface ProductCategoryTenantDao extends BaseDao<ProductCategoryTenant, Long> {

	/**
	 * 查找顶级商品分类
	 * @param count 数量
	 * @param tenant 商铺
	 * @return 顶级商品分类
	 */
	List<ProductCategoryTenant> findRoots(Integer count, Tenant tenant);

	/**
	 * 查找上级商品分类
	 * @param productCategory 商品分类
	 * @param count 数量
	 * @param tenant 商铺
	 * @return 上级商品分类
	 */
	List<ProductCategoryTenant> findParents(ProductCategoryTenant productCategoryTenant, Integer count, Tenant tenant);

	/**
	 * 查找下级商品分类
	 * @param productCategory 商品分类
	 * @param count 数量
	 * @param tenant 商铺
	 * @return 下级商品分类
	 */
	List<ProductCategoryTenant> findChildren(ProductCategoryTenant productCategoryTenant, Integer count, Tenant tenant);

	/**
	 * 查找顶级商品分类
	 * @param count 数量
	 * @param tenant 商家
	 * @return 顶级商品分类
	 */
	List<ProductCategoryTenant> findRoots(Tag tag, Integer count, Tenant tenant);

	/**
	 * @Title：findList @Description：
	 * @param tenant
	 * @param tags
	 * @param count
	 * @return List<ProductCategoryTenant>
	 */
	List<ProductCategoryTenant> findList(Tenant tenant, Integer count);

}